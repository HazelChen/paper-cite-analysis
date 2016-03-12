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

    public Sort() {
        threshhold = 5.0;
    }

    public Sort(double threshhold) {
        this.threshhold = threshhold;
    }

    public void setThreshhold(double threshhold) {
        this.threshhold = threshhold;
    }

    public LinkedHashMap<Bibtex, Double> sort(Map<Bibtex, Double> origin) {
        int size = origin.size();
        List<ScoreBibtex> lists  = new ArrayList<ScoreBibtex>();

        Set<Bibtex> keySet = origin.keySet();
        Iterator<Bibtex> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            Bibtex bibtex = iterator.next();
            ScoreBibtex scoreBibtex = new ScoreBibtex(bibtex, origin.get(bibtex));
            lists.add(scoreBibtex);
        }
        Collections.sort(lists);

        LinkedHashMap<Bibtex, Double> results = new LinkedHashMap<Bibtex, Double>();
        for (int k = 0; k < size ; k ++){
            results.put(lists.get(k).bibtex, lists.get(k).score);
        }
        return results;
    }

    class ScoreBibtex implements Comparable<ScoreBibtex>{
        Bibtex bibtex;
        double score;

        ScoreBibtex(Bibtex bibtex, double score ){
            this.bibtex = bibtex;
            this.score = score;
        }

        public int compareTo(ScoreBibtex other) {
            try {
                int year = Integer.valueOf(bibtex.getYear());
                int otherYear = Integer.valueOf(other.bibtex.getYear());
                double gap = score - other.score;
                if (gap > 0 && gap < threshhold && year < otherYear) {
                    return 1;
                }
                if (gap < 0 || gap > threshhold || (gap > 0 && gap < threshhold && year >= otherYear)) {
                    return Double.compare(other.score, score);
                }
            }catch (NumberFormatException e){
                System.err.println("Error: invalid year found while sort " + e);
                System.exit(-1);
            }
            return 0;
        }
    }

    public static void main(String[] args){
        Sort sort = new Sort();

        Map<Bibtex, Double> map = new HashMap<Bibtex, Double>();
        Bibtex bibtex1 = new Article();
        bibtex1.setYear("2013");
        map.put(bibtex1, Double.valueOf(90));

        Bibtex bibtex2 = new Inproceedings();
        bibtex2.setYear("2010");
        map.put(bibtex2, Double.valueOf(91));

        Bibtex bibtex3 = new Article();
        bibtex3.setYear("2015");
        map.put(bibtex3, Double.valueOf(70));

        sort.sort(map);
    }
}
