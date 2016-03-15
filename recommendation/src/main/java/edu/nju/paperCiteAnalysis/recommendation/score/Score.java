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

        inputTitles = splitTitle(titleStr);
        titleVec = new double[inputTitles.length];
        for (int i = 0; i < inputTitles.length; i++){
            titleVec[i] = 1.0;
        }

        this.relevantPapers = relevantPapers;
        for (Bibtex b : relevantPapers) {
            String title = b.getTitle();
            String[] titles = splitTitle(title);

            allTitles.add(titles);
        }

        for(int i = 0; i < inputAuthors.length; i++){
            System.out.println("作者为：" + inputAuthors[i]);
        }
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
        String[] titleArr = splitTitle(title);
        double[] relevantVec = new double[inputTitles.length];
        double min = 1000;
        double max = 0;

        for(int i = 0; i < inputTitles.length; i++){
            double w = tfidf.tfIdfCalculator(allTitles,titleArr,inputTitles[i]);
            relevantVec[i] = w;

            if(relevantVec[i] < min){
                min = relevantVec[i];
            }else if(relevantVec[i] > max){
                max = relevantVec[i];
            }
        }

        for(int i = 0; i < relevantVec.length; i++){
            relevantVec[i] = (relevantVec[i] - min) / (max - min);
        }
        return c.cosineSimilarity(titleVec,relevantVec);
    }

    private double bookTitleScore(String bookTitle){
        if(inputPaper instanceof Inproceedings){
            Inproceedings inproceedings = (Inproceedings) inputPaper;
            if(inproceedings.getBooktitle().equalsIgnoreCase(bookTitle)){
                return 1.0;
            }
        }
        return 0.0;
    }

    private double journalScore(String journal){
        if(inputPaper instanceof Article){
            Article inputArticle = (Article) inputPaper;
            if (inputArticle.getJournal().equalsIgnoreCase(journal)){
                return 1.0;
            }
        }
        return 0.0;
    }

    private String[] splitTitle(String title){
        List<String> titleList = tokenAnalyzer.wordSplit(title);
        String[] titles = titleList.toArray(new String[titleList.size()]);

        return titles;
    }

//    public static void main(String[] args){
//        List<Bibtex> input = new ArrayList<Bibtex>();
//        Article falseInput = new Article();
//        falseInput.setAuthor("Yossi Rubner and Carlo Tomasi and Leonidas J. Guibas");
//        falseInput.setTitle("The Earth Mover's Distance as a Metric for Image Retrieval");
//        falseInput.setYear("2000");
//        falseInput.setJournal("International Journal of Computer Vision");
//        input.add(falseInput);
//
//        List<Bibtex> relevantPapers = new ArrayList<Bibtex>();
//        Article re1 = new Article();
//        re1.setAuthor("Yossi Rubner and Carlo Tomasi");
//        re1.setTitle("The Earth Mover's Distance as a Metric for Image");
//        re1.setJournal("International Journal of Computer Vision");
//
//        Article re2 = new Article();
//        re2.setAuthor("Yossi Rubner and Carlo Tomasi");
//        re2.setTitle("The Earth Mover's as a Metric for Image");
//        re2.setJournal("International of Computer Vision");
//
//        relevantPapers.add(re2);
//        relevantPapers.add(re1);
//
//        Score score = new Score();
//        score.score(input,relevantPapers);
//    }
}
