package edu.nju.paperCiteAnalysis.invertedIndex;

import edu.nju.classifier.common.PropertyConstant;
import edu.nju.tokenAnalyzer.TokenAnalyzer;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.*;

/**
 * Created by hazel on 16-2-27.
 */
public class ExtractWordsMapper extends TableMapper<Text, Text> {
    private TokenAnalyzer tokenAnalyzer = new TokenAnalyzer();

    public void map(ImmutableBytesWritable row, Result value, Context context)
            throws InterruptedException, IOException {

        String rowKey = new String(row.get());

        byte[] articleTitleByte = value.getValue(
                Bytes.toBytes(PropertyConstant.ARTICLE),
                Bytes.toBytes(PropertyConstant.TITLE));

        if (articleTitleByte != null) {
            //Is article
            participleAndWriteToContext(articleTitleByte, rowKey, context);
            participleAndWriteToContext(
                    getValue(value, PropertyConstant.ARTICLE, PropertyConstant.AUTHOR),
                    rowKey, context);
            participleAndWriteToContext(
                    getValue(value, PropertyConstant.ARTICLE, PropertyConstant.JOURNAL),
                    rowKey, context);
            participleAndWriteToContext(
                    getValue(value, PropertyConstant.ARTICLE, PropertyConstant.YEAR),
                    rowKey, context);
        } else {
            //Is inproceedings
            participleAndWriteToContext(
                    getValue(value, PropertyConstant.INPROCEEDINGS, PropertyConstant.TITLE),
                    rowKey, context);
            participleAndWriteToContext(
                    getValue(value, PropertyConstant.INPROCEEDINGS, PropertyConstant.AUTHOR),
                    rowKey, context);
            participleAndWriteToContext(
                    getValue(value, PropertyConstant.INPROCEEDINGS, PropertyConstant.BOOKTITLE),
                    rowKey, context);
            participleAndWriteToContext(
                    getValue(value, PropertyConstant.INPROCEEDINGS, PropertyConstant.YEAR),
                    rowKey, context);
        }
    }

    private byte[] getValue(Result value, String columnFamily, String column) {
        byte[] bytes = value.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
//        if (bytes == null) {
//            throw new RuntimeException(
//                    "Error: Null sentence input with column family: " + columnFamily
//                            + " column: " + column);
//        }
        return bytes;
    }

    private void participleAndWriteToContext(byte[] sentence, String rowKey, Context context)
            throws IOException, InterruptedException {
        if (sentence == null) {
            return;
        }
        String sentenceString = new String(sentence);

        List<String> words = tokenAnalyzer.wordSplit(sentenceString);
        System.out.println(sentenceString + ": " + words);

        Map<String, Integer> wordCountMap = new HashMap<String, Integer>();
        for (String word : words) {
            Integer count = wordCountMap.get(word);
            if (count == null) {
                wordCountMap.put(word, 1);
            } else {
                wordCountMap.put(word, count + 1);
            }
        }

        for (Map.Entry<String, Integer> wordAndCount : wordCountMap.entrySet()) {
            String contextValue = "<" + rowKey + "," +
                    wordAndCount.getValue() * 100 / words.size() + ">";
            context.write(new Text(wordAndCount.getKey()), new Text(contextValue));
        }
    }

}
