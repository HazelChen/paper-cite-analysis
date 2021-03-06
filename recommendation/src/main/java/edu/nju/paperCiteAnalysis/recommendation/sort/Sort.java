package edu.nju.paperCiteAnalysis.recommendation.sort;

import edu.nju.paperCiteAnalysis.recommendation.common.Article;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.common.Inproceedings;

import java.util.*;

/**
 * Created by margine on 16-3-12.
 */
public class Sort {
    private double threshhold;
    private int topNum;

    public Sort() {
        threshhold = 5.0;
        topNum = 10;
    }

    public Sort(double threshhold) {
        super();
        this.threshhold = threshhold;
    }

    public Sort(double threshhold, int topNum) {
        this.threshhold = threshhold;
        this.topNum = topNum;
    }

    public Sort(int topNum) {
        super();
        if (topNum > 0)
            this.topNum = topNum;
    }

    public void setThreshhold(double threshhold) {
        this.threshhold = threshhold;
    }

    public LinkedHashMap<Bibtex, Double> sort(Map<Bibtex, Double> origin) {
        List<ScoreBibtex> lists = new ArrayList<ScoreBibtex>();

        Set<Bibtex> keySet = origin.keySet();
        Iterator<Bibtex> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            Bibtex bibtex = iterator.next();
            ScoreBibtex scoreBibtex = new ScoreBibtex(bibtex, origin.get(bibtex));
            lists.add(scoreBibtex);
        }

        Collections.sort(lists);

        LinkedHashMap<Bibtex, Double> results = new LinkedHashMap<Bibtex, Double>();
        int size = topNum <= lists.size() ? topNum : lists.size();
        for (int k = 0; k < size; k++) {
            results.put(lists.get(k).bibtex, lists.get(k).score);
        }
        return results;
    }

    class ScoreBibtex implements Comparable<ScoreBibtex> {
        Bibtex bibtex;
        double score;

        ScoreBibtex(Bibtex bibtex, double score) {
            this.bibtex = bibtex;
            this.score = score;
        }

        public int compareTo(ScoreBibtex other) {
            int year = Integer.valueOf(bibtex.getYear());
            int otherYear = Integer.valueOf(other.bibtex.getYear());
            double gap = score - other.score;
            if (Double.compare(gap, 0) > 1 && Double.compare(gap, threshhold) < 1 && year < otherYear) {
                return 1;
            } else {
                return Double.compare(other.score, score);
            }
        }
    }

    public static void main(String[] args) {
        Sort sort = new Sort();

        Map<Bibtex, Double> map = new HashMap<Bibtex, Double>();
        Bibtex bibtex1 = new Article();
        bibtex1.setYear("2011");
        map.put(bibtex1, Double.valueOf(1.707));

        Bibtex bibtex2 = new Inproceedings();
        bibtex2.setYear("2010");
        map.put(bibtex2, Double.valueOf(0.0));

//        Bibtex bibtex3 = new Article();
//        bibtex3.setYear("2015");
//        map.put(bibtex3, Double.valueOf(70));

        sort.sort(map);
    }
}
