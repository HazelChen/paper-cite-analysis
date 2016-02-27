package edu.nju.paperCiteAnalysis.invertedIndex;

import edu.nju.classifier.common.InvertedIndexDBConstant;
import edu.nju.classifier.common.PropertyConstant;
import edu.nju.classifier.hbaseUtil.HBaseDAO;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hazel on 16-2-27.
 */
public class BuildInvertedIndexDBReducer
        extends TableReducer<Text, Text, ImmutableBytesWritable> {

    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        StringBuffer allValue = new StringBuffer();
        int count = 0;
        for (Text val : values) {
            allValue.append(val);
            count += getCount(val.toString());
        }

        Put put = new Put(Bytes.toBytes(key.toString()));
        put.addColumn(Bytes.toBytes(InvertedIndexDBConstant.COLUMN_FAMILY),
                Bytes.toBytes(InvertedIndexDBConstant.LOCATION_WITH_COUNT),
                Bytes.toBytes(allValue.toString()));
        put.addColumn(Bytes.toBytes(InvertedIndexDBConstant.COLUMN_FAMILY),
                Bytes.toBytes(InvertedIndexDBConstant.COUNT),
                Bytes.toBytes(count));

        context.write(null, put);
    }

    private int getCount(String value) {
        Pattern valuePattern = Pattern.compile("^<.*?,([0-9]+)>$");
        Matcher valueMatcher = valuePattern.matcher(value);
        if (!valueMatcher.find()) {
            throw new RuntimeException("Error in reduce : unknow value " + value);
        }
        return Integer.parseInt(valueMatcher.group(1));
    }

}
