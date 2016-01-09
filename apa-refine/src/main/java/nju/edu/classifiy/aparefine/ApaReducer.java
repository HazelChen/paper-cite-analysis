package nju.edu.classifiy.aparefine;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.IOException;

/**
 * Created by nathan on 16-1-6.
 */
public class ApaReducer extends Reducer<Text, Text, Text, Text>{
        private MultipleOutputs<Text, Text> multipleOutputs;

        @Override
        public void setup(Context context) {
            multipleOutputs = new MultipleOutputs<Text, Text>(context);
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            StringBuffer stringBuffer = new StringBuffer();
            for (Text val : values) {
                stringBuffer.append(val.toString());
            }

            multipleOutputs.write(ApaRefine.OUTPUT_PATH, NullWritable.get(), stringBuffer.toString());
        }

        @Override
        public void cleanup(Context context) throws IOException, InterruptedException {
            multipleOutputs.close();
        }

}
