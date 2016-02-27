package edu.nju.classifier.searchAnalysis;


import edu.nju.classifier.common.HBaseConstant;
import edu.nju.classifier.hbaseUtil.HBaseDAO;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

/**
 * Created by margine on 16-2-27.
 */
public class Analysis {

    private final static String TABLENAME = "paper2";
    private Connection connection;
    private Table table;

    public void execute() {
        Configuration conf = new Configuration();
        try {
            connection = ConnectionFactory.createConnection(conf);
            Admin admin = connection.getAdmin();
            if (!admin.tableExists(TableName.valueOf(TABLENAME))) {
                System.err.println("No such table [" + TABLENAME + "]");
                System.exit(-1);
            }
            table = connection.getTable(TableName.valueOf(TABLENAME));
            ResultScanner scanner = HBaseDAO.scanAll(table);

            for (Result result : scanner) {
                String apaStr, mlaStr, chicagoStr;
                byte[] family = HBaseConstant.REGION_NAME.getBytes();
                apaStr = new String(result.getValue(family, HBaseConstant.APA.getBytes()));
                mlaStr = new String(result.getValue(family, HBaseConstant.MLA.getBytes()));
                chicagoStr = new String(result.getValue(family, HBaseConstant.CHICAGO.getBytes()));
            }

        } catch (Exception e) {
            System.err.println("an error occurs while read data: ");
            e.printStackTrace();
            System.exit(-2);
        }
    }

    public static void main(String[] args) {
        Analysis a = new Analysis();
        a.execute();
    }

}
