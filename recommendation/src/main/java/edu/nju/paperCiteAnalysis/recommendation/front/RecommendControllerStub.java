package edu.nju.paperCiteAnalysis.recommendation.front;

import edu.nju.paperCiteAnalysis.recommendation.common.Article;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.common.Inproceedings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hazel on 2016-03-12.
 */
public class RecommendControllerStub implements RecommendController{

    public Map<Bibtex, Integer> recommend(List<String> bibtexStrings) {
        Map<Bibtex, Integer> result = new HashMap<Bibtex, Integer>();

        Bibtex bibtex1 = new Article("Harold W. Thimbleby and Stuart Inglis and Ian H. Witten",
                "Displaying 3D Images: Algorithms for Single-Iamge Random-Dot Stereograms",
                "1994", "IEEE Computer", "38-48", "27");
        result.put(bibtex1, 90);

        Bibtex bibtex2 = new Inproceedings("Guobin Shen and Bing Zeng and Ming L. Liou",
                "A New Padding Technique for Coding of Arbitrarily-Shaped Iamge/Video Segments",
                "1999", "ICIP");
        result.put(bibtex2, 85);

        return result;
    }
}
