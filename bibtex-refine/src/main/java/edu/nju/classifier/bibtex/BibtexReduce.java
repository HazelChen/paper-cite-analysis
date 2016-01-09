package edu.nju.classifier.bibtex;

import edu.nju.classifier.common.PropertyConstant;
import edu.nju.classifier.hbaseUtil.HBaseDAO;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Created by margine on 16-1-6.
 */
public class BibtexReduce extends Reducer<Text, Text, Text, Text> {
    private MultipleOutputs<Text, Text> multipleOutputs;
    private Connection connection;
    private Table table;
    private String serparator = ":";
    private String startSymbol = "={";
    private String endSymbol = "},";
    private static int index = 0;

    @Override
    public void setup(Reducer.Context context) {
        multipleOutputs = new MultipleOutputs<Text, Text>(context);
        Configuration conf = new Configuration();
        try {
            connection = ConnectionFactory.createConnection(conf);
            Admin admin = connection.getAdmin();
            if (!admin.tableExists(TableName.valueOf("cite_refine"))) {
                ArrayList<String> columFamilies = new ArrayList<>(2);
                columFamilies.add(PropertyConstant.ARTICLE);
                columFamilies.add(PropertyConstant.INPROCEEDINGS);
                HBaseDAO.createTable("cite_refine", columFamilies);
            }
            table = connection.getTable(TableName.valueOf("cite_refine"));
        } catch (Exception e) {
            System.out.println("Error: while set up database configuration " + e);
            System.exit(-1);
        }
    }

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        StringBuffer buffer = new StringBuffer();
        for (Text val : values) {
            String bibtextCite = val.toString();
            CustomedHashMap map = new CustomedHashMap();

            /**get cite type*/
            if (bibtextCite.equals("") || !bibtextCite.contains("@")) {
                continue;
            }
            String type = bibtextCite.substring(bibtextCite.indexOf("@") + 1, bibtextCite.indexOf("{"));
            String typeValue = bibtextCite.substring(bibtextCite.indexOf("{") + 1, bibtextCite.indexOf(","));
            buffer.append(PropertyConstant.TYPE + serparator + type);
            buffer.append("\r\n");
            map.put(PropertyConstant.TYPE, type);

            /**get rest property*/
            bibtextCite = preProcess(bibtextCite);
            StringTokenizer tokenizer = new StringTokenizer(bibtextCite, endSymbol);
            while (tokenizer.hasMoreElements()) {
                String element = tokenizer.nextToken().trim();
                if (element.equals("") || !element.contains(startSymbol))
                    continue;

                String propertyName = element.substring(0, element.indexOf(startSymbol));
                String propertyValue = element.substring(element.indexOf(startSymbol) + startSymbol.length());

                map.put(propertyName, propertyValue);
                buffer.append(propertyName + serparator + propertyValue);
                buffer.append("\r\n");
            }
            writeDatabase(map);
            index++;
        }
        multipleOutputs.write(BibtexClassifier.OUTPUT_PATH, NullWritable.get(), buffer.toString());
    }

    @Override
    public void cleanup(Reducer.Context context) throws IOException, InterruptedException {
        table.close();
        connection.close();
        multipleOutputs.close();
    }

    private void writeDatabase(HashMap<String, String> val) throws IOException, InterruptedException {
        try {
            String rowIndex = "row" + index;
            String type = val.get(PropertyConstant.TYPE);
            if (type != null && type.equals(PropertyConstant.ARTICLE)) {
                HBaseDAO.putCell(table, rowIndex, PropertyConstant.ARTICLE, PropertyConstant.TITLE, val.get(PropertyConstant.TITLE));
                HBaseDAO.putCell(table, rowIndex, PropertyConstant.ARTICLE, PropertyConstant.AUTHOR, val.get(PropertyConstant.AUTHOR));
                HBaseDAO.putCell(table, rowIndex, PropertyConstant.ARTICLE, PropertyConstant.JOURNAL, val.get(PropertyConstant.JOURNAL));
                HBaseDAO.putCell(table, rowIndex, PropertyConstant.ARTICLE, PropertyConstant.YEAR, val.get(PropertyConstant.YEAR));
                HBaseDAO.putCell(table, rowIndex, PropertyConstant.ARTICLE, PropertyConstant.VOLUME, val.get(PropertyConstant.VOLUME));
                HBaseDAO.putCell(table, rowIndex, PropertyConstant.ARTICLE, PropertyConstant.PAGES, val.get(PropertyConstant.PAGES));

            } else if (type != null && type.equals(PropertyConstant.INPROCEEDINGS)) {
                HBaseDAO.putCell(table, rowIndex, PropertyConstant.INPROCEEDINGS, PropertyConstant.TITLE, val.get(PropertyConstant.TITLE));
                HBaseDAO.putCell(table, rowIndex, PropertyConstant.INPROCEEDINGS, PropertyConstant.AUTHOR, val.get(PropertyConstant.AUTHOR));
                if (val.containsKey(PropertyConstant.BOOKTITLE)) {
                    HBaseDAO.putCell(table, rowIndex, PropertyConstant.INPROCEEDINGS, PropertyConstant.BOOKTITLE, val.get(PropertyConstant.BOOKTITLE));
                }
                HBaseDAO.putCell(table, rowIndex, PropertyConstant.INPROCEEDINGS, PropertyConstant.YEAR, val.get(PropertyConstant.YEAR));
            }

        } catch (Exception e) {
            System.err.println("Error: insert data into HBase " + e);
            System.exit(-2);
        }
    }
    /**
     * format the string content by insert ‘,’ for easy split
     *
     * @param s
     * @return
     */
    private String preProcess(String s) {
        StringBuffer buffer = new StringBuffer(s);
        buffer.setLength(s.length() - 2);
        buffer.append(endSymbol);
        return buffer.substring(buffer.indexOf(",") + 1);
    }
}
