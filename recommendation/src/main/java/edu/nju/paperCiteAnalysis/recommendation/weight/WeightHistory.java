package edu.nju.paperCiteAnalysis.recommendation.weight;

import edu.nju.paperCiteAnalysis.recommendation.common.Article;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.common.Inproceedings;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nathan on 16-3-11.
 */
public class WeightHistory {
    private static List<SearchWeight> historyList = new ArrayList<SearchWeight>();

    @Deprecated
    public SearchWeight SearchWeight(List<Bibtex> searchInfo) {
        int pos = getPos(searchInfo);
        return pos!=-1?historyList.get(pos):new SearchWeight(searchInfo);
    }

    public SearchWeight SearchWeight(int pos) {
        if(pos>=0 && pos<historyList.size())
            return historyList.get(pos);
        return null;
    }

    public int getPos(List<Bibtex> searchInfo) {
        if(CollectionUtils.isEmpty(searchInfo))
            return -1;
        for(int i=0; i<historyList.size(); ++i) {
            SearchWeight tmp = historyList.get(i);
            if(searchInfo.size() != tmp.getSearchInfo().size())
                continue;
            else {
                if(equals(tmp.getSearchInfo(), searchInfo))
                    return i;
            }
        }
        return -1;
    }

    public void like(int pos, List<Bibtex> likeInfo) {
        if(pos>=0 && pos<historyList.size())
            historyList.get(pos).like(likeInfo);
    }

    public void dislike(int pos, List<Bibtex> likeInfo) {
        if(pos>=0 && pos<historyList.size())
            historyList.get(pos).like(likeInfo);
    }

    public Map<String, Double> getWeight(int pos) {
        if(pos>=0 && pos<historyList.size())
            return historyList.get(pos).getWeight();
        return null;
    }

    private boolean equals(List<Bibtex> list1, List<Bibtex> list2) {
        if(list1.containsAll(list2) && list2.containsAll(list1))
            return true;
        return false;
    }

    /*public static void main(String args[]) {
        WeightHistory test = new WeightHistory();
        List<Bibtex> list1 = new ArrayList<Bibtex>();
        List<Bibtex> list2 = new ArrayList<Bibtex>();

        Inproceedings a1 = new Inproceedings();
        a1.setTitle("T1");a1.setAuthor("A1");a1.setYear("2009");a1.setBooktitle("BT1");
        Article b1 = new Article();
        b1.setTitle("T2");b1.setAuthor("A2");b1.setYear("2010");b1.setVolume("");b1.setJournal("J2");
        list1.add(a1);list1.add(b1);

        Inproceedings a2 = new Inproceedings();
        a2.setTitle("T1");a2.setAuthor("A1");a2.setYear("2009");a2.setBooktitle("BT1");
        Article b2 = new Article();
        b2.setTitle("T2");b2.setAuthor("A2");b2.setYear("2010");b2.setVolume(null);b2.setJournal("J2");
        list2.add(a2);list2.add(b2);

        System.out.println(test.equals(list1,list2));
    }*/
}
