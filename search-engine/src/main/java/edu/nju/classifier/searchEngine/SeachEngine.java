package edu.nju.classifier.searchEngine;

import edu.nju.classifier.common.Article;
import edu.nju.classifier.common.Bibtex;
import edu.nju.classifier.common.Inproceedings;
import edu.nju.classifier.common.MatchScore;
import edu.nju.tokenAnalyzer.TokenAnalyzer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by nathan on 16-2-27.
 */
public class SeachEngine {

    private TokenAnalyzer tokenAnalyzer;

    private static Map<String, List<String>> invertedIndex = InvertedIndexFactory.createInvertedIndex();


    private List<MatchScore> getOrderedList(Map<String, List<Double>> scoreList) {
        List<MatchScore> orderedList = new ArrayList<MatchScore>();

        if(MapUtils.isEmpty(scoreList))
            return orderedList;
        for(String word:scoreList.keySet()) {
            MatchScore matchScore = new MatchScore();
            double score = 0.0;
            for(double s:scoreList.get(word)) {
                score+=s;
            }
            matchScore.setRowKey(word);
            matchScore.setScore(score);
            orderedList.add(matchScore);
        }
        Collections.sort(orderedList);

        return orderedList;
    }

    public List<Bibtex> search(String target, int num) {
        tokenAnalyzer = new TokenAnalyzer();

        Map<String, List<Double>> scoreList = new HashMap<String, List<Double>>();

        List<String> sources = tokenAnalyzer.wordSplit(target);
        if(CollectionUtils.isEmpty(sources))
            return null;
        //  对每个关键字进行处理
        for(String source:sources) {
            if(invertedIndex.containsKey(source)) {
                List<String> rowKeys = invertedIndex.get(source);
                int count = rowKeys.size();
                //  对符合关键字检索结果的rowkey进行处理
                for(String rowKey:rowKeys) {
                    String[] tmp = rowKey.trim().split(",");
                    String row = tmp[0].trim();
                    double tf = Double.parseDouble(tmp[1].trim()); // term-frequency
                    double tfidf = tf / 100 * Math.log(1400/count);   //  term-frequency * inverse document-frequency
                    if(scoreList.containsKey(row))
                        scoreList.get(row).add(tfidf);
                    else {
                        List<Double> list = new ArrayList<Double>();
                        list.add(tfidf);
                        scoreList.put(row, list);
                    }
                }
            }
        }

        List<MatchScore> orderedList = getOrderedList(scoreList);
        int size = orderedList.size()>num?num:orderedList.size();
        orderedList = orderedList.subList(0, size);

        return BibtexFactory.fetchResultList(orderedList);
    }


    public static void main(String args[]) {
//        Guibas, L.J., Rubner, Y., & Tomasi, C.. (2000). The Earth Mover's Distance as a Metric for Image Retrieval. International Journal of Computer Vision, 40, 99-121.
//        Rubner, Yossi et al. “The Earth Mover's Distance as a Metric for Image Retrieval.” International Journal of Computer Vision 40 (2000): 99-121.
//        Rubner, Yossi, Carlo Tomasi and Leonidas J. Guibas. “The Earth Mover's Distance as a Metric for Image Retrieval.” International Journal of Computer Vision 40 (2000): 99-121.

        String target = "Spatial Pyramid Pooling in Deep Convolutional Networks for Visual Recognition";
        SeachEngine se = new SeachEngine();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入您要搜索的内容（输入'q'结束程序）：");
        String text = "";
        while(!(text=scanner.nextLine()).equals("q")) {
            List<Bibtex> searchResult = se.search(text, 5);
            if(CollectionUtils.isNotEmpty(searchResult)) {
                System.out.println("搜索结果如下（显示" + searchResult.size() + "条）：");
                for (int i = 0; i < searchResult.size(); ++i) {
                    if (searchResult.get(i) instanceof Article) {
                        Article a = (Article)searchResult.get(i);
                        System.out.println(i+1 + ".\tArticle.\t" +
                                "author("+a.getAuthor()+"), " +
                                "title("+a.getTitle()+"), " +
                                "year("+a.getYear()+"), " +
                                "journal("+a.getJournal()+"), "+
                                "volume("+a.getVolume()+"), "+
                                "pages("+a.getPages()+")");
                    } else {
                        Inproceedings in = (Inproceedings)searchResult.get(i);
                        System.out.println(i+1 + ".\tInproceedings.\t" +
                                "author("+in.getAuthor()+"), " +
                                "title("+in.getTitle()+"), " +
                                "year("+in.getYear()+"), " +
                                "booktitle("+in.getBooktitle()+")");
                    }
                }
            }
            System.out.println("请继续输入您要搜索的内容（输入'q'结束程序）：");
        }
        System.exit(0);
    }
}
