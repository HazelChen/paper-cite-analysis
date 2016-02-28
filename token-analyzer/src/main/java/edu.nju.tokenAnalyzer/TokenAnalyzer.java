package edu.nju.tokenAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zxy on 16-2-27.
 */
public class TokenAnalyzer {
    private static final String[] ENGLISH_STOP_WORDS = {
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with"
    };

    public List<String> wordSplit(String inputString){
        //将所有标点转换成空格后进行分割
        Pattern pattern = Pattern.compile("[.,\"\\?!:'/{}=~\\-\\+;@_#%&()“”]");
        Matcher matcher = pattern.matcher(inputString);
        String cleanString = matcher.replaceAll(" ");

        String[] resultArray = cleanString.split(" +");

        //将单词转换成下小写后，过滤无效词汇
        List<String> result = new ArrayList<String>();
        for(String s: resultArray){
            String standardString = s.toLowerCase();
            boolean isStopWord = false;

            for(int i = 0;i < ENGLISH_STOP_WORDS.length; i++){
                if(ENGLISH_STOP_WORDS[i].equals(standardString)){
                    isStopWord = true;
                }
            }

            if(!isStopWord){
                result.add(standardString);
//                System.out.println(standardString);
            }
        }

        return result;
    }

    public static void main(String[] args){
        TokenAnalyzer tokenAnalyzer = new TokenAnalyzer();
        String testString = "Brin, Sergey and Lawrence Page. “Reprint of: The anatomy of a large-scale hypertextual web search engine.” Computer Networks 56 (1998): 3825-3833.";
        tokenAnalyzer.wordSplit(testString);
    }
}
