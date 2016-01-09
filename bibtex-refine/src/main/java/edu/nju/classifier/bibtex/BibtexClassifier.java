package edu.nju.classifier.bibtex;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by margine on 16-1-2.
 */
public class BibtexClassifier {
    public static final String OUTPUT_PATH = "bibtexRefine";
    public static void main(String[] args){
        if (args.length != 2) {
            System.err.printf("Usage: " + BibtexClassifier.class.toString() + " [tablename] [output path]");
            System.exit(-1);
        }
        Configuration conf = HBaseConfiguration.create();
        Job job = null;
        try {
            job = Job.getInstance(conf);
        } catch (IOException e) {
            System.err.printf("An error occurs when get job instance" + e);
            System.exit(-2);
        }

        job.setJarByClass(BibtexClassifier.class);
        job.setJobName("bibtex");

        job.setMapperClass(BibtexMap.class);
        job.setReducerClass(BibtexReduce.class);

        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        Scan scan = new Scan();
        scan.setCaching(500);
        scan.setCacheBlocks(false);

        try {
            TableMapReduceUtil.initTableMapperJob(
                    args[0],
                    scan,
                    BibtexMap.class,
                    Text.class,
                    Text.class,
                    job
            );
        } catch (IOException e) {
            System.err.println("An error occurs when init table mapper job：" + e);
            System.exit(-3);
        }

        Path outputPath = new Path(args[1]);
        FileOutputFormat.setOutputPath(job, outputPath);

        LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);

        MultipleOutputs.addNamedOutput(job,
                OUTPUT_PATH, TextOutputFormat.class,
                NullWritable.class, Text.class);

        try {
            FileSystem fileSystem = FileSystem.get(conf);
            if (fileSystem.exists(outputPath)) {
                fileSystem.delete(outputPath, true);
            }
        } catch (IOException e) {
            System.err.println("An error occurs when deleting old output files: " + e);
            System.exit(-4);
        }

        try {
            boolean result = job.waitForCompletion(true);
            if (!result) {
                System.err.println("An error occurs while running...");
            }
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            System.err.println("An error occurs when job execution: " + e);
            System.exit(-5);
        }
    }
}
