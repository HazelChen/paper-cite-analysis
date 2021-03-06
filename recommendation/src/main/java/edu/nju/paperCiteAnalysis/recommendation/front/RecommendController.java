package edu.nju.paperCiteAnalysis.recommendation.front;

import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;

import java.util.List;
import java.util.Map;

/**
 * Created by hazel on 2016-03-12.
 */
public interface RecommendController {

    Map<Bibtex, Double> recommend(List<String> bibtexStrings);

    void like(Bibtex bibtex);

    void dislike(Bibtex bibtex);
}
