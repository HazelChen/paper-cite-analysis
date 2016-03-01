package edu.nju.classifier.searchEngine;

import edu.nju.classifier.common.*;
import edu.nju.classifier.hbaseUtil.HBaseDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

/**
 * Created by nathan on 16-2-29.
 */
public class BibtexFactory {

    private static Table table;
    private static Configuration conf;
    private static Connection connection;

    private static void setup() {
        conf = HBaseConfiguration.create();
        try {
            connection = ConnectionFactory.createConnection(conf);
            table = connection.getTable(TableName.valueOf(PropertyConstant.TABLENAME));
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

    public static List<Bibtex> fetchResultList(List<MatchScore> orderedList, int num) {
        Set<Bibtex> bibtexList = new LinkedHashSet<Bibtex>();
        if(CollectionUtils.isEmpty(orderedList))
            return null;

        setup();
        try{
            int count = 0;
            while(bibtexList.size() != num) {
                if(orderedList.size() < num && count==orderedList.size())
                    break;
                MatchScore ms = orderedList.get(count++);
                Result result = HBaseDAO.getRow(table, ms.getRowKey());

                byte[] articleTitleByte = result.getValue(
                        Bytes.toBytes(PropertyConstant.ARTICLE),
                        Bytes.toBytes(PropertyConstant.TITLE));
                if(articleTitleByte != null) {
                    Article bibtex = new Article();
                    bibtex.setTitle(Bytes.toString(articleTitleByte));
                    bibtex.setAuthor(Bytes.toString(result.getValue(
                            Bytes.toBytes(PropertyConstant.ARTICLE),
                            Bytes.toBytes(PropertyConstant.AUTHOR))));
                    bibtex.setYear(Bytes.toString(result.getValue(
                            Bytes.toBytes(PropertyConstant.ARTICLE),
                            Bytes.toBytes(PropertyConstant.YEAR))));
                    bibtex.setJournal(Bytes.toString(result.getValue(
                            Bytes.toBytes(PropertyConstant.ARTICLE),
                            Bytes.toBytes(PropertyConstant.JOURNAL))));
                    bibtex.setPages(Bytes.toString(result.getValue(
                            Bytes.toBytes(PropertyConstant.ARTICLE),
                            Bytes.toBytes(PropertyConstant.PAGES))));
                    bibtex.setVolume(Bytes.toString(result.getValue(
                            Bytes.toBytes(PropertyConstant.ARTICLE),
                            Bytes.toBytes(PropertyConstant.VOLUME))));
                    bibtexList.add(bibtex);
                }else {
                    Inproceedings bibtex = new Inproceedings();
                    bibtex.setTitle(Bytes.toString(result.getValue(
                            Bytes.toBytes(PropertyConstant.INPROCEEDINGS),
                            Bytes.toBytes(PropertyConstant.TITLE))));
                    bibtex.setAuthor(Bytes.toString(result.getValue(
                            Bytes.toBytes(PropertyConstant.INPROCEEDINGS),
                            Bytes.toBytes(PropertyConstant.AUTHOR))));
                    bibtex.setBooktitle(Bytes.toString(result.getValue(
                            Bytes.toBytes(PropertyConstant.INPROCEEDINGS),
                            Bytes.toBytes(PropertyConstant.BOOKTITLE))));
                    bibtex.setYear(Bytes.toString(result.getValue(
                            Bytes.toBytes(PropertyConstant.INPROCEEDINGS),
                            Bytes.toBytes(PropertyConstant.YEAR))));
                    bibtexList.add(bibtex);
                }

            }
        }catch (Exception e) {
            System.out.println("Error: while fetch the bibtex data " + e);
            e.printStackTrace();
            System.exit(-1);
        }

        cleanup();

        List<Bibtex> result = new ArrayList<Bibtex>(bibtexList);
        return result;
    }
}
