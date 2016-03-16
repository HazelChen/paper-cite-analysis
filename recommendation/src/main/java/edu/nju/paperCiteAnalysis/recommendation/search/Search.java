package edu.nju.paperCiteAnalysis.recommendation.search;

import edu.nju.classifier.searchEngine.InvertedIndexFactory;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.tools.StringToBibtex;
import edu.nju.tokenAnalyzer.TokenAnalyzer;

import java.util.*;

/**
 * Created by margine on 16-3-12.
 */
public class Search {
    private TokenAnalyzer tokenAnalyzer;
    DataHelper dataHelper;
    private static Map<String, List<String>> invertedIndex = InvertedIndexFactory.createInvertedIndex();

    public List<Bibtex> search(String source) {
        ArrayList<String> tmp = new ArrayList<String>(1);
        tmp.add(source);
        return search(tmp);
    }

    public List<Bibtex> search(List<String> sources) {
        Set<String> rowkeySets = new HashSet<String>();
        List<String> keywords = new ArrayList<String>();
        for (String source : sources){
            keywords.addAll(getSearchKeys(source));
        }
        for (String keyword : keywords){
            List <String> rowkeys = invertedIndex.get(keyword);
            if (rowkeys != null && rowkeys.size() != 0){
               for (String rowkey: rowkeys){
                   //split<rowkey,num>
                   rowkeySets.add(rowkey.substring(0, rowkey.indexOf(",")));
               }
            }
        }
        dataHelper = new DataHelper();
        List<Bibtex> results = new ArrayList<Bibtex>();
        results.addAll(dataHelper.getResults(rowkeySets));
        return  results;
    }

    private List<String> getSearchKeys(String source){
        tokenAnalyzer = new TokenAnalyzer();
        Bibtex bibtex = StringToBibtex.convert(source);
        bibtex.setYear("");

        //filter symbol
        String[] removeKeys = new String[]{"article", "inproceedings", "author", "title", "year","pages", "journal", "booktitle", "volume"};

        List<String> keys = new ArrayList<String>();
        keys.addAll(tokenAnalyzer.wordSplit(bibtex.toString()));
        for (int i = 0; i < removeKeys.length ; i++){
            keys.remove(removeKeys[i]);
        }
        return  keys;
    }
    public static void main(String[] args) {
        Search search = new Search();
        search.search("@article{Bowyer2000IEEETransPatternAnalMachIntell,\n" +
                "\t\t\t  title={A 20th Anniversary Survey: Introduction to 'Content-Based Image Retrieval at the End of the Early Years'},\n" +
                "\t\t\t  author={Kevin W. Bowyer and Patrick J. Flynn},\n" +
                "\t\t\t  journal={IEEE Trans. Pattern Anal. Mach. Intell.},\n" +
                "\t\t\t  year={2000},\n" +
                "\t\t\t  volume={22},\n" +
                "\t\t\t  pages={1348}\n" +
                "\t\t\t}");
    }
}
