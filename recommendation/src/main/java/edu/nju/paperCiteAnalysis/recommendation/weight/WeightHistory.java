package edu.nju.paperCiteAnalysis.recommendation.weight;

import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by nathan on 16-3-11.
 */
public class WeightHistory {
    private static List<SearchWeight> historyList;

    public void addHistory() {

    }

    public SearchWeight searchHistory(List<Bibtex> searchInfo) {
        int pos = getPos(searchInfo);
        return pos!=-1?historyList.get(pos):new SearchWeight(searchInfo);
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

    private boolean equals(List<Bibtex> list1, List<Bibtex> list2) {
        if(list1.containsAll(list2) && list2.containsAll(list1))
            return true;
        return false;
    }
}
