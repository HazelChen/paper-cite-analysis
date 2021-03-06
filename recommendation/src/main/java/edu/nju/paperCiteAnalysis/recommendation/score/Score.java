package edu.nju.paperCiteAnalysis.recommendation.score;

import edu.nju.classifier.common.PropertyConstant;
import edu.nju.paperCiteAnalysis.recommendation.common.Article;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.common.Inproceedings;
import edu.nju.paperCiteAnalysis.recommendation.weight.SearchWeight;
import edu.nju.paperCiteAnalysis.recommendation.weight.WeightHistory;
import edu.nju.tokenAnalyzer.TokenAnalyzer;

import java.util.*;

/**
 * Created by zxy on 16-3-12.
 */

//计算相关论文的分数
public class Score {
    private List<Bibtex> relevantPapers = new ArrayList<Bibtex>();
    private List<Bibtex> inputPaper;
    private double[] authorVec;
    private String[] inputAuthors;
    private String[] inputTitles;
    private double[] titleVec;
    private List<String[]> allTitles = new ArrayList<String[]>();
    private TokenAnalyzer tokenAnalyzer = new TokenAnalyzer();
    private WeightHistory weightHistory = new WeightHistory();

    private CosineSimilarity c = new CosineSimilarity();
    private Tfidf tfidf = new Tfidf();

    public Map<Bibtex,Double> score(List<Bibtex> inputPaper, List<Bibtex> relevantPapers){
        initPapers(inputPaper,relevantPapers);
        Map<Bibtex,Double> scoreMap = new HashMap<Bibtex, Double>();

        if(relevantPapers.size() == 0){
            System.out.println("搜索不到相关论文");
            return null;
        }else{
            SearchWeight searchWeight = weightHistory.getSearchWeight(inputPaper);
            Map<String, Double> weight = searchWeight.getWeight();
            double authorRate = weight.get(PropertyConstant.AUTHOR);
            double titleRate = weight.get(PropertyConstant.TITLE);
            double journalRate = weight.get(PropertyConstant.JOURNAL);

            for(Bibtex bibtex:relevantPapers){
                if(bibtex instanceof Article){
                    Article article = (Article) bibtex;

                    double authorScore = authorScore(article.getAuthor());
                    double titleScore = titleScore(article.getTitle());
                    double journalScore = journalScore(article.getJournal());

                    double score = authorScore * authorRate + titleScore * titleRate + journalScore * journalRate;
                    scoreMap.put(article,score);
                    System.out.println(article.getTitle() + "的分数为" + score);

                }else if(bibtex instanceof Inproceedings){
                    Inproceedings inproceedings = (Inproceedings) bibtex;

                    double authorScore = authorScore(inproceedings.getAuthor());
                    double titleScore = titleScore(inproceedings.getTitle());
                    double bookTitleScore = bookTitleScore(inproceedings.getBooktitle());

                    double score = authorScore * authorRate + titleScore * titleRate + bookTitleScore * journalRate;
                    scoreMap.put(inproceedings,score);
                    System.out.println(inproceedings.getTitle() + "的分数为" + score);
                }
            }

            return scoreMap;
        }
    }

    //初始化成员变量
    private void initPapers(List<Bibtex> inputPaper, List<Bibtex> relevantPapers){
        this.inputPaper = inputPaper;

        String authorStr = "";
        String titleStr = "";
        for(Bibtex input : inputPaper){
            authorStr += input.getAuthor() + " and ";
            titleStr += input.getTitle() + " ";
        }

        inputAuthors = authorStr.split(" and ");
        authorVec = new double[inputAuthors.length];

        for (int i = 0; i < inputAuthors.length; i++) {
            authorVec[i] = 1.0;
        }

        this.relevantPapers = relevantPapers;
        for (Bibtex b : relevantPapers) {
            String title = b.getTitle();
            String[] titles = splitTitle(title);

            allTitles.add(titles);
        }

        //输入向量初始化
        inputTitles = splitTitle(titleStr);
        titleVec = titleVelCal(titleStr);

    }

    private double authorScore(String authors){
        double[] relevantVec = new double[10];
        String[] relevantAuthors = authors.split(" and ");
        List<String> relAuthorsList = new ArrayList<String>();

        Collections.addAll(relAuthorsList,relevantAuthors);
        for (int i = 0; i < inputAuthors.length; i++) {
            if(relAuthorsList.contains(inputAuthors[i])){
                relevantVec[i] = 1.0;
            }else{
                relevantVec[i] = 0.0;
            }
        }

        return c.cosineSimilarity(authorVec,relevantVec);
    }

    private double titleScore(String title){
        double relevantVec[] = titleVelCal(title);
        return c.cosineSimilarity(titleVec,relevantVec);
    }

    private double[] titleVelCal(String title){
        String[] titleArr = splitTitle(title);
        double[] relevantVec = new double[inputTitles.length];
        double min = 1000;
        double max = 0;

        for(int i = 0; i < inputTitles.length; i++){
            relevantVec[i] = tfidf.tfIdfCalculator(allTitles,titleArr,inputTitles[i]);

            if(relevantVec[i] < min){
                min = relevantVec[i];
            }else if(relevantVec[i] > max){
                max = relevantVec[i];
            }
        }

        for(int i = 0; i < relevantVec.length; i++){
            if(Double.compare(min,max) != 0){
                relevantVec[i] = (relevantVec[i] - min) / (max - min);
            }
        }

        return relevantVec;
    }

    private double bookTitleScore(String bookTitle){
        for(Bibtex bibtex : inputPaper){
            if(bibtex instanceof Inproceedings){
                Inproceedings inproceedings = (Inproceedings) bibtex;
                if(inproceedings.getBooktitle().equalsIgnoreCase(bookTitle)){
                    return 1.0;
                }
            }
        }
        return 0.0;
    }

    private double journalScore(String journal){
        for(Bibtex bibtex : inputPaper){
            if(bibtex instanceof Article){
                Article inputArticle = (Article) bibtex;
                if (inputArticle.getJournal().equalsIgnoreCase(journal)){
                    return 1.0;
                }
            }
        }
        return 0.0;
    }

    private String[] splitTitle(String title){
        List<String> titleList = tokenAnalyzer.wordSplit(title);
        String[] titles = titleList.toArray(new String[titleList.size()]);

        return titles;
    }

    public static void main(String[] args){
        List<Bibtex> input = new ArrayList<Bibtex>();
        Inproceedings falseInput = new Inproceedings();
        falseInput.setAuthor("Chenliang Xu and Caiming Xiong and Jason J. Corso");
        falseInput.setTitle("Streaming Hierarchical Video Segmentation");
        falseInput.setYear("2012");
        falseInput.setBooktitle("ECCV");
        input.add(falseInput);

        List<Bibtex> relevantPapers = new ArrayList<Bibtex>();
        Inproceedings re1 = new Inproceedings();
        re1.setAuthor("Kenneth L. Clarkson and David P. Woodruff");
        re1.setTitle("Numerical linear algebra in the streaming model");
        re1.setBooktitle("STOC");
        re1.setYear("2009");

        Inproceedings re2 = new Inproceedings();
        re2.setAuthor("Fabio Galasso and Margret Keuper and Thomas Brox and Bernt Schiele");
        re2.setTitle("Spectral Graph Reduction for Efficient Image and Streaming Video Segmentation");
        re2.setBooktitle("CVPR");
        re2.setYear("2014");

        relevantPapers.add(re1);
        relevantPapers.add(re2);

        Score score = new Score();
        score.score(input,relevantPapers);
    }
}
