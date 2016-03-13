package edu.nju.paperCiteAnalysis.recommendation.search;

import edu.nju.classifier.hbaseUtil.HBaseDAO;
import edu.nju.paperCiteAnalysis.recommendation.common.Article;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.common.Inproceedings;
import edu.nju.paperCiteAnalysis.recommendation.common.PropertyConstant;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by margine on 16-3-12.
 */
public class DataHelper {
    private static final String TABLE_NAME = "cite_refine";
    private Table table;
    private Connection connection;
    private Configuration conf;

    private void setup(){
        conf = HBaseConfiguration.create();
        try {
            connection = ConnectionFactory.createConnection(conf);
            table = connection.getTable(TableName.valueOf(TABLE_NAME));
        } catch (IOException e) {
            System.out.println("Error: while set up database configuration " + e);
            System.exit(-1);
        }
    }

    private void cleanup(){
        try {
            table.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error: while clean up database connection " + e);
            System.exit(-1);
        }
    }

    protected List<Bibtex> getResults(Collection<String> keys){
        List<Bibtex> results = new ArrayList<Bibtex>();
        setup();
        try {
            for (String key: keys){
               Result result =  HBaseDAO.getRow(table, key);

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
                    results.add(bibtex);
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
                    results.add(bibtex);
                }
            }
        }catch (IOException e){
            System.err.println("Error: while get data from database " + e);
            System.exit(-2);
        }
        cleanup();
        return  results;
    }
}
