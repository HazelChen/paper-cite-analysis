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
            List<String> titleWords = split(articleTitleByte);
            List<String> authorWords = split(
                    getValue(value, PropertyConstant.ARTICLE, PropertyConstant.AUTHOR));
            List<String> journalWords = split(
                    getValue(value, PropertyConstant.ARTICLE, PropertyConstant.JOURNAL));
            List<String> yearWords = split(
                    getValue(value, PropertyConstant.ARTICLE, PropertyConstant.YEAR));

            int count = 0;
            count += titleWords == null ? 0 : titleWords.size();
            count += journalWords == null ? 0 : journalWords.size();
            count += yearWords == null ? 0 : yearWords.size();

            writeToContext(titleWords, count, rowKey, context);
            writeToContext(authorWords, count, rowKey, context);
            writeToContext(journalWords, count, rowKey, context);
            writeToContext(yearWords, count, rowKey, context);
        } else {
            //Is inproceedings
            List<String> titleWords = split(
                    getValue(value, PropertyConstant.INPROCEEDINGS, PropertyConstant.TITLE));
            List<String> authorWords = split(
                    getValue(value, PropertyConstant.INPROCEEDINGS, PropertyConstant.AUTHOR));
            List<String> bookTitleWords = split(
                    getValue(value, PropertyConstant.INPROCEEDINGS, PropertyConstant.BOOKTITLE));
            List<String> yearWords = split(
                    getValue(value, PropertyConstant.INPROCEEDINGS, PropertyConstant.YEAR));

            int count = 0;
            count += titleWords == null ? 0 : titleWords.size();
            count += authorWords == null ? 0 : authorWords.size();
            count += bookTitleWords == null ? 0 : bookTitleWords.size();
            count += yearWords == null ? 0 : yearWords.size();

            writeToContext(titleWords, count, rowKey, context);
            writeToContext(authorWords, count, rowKey, context);
            writeToContext(bookTitleWords, count, rowKey, context);
            writeToContext(yearWords, count, rowKey, context);

        }
    }

    private byte[] getValue(Result value, String columnFamily, String column) {
        return value.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
    }

    private List<String> split(byte[] sentence) {
        if (sentence == null) {
            return null;
        }
        String sentenceString = new String(sentence);

        List<String> words = tokenAnalyzer.wordSplit(sentenceString);
        System.out.println(sentenceString + ": " + words);
        return words;
    }

    private void writeToContext(List<String> words, int allCount,
                                String rowKey, Context context)
                                    throws IOException, InterruptedException {
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
                    wordAndCount.getValue() * 100 / allCount + ">";
            context.write(new Text(wordAndCount.getKey()), new Text(contextValue));
        }
    }

}
