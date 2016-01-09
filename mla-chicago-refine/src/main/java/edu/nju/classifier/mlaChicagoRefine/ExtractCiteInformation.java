package edu.nju.classifier.mlaChicagoRefine;

import edu.nju.classifier.common.HBaseConstant;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hazel on 15-12-31.
 */
public class ExtractCiteInformation extends TableMapper<Text, Text> {
    private final Pattern ARTICLE_PATTERN =
            Pattern.compile(
                    "^(.*?)\\.\\s“(.*?)”\\s(.*?)(\\S+)\\s\\(([0-9]+)\\):\\s(.*?).$"
            );

    private final Pattern INPROCEEDINGS_PATTERN =
            Pattern.compile(
                    "^(.*?)\\.\\s“(.*?)”\\s(.*?)\\s\\(([0-9]+)\\).$"
            );

    private final Pattern INPROCEEDINGS_PATTERN_II =
            Pattern.compile(
                    "^(.*?)\\.\\s“(.*?)”\\s\\(([0-9]+)\\).$"
            );

    public void map(ImmutableBytesWritable row, Result value, Context context)
            throws InterruptedException, IOException {

        byte[] mlaResult = value.getValue(
                Bytes.toBytes(HBaseConstant.REGION_NAME),
                Bytes.toBytes(HBaseConstant.MLA));
        if (mlaResult != null) {
            String mla = new String(mlaResult, "utf-8");
            if (mla.contains("“")) {
                doMapper(mla, new Text(HBaseConstant.MLA + ":" + row.toString()), context);
            }
        }
        byte[] chicagoResult = value.getValue(
                Bytes.toBytes(HBaseConstant.REGION_NAME),
                Bytes.toBytes(HBaseConstant.CHICAGO));
        if (chicagoResult != null) {
            String chicago = new String(chicagoResult, "utf-8");
            if (chicago.contains("“")) {
                doMapper(chicago, new Text(HBaseConstant.CHICAGO + ":" + row.toString()), context);
            }
        }

    }

    private void doMapper(String data, Text rowKeyText, Context context)
            throws InterruptedException, IOException {
        Matcher articleMatcher = ARTICLE_PATTERN.matcher(data);
        Matcher inproceedingsMatcher = INPROCEEDINGS_PATTERN.matcher(data);
        Matcher inproceedingsMatcher2 = INPROCEEDINGS_PATTERN_II.matcher(data);

        try {
            if (articleMatcher.find()) {
                context.write(rowKeyText, new Text("type: article"));
                context.write(rowKeyText, new Text("author: " + articleMatcher.group(1)));
                context.write(rowKeyText, new Text("title: " + articleMatcher.group(2)));
                context.write(rowKeyText, new Text("journal: " + articleMatcher.group(3)));
                context.write(rowKeyText, new Text("volume: " + articleMatcher.group(4)));
                context.write(rowKeyText, new Text("year: " + articleMatcher.group(5)));
                context.write(rowKeyText, new Text("pages: " + articleMatcher.group(6)));
            } else if (inproceedingsMatcher.find()) {
                context.write(rowKeyText, new Text("type: inproceedings"));
                context.write(rowKeyText, new Text("author: " + inproceedingsMatcher.group(1)));
                context.write(rowKeyText, new Text("title: " + inproceedingsMatcher.group(2)));
                context.write(rowKeyText, new Text("booktitle: " + inproceedingsMatcher.group(3)));
                context.write(rowKeyText, new Text("year: " + inproceedingsMatcher.group(4)));
            } else if (inproceedingsMatcher2.find()) {
                context.write(rowKeyText, new Text("type: inproceedings"));
                context.write(rowKeyText, new Text("author: " + inproceedingsMatcher2.group(1)));
                context.write(rowKeyText, new Text("title: " + inproceedingsMatcher2.group(2)));
                context.write(rowKeyText, new Text("year: " + inproceedingsMatcher2.group(3)));
            } else {
                throw new RuntimeException("An new type find: " + data);
            }
        } catch (IllegalStateException e) {
            throw new RuntimeException("Can not resolve: [" + data + "]");
        }
    }

}
