package edu.nju.classifier.searchAnalysis;

import edu.nju.classifier.common.Article;
import edu.nju.classifier.common.Bibtex;
import edu.nju.classifier.common.Inproceedings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by margine on 16-2-28.
 */
public class Search {
    public static List<Bibtex> DoSearch(String queryStr, int k){
        ArrayList<Bibtex> bibtexs = new ArrayList<Bibtex>();
        Bibtex bibtex1 = new Article();
        bibtex1.setYear("1998");
        bibtex1.setAuthor("Sergey Brin and Lawrence Page");
        bibtex1.setTitle("Reprint of: The anatomy of a large-scale hypertextual web search engine");
        ((Article)bibtex1).setJournal("Computer Networks");
        ((Article)bibtex1).setPages("3825-3833");
        ((Article)bibtex1).setVolume("56");

        Bibtex bibtex2 = new Inproceedings();
        bibtex2.setYear("2006");
        bibtex2.setAuthor("Simone Santini");
        bibtex2.setTitle("Video Search");
        ((Inproceedings)bibtex2).setBooktitle("MM");

        Bibtex bibtex3 = new Inproceedings();
        bibtex3.setYear("206");
        bibtex3.setAuthor("Simone Santini");
        bibtex3.setTitle("Video Search");
        ((Inproceedings)bibtex3).setBooktitle("MM");

        Bibtex bibtex4 = new Article();
        bibtex4.setTitle("Transductive multi-distance learning for video search");
        bibtex4.setAuthor("Songhao Zhu and Zhiwei Liang and Yuncai Liu");
        bibtex4.setYear("2013");
        ((Article)bibtex4).setJournal("Pattern Anal. Appl.");
        ((Article)bibtex4).setPages("117-124");
        ((Article)bibtex4).setVolume("16");

        bibtexs.add(bibtex2);
        bibtexs.add(bibtex3);
        bibtexs.add(bibtex1);
        bibtexs.add(bibtex4);

        return  bibtexs;
    }
}
