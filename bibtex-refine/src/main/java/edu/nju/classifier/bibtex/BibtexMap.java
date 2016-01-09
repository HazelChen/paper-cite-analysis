package edu.nju.classifier.bibtex;

import edu.nju.classifier.common.HBaseConstant;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * Created by margine on 16-1-6.
 */
public class BibtexMap extends TableMapper<Text, Text> {
    public void map(ImmutableBytesWritable row, Result value, Context context) throws InterruptedException, IOException {
        byte[] result = value.getValue(Bytes.toBytes(HBaseConstant.REGION_NAME), Bytes.toBytes(HBaseConstant.BIBTEX));
        if (result == null) {
            return;
        }
        Text text = new Text(new String(result));
        Text key = new Text(row.toString());
        Text rowIndex = new Text(row.toString());
        context.write(rowIndex, text);
    }
}
