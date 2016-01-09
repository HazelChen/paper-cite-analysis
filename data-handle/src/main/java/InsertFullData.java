import edu.nju.classifier.common.HBaseConstant;
import edu.nju.classifier.hbaseUtil.HBaseDAO;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by margine on 15-12-31.
 * function: insert data come from 7 teams
 */
public class InsertFullData {
    private static Map<String, String> encodingMaps;

    static {
        encodingMaps = new HashMap<String, String>();
        encodingMaps.put("apa-16.txt", "utf-16");
        encodingMaps.put("mla-16.txt", "utf-16");
        encodingMaps.put("chicago-16.txt", "utf-16");
        encodingMaps.put("apa-12.txt", "gb2312");
        encodingMaps.put("mla-12.txt", "gb2312");
        encodingMaps.put("chicago-12.txt", "gb2312");
        encodingMaps.put("bibtex-12.txt", "gb2312");
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.err.println("Usage: " + InsertFullData.class.getName() + " [table name] [data file directoryPath]");
            System.exit(-1);
        }

        File directory = new File(args[1]);
        if (!directory.isDirectory()) {
            System.err.println(args[1] + " is not a directory!");
            System.exit(-2);
        }
        File[] files = directory.listFiles();
        if (files == null) {
            System.err.println("Something error when reading " + args[1] + "!");
            System.exit(-2);
        }
        Arrays.sort(files, new Comparator< File>() {
            public int compare(File f1, File f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });

        Map<String, Integer> beginIndexes = new HashMap<String, Integer>();
        beginIndexes.put(HBaseConstant.BIBTEX, 0);
        beginIndexes.put(HBaseConstant.APA, 0);
        beginIndexes.put(HBaseConstant.CHICAGO, 0);
        beginIndexes.put(HBaseConstant.MLA, 0);

        try {
            Configuration conf = new Configuration();
            Connection connection = ConnectionFactory.createConnection(conf);
            HBaseDAO.createTable(args[0], HBaseConstant.REGION_NAME);
            Table table = connection.getTable(TableName.valueOf(args[0]));

            for (File file : files) {
                String fileName = file.getName();
                List<String> data = FileHelper.read(file, encodingMaps.get(fileName));

                String column = getColumn(fileName);
                boolean needClean = needClean(fileName);
                int beginIndex = beginIndexes.get(column);
                for (String row : data) {
                    if (row.equals("")) {
                        continue;
                    }
                    if (needClean) {
                        row = clean(row);
                    }
                    HBaseDAO.putCell(table, "row" + beginIndex,
                            HBaseConstant.REGION_NAME, column, row);
                    beginIndex++;
                }
                System.out.println("Scanning file: " + fileName + ", size: " + data.size() +
                        ", beginIndex: " + beginIndex);
                beginIndexes.put(column, beginIndex);
            }
            table.close();
        } catch (IOException e) {
            System.err.println("Error: insert data into HBase " + e);
            e.printStackTrace();
        }
    }

    private static String getColumn(String filename) {
        if (filename.contains(HBaseConstant.BIBTEX.toLowerCase())) {
            return HBaseConstant.BIBTEX;
        } else if (filename.contains(HBaseConstant.APA.toLowerCase())) {
            return HBaseConstant.APA;
        } else if (filename.contains(HBaseConstant.MLA.toLowerCase())) {
            return HBaseConstant.MLA;
        } else if (filename.contains(HBaseConstant.CHICAGO.toLowerCase())) {
            return HBaseConstant.CHICAGO;
        } else {
            throw new RuntimeException("Error file name:" + filename);
        }
    }

    private static boolean needClean(String filename) {
        return filename.contains("8");
    }

    private static String clean(String s) {
        if (!s.startsWith("r")) {
            return s;
        }

        return s.substring(s.indexOf(":") + 1);
    }
}
