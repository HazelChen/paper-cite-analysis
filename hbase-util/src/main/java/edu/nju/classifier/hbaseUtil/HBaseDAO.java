package edu.nju.classifier.hbaseUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by gyq on 2015/12/23.
 */
public class HBaseDAO {

    public static Configuration conf;

    static {
        conf = HBaseConfiguration.create();
    }

    public static void createTable(String tablename, String columnFamily)
            throws IOException {
        Connection connection = ConnectionFactory.createConnection(conf);
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Admin admin = connection.getAdmin();
        try {
            if (admin.tableExists(name)) {
                System.exit(0);
            } else {
                HTableDescriptor tableDesc = new HTableDescriptor(name);
                tableDesc.addFamily(new HColumnDescriptor(columnFamily));
                admin.createTable(tableDesc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            table.close();
            admin.close();
            connection.close();
        }
    }

    public static void createTable(String tablename, ArrayList<String> columnFamilies)
            throws IOException {
        Connection connection = ConnectionFactory.createConnection(conf);
        TableName name = TableName.valueOf(tablename);
        Table table = connection.getTable(name);
        Admin admin = connection.getAdmin();
        try {
            if (admin.tableExists(name)) {
                System.exit(0);
            } else {
                HTableDescriptor tableDesc = new HTableDescriptor(name);
                for (int i = 0; i< columnFamilies.size(); i++ ) {
                    tableDesc.addFamily(new HColumnDescriptor(columnFamilies.get(i)));
                }
                admin.createTable(tableDesc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            table.close();
            admin.close();
            connection.close();
        }
    }

    public static boolean deleteTable(String tablename) throws IOException {
        Connection connection = ConnectionFactory.createConnection(conf);
        TableName name = TableName.valueOf(tablename);
        Admin admin = connection.getAdmin();
        try {
            if (admin.tableExists(name)) {
                admin.disableTable(name);
                admin.deleteTable(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            admin.close();
            connection.close();
        }
        return true;
    }

    public static void putCell(Table table, String rowKey,
                               String columnFamily, String identifier, String data)
            throws IOException {
        Put p = new Put(Bytes.toBytes(rowKey));
        p.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(identifier), Bytes.toBytes(data));
        table.put(p);
    }

    public static Result getRow(Table table, String rowKey) throws IOException {
        Get get = new Get(Bytes.toBytes(rowKey));
        Result result = table.get(get);
        return result;
    }

    public static void deleteRow(Table table, String rowKey) throws IOException {
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        table.delete(delete);
    }

    public static ResultScanner scanAll(Table table) throws IOException {
        Scan s = new Scan();
        ResultScanner rs = table.getScanner(s);
        return rs;
    }

    public static ResultScanner scanRange(Table table, String startrow, String endrow)
            throws IOException {
        Scan s = new Scan(Bytes.toBytes(startrow), Bytes.toBytes(endrow));
        ResultScanner rs = table.getScanner(s);
        return rs;
    }

    public static ResultScanner scanFilter(Table table, String startrow, Filter filter)
            throws IOException {
        Scan s = new Scan(Bytes.toBytes(startrow), filter);
        ResultScanner rs = table.getScanner(s);
        return rs;
    }
}