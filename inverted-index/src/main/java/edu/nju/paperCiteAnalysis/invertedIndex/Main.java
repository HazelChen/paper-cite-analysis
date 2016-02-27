package edu.nju.paperCiteAnalysis.invertedIndex;

import edu.nju.classifier.common.InvertedIndexDBConstant;
import edu.nju.classifier.common.PropertyConstant;
import edu.nju.classifier.hbaseUtil.HBaseDAO;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hazel on 16-2-27.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.printf("Usage: " + Main.class.toString() +
                    " [source table name] [inverted index table name]");
            System.exit(-1);
        }

        Configuration config = HBaseConfiguration.create();
        Job job = null;
        try {
            job = Job.getInstance(config);
        } catch (IOException e) {
            System.err.printf("An error occurs when get job instance" + e);
            System.exit(-2);
        }
        job.setJarByClass(Main.class);     // class that contains mapper and reducer

        Scan scan = new Scan();
        scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
        scan.setCacheBlocks(false);  // don't set to true for MR jobs

        try {
            Connection connection = ConnectionFactory.createConnection(new Configuration());
            Admin admin = connection.getAdmin();
            if (!admin.tableExists(TableName.valueOf(args[1]))) {
                ArrayList<String> columFamilies = new ArrayList<>();
                columFamilies.add(InvertedIndexDBConstant.COLUMN_FAMILY);
                HBaseDAO.createTable(args[1], columFamilies);
            }
        } catch (Exception e) {
            System.err.println("Error: while set up database configuration " + e);
            System.exit(-1);
        }

        try {
            TableMapReduceUtil.initTableMapperJob(
                    args[0],        // input table
                    scan,               // Scan instance to control CF and attribute selection
                    ExtractWordsMapper.class,     // mapper class
                    Text.class,         // mapper output key
                    Text.class,  // mapper output value
                    job);
            TableMapReduceUtil.initTableReducerJob(
                    args[1],        // output table
                    BuildInvertedIndexDBReducer.class,    // reducer class
                    job);
        } catch (IOException e) {
            System.err.println("An error occurs when init table mapper/reducer job：" + e);
            System.exit(-3);
        }
        job.setNumReduceTasks(1);   // at least one, adjust as required

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
