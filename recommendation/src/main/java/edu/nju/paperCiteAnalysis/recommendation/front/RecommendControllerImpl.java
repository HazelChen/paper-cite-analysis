package edu.nju.paperCiteAnalysis.recommendation.front;

import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.score.Score;
import edu.nju.paperCiteAnalysis.recommendation.search.Search;
import edu.nju.paperCiteAnalysis.recommendation.sort.Sort;
import edu.nju.paperCiteAnalysis.recommendation.tools.StringToBibtex;
import edu.nju.paperCiteAnalysis.recommendation.weight.SearchWeight;

import java.util.*;

/**
 * Created by hazel on 2016-03-12.
 */
public class RecommendControllerImpl implements RecommendController{
    private Map<List<Bibtex>, Set<Bibtex>> trash;

    private Search search;
    private Score score;
    private Sort sort;

    private List<Bibtex> lastInput;
    private SearchWeight searchWeight;


    public RecommendControllerImpl() {
        trash = new HashMap<List<Bibtex>, Set<Bibtex>>();

        search = new Search();
        score = new Score();
        sort = new Sort();

        lastInput = new ArrayList<Bibtex>();
    }

    public Map<Bibtex, Double> recommend(List<String> bibtexStrings) {
        lastInput.clear();
        for (String bibtexString : bibtexStrings) {
            Bibtex bibtex = StringToBibtex.convert(bibtexString);
            if (bibtex != null) {
                lastInput.add(bibtex);
            }
        }
        searchWeight = new SearchWeight(lastInput);

        List<Bibtex> relativeBibtex = search.search(bibtexStrings);
        Map<Bibtex,Double> bibtexWithScore = score.score(lastInput, relativeBibtex);
        return sort.sort(bibtexWithScore);
    }

    public void like(Bibtex bibtex) {
        if (searchWeight == null) {
            return;
        }

        searchWeight.like(Arrays.asList(bibtex));
    }

    public void dislike(Bibtex bibtex) {
        if (!lastInput.isEmpty()) {
            Set<Bibtex> litters = trash.get(lastInput);
            if (litters == null) {
                Set<Bibtex> litterSet = new HashSet<Bibtex>();
                litterSet.add(bibtex);
                trash.put(lastInput, litterSet);
            } else {
                litters.add(bibtex);
            }
        }

        if (searchWeight == null) {
            return;
        }
        searchWeight.dislike(Arrays.asList(bibtex));
    }
}
