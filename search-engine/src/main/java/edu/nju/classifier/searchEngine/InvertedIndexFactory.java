package edu.nju.classifier.searchEngine;

import edu.nju.classifier.common.InvertedIndexDBConstant;
import edu.nju.classifier.hbaseUtil.HBaseDAO;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

/**
 * Created by nathan on 16-2-29.
 */
public class InvertedIndexFactory {

    private static final String TABLE_NAME = "inverted_index";

    private static Table table;
    private static Configuration conf;
    private static Connection connection;

    private static void setup() {
        conf = HBaseConfiguration.create();
        try {
            connection = ConnectionFactory.createConnection(conf);
            table = connection.getTable(TableName.valueOf(TABLE_NAME));
        } catch (IOException e) {
            System.out.println("Error: while set up database configuration " + e);
            System.exit(-1);
        }
    }

    private static void cleanup() {
        try {
            table.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error: while clean up database connection " + e);
            System.exit(-1);
        }
    }

    public static Map<String, List<String>> createInvertedIndex() {
        Map<String, List<String>> invertedIndex = new HashMap<String, List<String>>();

        System.out.println("start creating inverted-index.................");
        setup();
        try {
            ResultScanner results = HBaseDAO.scanAll(table);
            for(Result r : results){
                String word = Bytes.toString(r.getRow());
                String info = Bytes.toString(r.getValue(
                        Bytes.toBytes(InvertedIndexDBConstant.COLUMN_FAMILY),
                        Bytes.toBytes(InvertedIndexDBConstant.LOCATION_WITH_COUNT)));
                String countStr = Bytes.toString(r.getValue(
                        Bytes.toBytes(InvertedIndexDBConstant.COLUMN_FAMILY),
                        Bytes.toBytes(InvertedIndexDBConstant.COUNT)));
                //int count = Integer.parseInt(countStr);

                String[] tmp = info.trim().substring(1, info.length()-1).split("><");
                List<String> locaWithCounts = Arrays.asList(tmp);
                invertedIndex.put(word, locaWithCounts);
            }
        }catch (Exception e) {
            System.out.println("Error: while fetch the data " + e);
            System.exit(-1);
        }
        cleanup();
        System.out.println("finish creating inverted-index.................");

        return invertedIndex;
    }
}
