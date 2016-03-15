package edu.nju.paperCiteAnalysis.recommendation.score;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxy on 16-3-12.
 */
public class Tfidf {
    List<String[]> allTerms = new ArrayList<String[]>();
    String[] totalTerms;

    //计算checkTerm在totalTerms中出现次数的百分比
    private double tfCalculator(String checkTerm){
        double count = 0;

        for(String term: totalTerms){
            if(term.equalsIgnoreCase(checkTerm)){
                count++;
            }
        }

        double tf = count / totalTerms.length;
        return tf;
    }

    //所有标题中包含某个单词的个数
    private double idfCalculator(String checkTerm){
        double count = 0;

        for(String[] totalTerms : allTerms){
            for(String term : totalTerms){
                if(term.equalsIgnoreCase(checkTerm)){
                    count++;
                    break;
                }
            }
        }

        if(count == 0){
            return 0;
        }else{
            double rate = 1200 / count;
            return Math.log(rate);
        }
    }

    public double tfIdfCalculator(List<String[]> allTerms, String[] totalTerms, String checkTerm){
        this.allTerms = allTerms;
        this.totalTerms = totalTerms;

        double tf = tfCalculator(checkTerm);
        double idf = idfCalculator(checkTerm);
        double tfIdf = tf * idf;

        return tfIdf;
    }
}
