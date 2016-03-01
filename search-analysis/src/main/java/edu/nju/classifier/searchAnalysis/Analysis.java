package edu.nju.classifier.searchAnalysis;


import edu.nju.classifier.common.Article;
import edu.nju.classifier.common.Bibtex;
import edu.nju.classifier.common.HBaseConstant;
import edu.nju.classifier.common.Inproceedings;
import edu.nju.classifier.formatExtract.Format;
import edu.nju.classifier.hbaseUtil.HBaseDAO;
import edu.nju.classifier.searchEngine.SeachEngine;
import edu.nju.dataHandle.FileHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margine on 16-2-27.
 */
public class Analysis {

    private String noHitFilePath = "output/error.txt";
    private String resultFilePath = "output/result.txt";
    /*search count*/
    private static int count = 0;
    private static double averageAccuracy = 0.0;

    private final static String TABLENAME = "paper2";
    private Connection connection;
    private Table table;

    private int fullScoreInp = 4;
    private int fullScoreArt = 6;
    private double threshhold = 0.3;

    private List<Double> apaAccuracies = new ArrayList<Double>();
    private List<Double> mlaAccuracies = new ArrayList<Double>();
    private List<Double> chicagoAccuracies = new ArrayList<Double>();

    private SeachEngine seachEngine = new SeachEngine();

    public void execute(int k) {
        Configuration conf = new Configuration();
        String apaStr = null, mlaStr = null, chicagoStr = null;
        try {
            connection = ConnectionFactory.createConnection(conf);
            Admin admin = connection.getAdmin();
            if (!admin.tableExists(TableName.valueOf(TABLENAME))) {
                System.err.println("No such table [" + TABLENAME + "]");
                System.exit(-1);
            }
            table = connection.getTable(TableName.valueOf(TABLENAME));
            ResultScanner scanner = HBaseDAO.scanAll(table);

            int apaSize = 0, mlaSize = 0, chicagoSize = 0;
            double apaAccuracy = 0.0, mlaAccuracy = 0.0, chicagoArruracy = 0.0;
            for (Result result : scanner) {
                byte[] family = HBaseConstant.REGION_NAME.getBytes();
                if (result.containsColumn(family, HBaseConstant.APA.getBytes())) {
                    apaSize++;
                    apaStr = new String(result.getValue(family, HBaseConstant.APA.getBytes()));
                    apaAccuracy += doAnalysis(apaStr, k, Format.formartAPA(apaStr));
                }
                if (result.containsColumn(family, HBaseConstant.MLA.getBytes())) {
                    mlaSize++;
                    mlaStr = new String(result.getValue(family, HBaseConstant.MLA.getBytes()));
                    mlaAccuracy += doAnalysis(mlaStr, k, Format.formartMC(mlaStr));
                }
                if (result.containsColumn(family, HBaseConstant.CHICAGO.getBytes())) {
                    chicagoSize++;
                    chicagoStr = new String(result.getValue(family, HBaseConstant.CHICAGO.getBytes()));
                    chicagoArruracy += doAnalysis(chicagoStr, k, Format.formartMC(chicagoStr));
                }
            }
            ArrayList<String> contents = new ArrayList<String>();
            contents.add("apa average accuracy: " + (apaAccuracy / apaSize));
            contents.add("mla average accuracy: " + (mlaAccuracy / mlaSize));
            contents.add("chicago average accuracy: " + (chicagoArruracy / chicagoSize));
            FileHelper.append(contents, resultFilePath);

        } catch (Exception e) {
            System.err.println("an error occurs while reading query data: ");
            System.err.println("apa:" + apaStr);
            e.printStackTrace();
            System.exit(-2);
        }
    }


    private double doAnalysis(String queryStr, int k, Bibtex validate) {
        if (validate == null) {
            return 0.0;
        }
        count++;
        System.out.println(count);
        List<Bibtex> results = seachEngine.search(queryStr, k);

        double accuracy = 0.0;
        int i = 0;
        int hitIndex = -1;

        if (CollectionUtils.isNotEmpty(results)) {
            for (Bibtex one : results) {
                if (one.equals(validate)) {
                    hitIndex = i;
                    accuracy = (k - i) / (double) k;
                    break;
                }
                i++;
            }
        }
        if (hitIndex == -1) {
            FileHelper.append("item " + count + " :No hit, search fail. The query string is : " + queryStr, noHitFilePath);
            FileHelper.append(results.toString()+"\r\n", noHitFilePath);
        } else {
            List<String> contents = new ArrayList<String>();
//
            contents.add("item " + count + " :search with " + k + " result(s) accuracy: " + accuracy);
            FileHelper.append(contents, resultFilePath);
        }
        averageAccuracy = (averageAccuracy * (count - 1) + accuracy) / (double) count;
        return accuracy;
    }

    private void doAnalysis2(String queryStr, int k, Bibtex validate) {
        if (validate == null) {
            return;
        }
        count++;
        //TODO call search method here
        List<Bibtex> results = Search.DoSearch(queryStr, k);
        if (results == null || results.size() < k) {
            System.err.println("an error occurs while searching...\n caused by the number of result is less than " + k);
            System.exit(-4);
        }

        double total = 0.0;
        int i = 0;
        for (Bibtex one : results) {
            double accuracy = 0.0;
            accuracy += similarDegree(one.getTitle(), validate.getTitle()) * Weight.TITLE_WEIGHT;
            accuracy += similarDegree(one.getYear(), validate.getYear()) * Weight.YEAR_WEIGHT;
//            accuracy += similarDegree(one.getAuthor(), validate.getAuthor()) * Weight.AUTHOR_WEIGHT;

            if (one instanceof Inproceedings && validate instanceof Inproceedings) {
                accuracy += Weight.TYPE_WEIGHT;
                accuracy += similarDegree(((Inproceedings) one).getBooktitle(), ((Inproceedings) validate).getBooktitle()) * Weight.BOOKTITLE_WEIGHT;
                accuracy /= Double.valueOf(Weight.INPROCCEDING_TOTAL_WEIGHT);
            } else if (one instanceof Article && validate instanceof Article) {
                accuracy += Weight.TYPE_WEIGHT;
                accuracy += similarDegree(((Article) one).getPages(), ((Article) validate).getPages()) * Weight.PAGES_WEIGHT;
                accuracy += similarDegree(((Article) one).getJournal(), ((Article) validate).getJournal()) * Weight.JOURNAL_WEIGHT;
                accuracy += similarDegree(((Article) one).getVolume(), ((Article) validate).getVolume()) * Weight.VOLUME_WEIGHT;
                accuracy /= Double.valueOf(Weight.ARTICLE_TOTAL_WEIGHT);
            } else {
                accuracy = 0.0;
            }
            if (accuracy > threshhold) {
                System.out.println(count + ": hit result index " + (i + 1) + " with accuracy: " + accuracy);
            }
            accuracy *= (k - i) / k;
            total += accuracy;
            i++;
        }
        System.out.println(count + ": total " + total);
        averageAccuracy = (averageAccuracy * (count - 1) + total) / (double) count;
    }

    public double similarDegree(String source, String target) {
        String s1 = Util.removeSign(source);
        String s2 = Util.removeSign(target);
        int temp = Math.max(s1.length(), s2.length());
        int temp2 = Util.longestCommonSubstring(s1, s2).length();
        return temp2 * 1.0 / temp;

    }

    public void execute2(int k) {
        ArrayList<String> test = new ArrayList<String>();

        String apa1 = "Brin, S., & Page, L.. (1998). Reprint of: The anatomy of a large-scale hypertextual web search engine. Computer Networks, 56, 3825-3833.";
        String apa2 = "Santini, S.. (2006). Video Search. MM.";
        String apa3 = "Liang, Z., Liu, Y., & Zhu, S.. (2013). Transductive multi-distance learning for video search. Pattern Anal. Appl., 16, 117-124.";

        String mla1 = "Brin, Sergey and Lawrence Page. “Reprint of: The anatomy of a large-scale hypertextual web search engine.” Computer Networks 56 (1998): 3825-3833.";
        String mla2 = "Santini, Simone. “Video Search.” MM (2006).";
        String mla3 = "Zhu, Songhao et al. “Transductive multi-distance learning for video search.” Pattern Anal. Appl. 16 (2013): 117-124.";

        String chicago1 = "Brin, Sergey and Lawrence Page. “Reprint of: The anatomy of a large-scale hypertextual web search engine.” Computer Networks 56 (1998): 3825-3833.";
        String chicago2 = "Santini, Simone. “Video Search.” MM (2006).";
        String chicago3 = "Zhu, Songhao, Zhiwei Liang and Yuncai Liu. “Transductive multi-distance learning for video search.” Pattern Anal. Appl. 16 (2013): 117-124.";

        test.add(apa1);
        test.add(apa2);
        test.add(apa3);

        test.add(mla1);
        test.add(mla2);
        test.add(mla3);

        test.add(chicago1);
        test.add(chicago2);
        test.add(chicago3);

        ArrayList<Bibtex> bibtices = new ArrayList<Bibtex>();
        Bibtex bibtex1 = Format.formartAPA(apa1);
        Bibtex bibtex2 = Format.formartAPA(apa2);
        Bibtex bibtex3 = Format.formartAPA(apa3);

        Bibtex bibtex4 = Format.formartMC(mla1);
        Bibtex bibtex5 = Format.formartMC(mla2);
        Bibtex bibtex6 = Format.formartMC(mla3);

        Bibtex bibtex7 = Format.formartMC(chicago1);
        Bibtex bibtex8 = Format.formartMC(chicago2);
        Bibtex bibtex9 = Format.formartMC(chicago3);

        bibtices.add(bibtex1);
        bibtices.add(bibtex2);
        bibtices.add(bibtex3);
        bibtices.add(bibtex4);
        bibtices.add(bibtex5);
        bibtices.add(bibtex6);
        bibtices.add(bibtex7);
        bibtices.add(bibtex8);
        bibtices.add(bibtex9);

        for (int i = 0; i < test.size(); i++) {
            doAnalysis(test.get(i), k, bibtices.get(i));
        }
        FileHelper.append("average accuracy: " + averageAccuracy, resultFilePath);
    }

}
