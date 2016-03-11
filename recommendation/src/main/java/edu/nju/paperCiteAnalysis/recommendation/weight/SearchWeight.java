package edu.nju.paperCiteAnalysis.recommendation.weight;

import edu.nju.classifier.common.PropertyConstant;
import edu.nju.paperCiteAnalysis.recommendation.common.Article;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.common.Inproceedings;
import edu.nju.tokenAnalyzer.TokenAnalyzer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

/**
 * Created by nathan on 16-3-11.
 */

public class SearchWeight {
    private List<Bibtex> searchInfo;
    private Map<String, Map<String, Integer>> infoCount;
    private Map<String, Double> weight;

    private TokenAnalyzer tokenAnalyzer;

    public SearchWeight(List<Bibtex> searchInfo) {
        this.searchInfo = searchInfo;
        this.tokenAnalyzer = new TokenAnalyzer();

        if(CollectionUtils.isNotEmpty(searchInfo)) {
            initCountMap();
            if(searchInfo.size() == 1) {
                weight.put(PropertyConstant.AUTHOR, 30.0);
                weight.put(PropertyConstant.TITLE, 50.0);
                weight.put(PropertyConstant.JOURNAL, 20.0);
            }
            else
                updateWeight();
        }
    }

    public Map<String, Double> getWeight() {
        return this.weight;
    }

    public List<Bibtex> getSearchInfo() {
        return this.searchInfo;
    }

    public void dislike(List<Bibtex> excludeV) {
        Map<String, List<String>> wordMapList = getWordMapList(excludeV);
        if(MapUtils.isEmpty(wordMapList))
            return;

        String[] types = {PropertyConstant.AUTHOR, PropertyConstant.TITLE, PropertyConstant.JOURNAL};
        for(String type : types) {
            Map<String, Integer> curMap = infoCount.get(type);
            for (String word : wordMapList.get(type)) {
                word = word.trim().toLowerCase();
                if (null != curMap.get(word)) {
                    int count = curMap.get(word);
                    curMap.put(word, count == 0?0:count - 1);
                } else {
                    // do nothing
                }
            }
            infoCount.put(type, sortMapByValue(curMap));
        }
        updateWeight();
    }

    public void like(List<Bibtex> includeV) {
        Map<String, List<String>> wordMapList = getWordMapList(includeV);
        if(MapUtils.isEmpty(wordMapList))
            return;

        String[] types = {PropertyConstant.AUTHOR, PropertyConstant.TITLE, PropertyConstant.JOURNAL};
        for(String type : types) {
            Map<String, Integer> curMap = infoCount.get(type);
            for (String word : wordMapList.get(type)) {
                word = word.trim().toLowerCase();
                if (null != curMap.get(word)) {
                    int count = curMap.get(word);
                    curMap.put(word, count + 1);
                } else {
                    curMap.put(word, 1);
                }
            }
            infoCount.put(type, sortMapByValue(curMap));
        }
        updateWeight();
    }

    /**
     * initialize Map<String, Map<String, Integer>> infoCount
     */
    private void initCountMap() {
        this.infoCount = new HashMap<String, Map<String, Integer>>();
        if(CollectionUtils.isEmpty(searchInfo))
            return;
        Map<String, List<String>> wordMapList = getWordMapList(this.searchInfo);
        if(MapUtils.isNotEmpty(wordMapList)) {
            infoCount.put(PropertyConstant.AUTHOR,
                    initWordCount(wordMapList.get(PropertyConstant.AUTHOR)));
            infoCount.put(PropertyConstant.TITLE,
                    initWordCount(wordMapList.get(PropertyConstant.TITLE)));
            infoCount.put(PropertyConstant.JOURNAL,
                    initWordCount(wordMapList.get(PropertyConstant.JOURNAL)));
        }
    }

    private Map<String, List<String>> getWordMapList(List<Bibtex> bibtexs) {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        List<String> authorCount = new ArrayList<String>();
        List<String> titleCount = new ArrayList<String>();
        List<String> jounalCount = new ArrayList<String>();
        for (int i = 0; i < bibtexs.size(); ++i) {
            Bibtex b = bibtexs.get(i);

            Collections.addAll(authorCount, b.getAuthor().trim().split(" and "));
            titleCount.addAll(tokenAnalyzer.wordSplit(b.getTitle()));

            if (b instanceof Article) {
                Article a = (Article)b;
                jounalCount.add(a.getJournal());
            } else {
                Inproceedings in = (Inproceedings)b;
                jounalCount.add(in.getBooktitle());
            }
        }
        result.put(PropertyConstant.AUTHOR, authorCount);
        result.put(PropertyConstant.TITLE, titleCount);
        result.put(PropertyConstant.JOURNAL, jounalCount);

        return result;
    }

    /**
     * count the same word
     * @param words
     * @return
     */
    private Map<String, Integer> initWordCount(List<String> words) {
        Map<String, Integer> curMap = new LinkedHashMap<String, Integer>();
        if(CollectionUtils.isEmpty(words))
            return curMap;

        for(String word : words){
            word = word.trim().toLowerCase();
            if(null != curMap.get(word)){
                int count = curMap.get(word);
                curMap.put(word, count+1);
            }else{
                curMap.put(word,1);
            }
        }
        return sortMapByValue(curMap);
    }

    private void updateWeight() {
        this.weight = new HashMap<String, Double>();
        if(MapUtils.isEmpty(infoCount))
            return;

        List<Map.Entry<String, Integer>> author = new
                ArrayList<Map.Entry<String, Integer>>(infoCount.get(PropertyConstant.AUTHOR).entrySet());
        List<Map.Entry<String, Integer>> title =
                new ArrayList<Map.Entry<String, Integer>>(infoCount.get(PropertyConstant.TITLE).entrySet());
        List<Map.Entry<String, Integer>> jounal = new
                ArrayList<Map.Entry<String, Integer>>(infoCount.get(PropertyConstant.JOURNAL).entrySet());
        double weight1 = CollectionUtils.isNotEmpty(author)?author.get(0).getValue():0;
        double weight2 = CollectionUtils.isNotEmpty(title)?title.get(0).getValue():0;
        double weight3 = CollectionUtils.isNotEmpty(jounal)?jounal.get(0).getValue():0;

        double sum = weight1 + weight2 + weight3;
        weight.put(PropertyConstant.AUTHOR, weight1/sum*100);
        weight.put(PropertyConstant.TITLE, weight2/sum*100);
        weight.put(PropertyConstant.JOURNAL, weight3/sum*100);
    }

    /**
     *
     * @param oriMap unsorted map
     * @return sorted map
     */
    private Map<String, Integer> sortMapByValue(Map<String, Integer> oriMap) {
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(oriMap.entrySet());
        if (MapUtils.isNotEmpty(oriMap)) {
            Collections.sort(entryList,
                    new Comparator<Map.Entry<String, Integer>>() {
                        public int compare(Map.Entry<String, Integer> entry1,
                                           Map.Entry<String, Integer> entry2) {
                            return entry2.getValue().compareTo(entry1.getValue());
                        }
                    });
            Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
            Map.Entry<String, Integer> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }

    /*public static void main(String[] args) {
        List<Bibtex> list = new ArrayList<Bibtex>();
        Article a1 = new Article();
        a1.setAuthor("Yossi Rubner and Carlo Tomasi and Leonidas J. Guibas");
        a1.setTitle("Earth Mover Metric Image Retrieval");
        a1.setJournal("International Journal of Computer Vision");
        list.add(a1);


        Inproceedings a2 = new Inproceedings();
        a2.setAuthor("Yossi Rubner and Carlo Tomasi");
        a2.setTitle("Metric Distributions Applications Image Databases Image");
        a2.setBooktitle("ICCV");
        list.add(a2);

        System.out.println("original data:");
        SearchWeight a = new SearchWeight(list);
        for(Map<String, Integer> tmp : a.getInfoCount().values()) {
            System.out.println("\t" + tmp.toString());
        }
        System.out.println("\t" + a.getWeight());


        System.out.println("after like:");
        Inproceedings like = new Inproceedings();
        like.setAuthor("Yossi Rubner");
        like.setTitle("Metric Metric with you");
        like.setBooktitle("ICCV");
        list.clear();
        list.add(like);
        a.like(list);
        for(Map<String, Integer> tmp : a.getInfoCount().values()) {
            System.out.println("\t" + tmp.toString());
        }
        System.out.println("\t" + a.getWeight());

        System.out.println("after dislike:");
        Inproceedings dislike = new Inproceedings();
        dislike.setAuthor("Yossi Rubner");
        dislike.setTitle("Metric with you");
        dislike.setBooktitle("International Journal of Computer Vision");
        list.clear();
        list.add(dislike);
        a.dislike(list);
        for(Map<String, Integer> tmp : a.getInfoCount().values()) {
            System.out.println("\t" + tmp.toString());
        }
        System.out.println("\t" + a.getWeight());
    }*/
}