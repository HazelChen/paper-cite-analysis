package edu.nju.paperCiteAnalysis.recommendation.front;

import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hazel on 2016-03-12.
 */
public class RecommendControllerImpl implements RecommendController{
    private Map<List<Bibtex>, Set<Bibtex>> trash;

    public RecommendControllerImpl() {
        trash = new HashMap<List<Bibtex>, Set<Bibtex>>();
    }

    public Map<Bibtex, Integer> recommend(List<String> bibtexStrings) {
        return null;
    }
}
