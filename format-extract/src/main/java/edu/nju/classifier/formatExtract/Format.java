package edu.nju.classifier.formatExtract;

import edu.nju.classifier.common.Article;
import edu.nju.classifier.common.Bibtex;
import edu.nju.classifier.common.Inproceedings;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by margine on 16-2-27.
 */
public class Format {
    private static final Pattern ARTICLE_PATTERN =
            Pattern.compile(
                    "^(.*?)\\.\\s“(.*?)”\\s(.*?)(\\S+)\\s\\(([0-9]+)\\):\\s(.*?).$"
            );

    private static final Pattern INPROCEEDINGS_PATTERN =
            Pattern.compile(
                    "^(.*?)\\.\\s“(.*?)”\\s(.*?)\\s\\(([0-9]+)\\).$"
            );

    private static final Pattern INPROCEEDINGS_PATTERN_II =
            Pattern.compile(
                    "^(.*?)\\.\\s“(.*?)”\\s\\(([0-9]+)\\).$"
            );

    public static Bibtex formartAPA(String apa) {
        ArrayList<String> items = new ArrayList<String>();
        int start = 0, count = 0;
        for (int i = 0; i < apa.length(); ++i) {
            if (count == 3) {
                items.add(apa.substring(start, apa.length() - 1));
                break;
            }
            if (apa.charAt(i) == '.') {
                if (i + 1 < apa.length()) {
                    if (apa.charAt(i + 1) == ' ') {
                        items.add(apa.substring(start, i));
                        start = i + 2;
                        count++;
                    }
                } else {
                    items.add(apa.substring(start, i));
                    start = i + 2;
                    count++;
                }
            } else
                continue;
        }
        if (items.size() != 4 && items.size() != 3)
            return null;
        String tmp = ",";
        if (items.size() == 4) {
            tmp = items.get(3);
        }

        String[] publicationData = tmp.split(",");
        Bibtex bibtex;
        if (publicationData.length <= 1) {
            bibtex = new Inproceedings();
            ((Inproceedings)bibtex).setBooktitle(publicationData[0].trim());
        } else {
            bibtex = new Article();
            ((Article)bibtex).setJournal(publicationData[0].trim());
            ((Article)bibtex).setVolume(publicationData[1].trim());
            if (publicationData.length == 3)
                ((Article)bibtex).setPages(publicationData[2].trim());
        }

        String author = items.get(0);
        String year = items.get(1).substring(1, items.get(1).length() - 1);
        String title = items.get(2);

        bibtex.setTitle(title);
        bibtex.setAuthor(author);
        bibtex.setYear(year);
        return bibtex;
    }

    /**
     *mla & chicago
     * @param data
     * @return
     */
    public static Bibtex formartMC(String data) {
        Matcher articleMatcher = ARTICLE_PATTERN.matcher(data);
        Matcher inproceedingsMatcher = INPROCEEDINGS_PATTERN.matcher(data);
        Matcher inproceedingsMatcher2 = INPROCEEDINGS_PATTERN_II.matcher(data);

        try {
            if (articleMatcher.find()) {
                Article article = new Article();
                article.setAuthor(articleMatcher.group(1).trim());
                String title = articleMatcher.group(2).replace(".", "");
                article.setTitle(title.trim());
                article.setJournal(articleMatcher.group(3).trim());
                article.setVolume(articleMatcher.group(4).trim());
                article.setYear(articleMatcher.group(5).trim());
                article.setPages(articleMatcher.group(6).trim());
                return article;
            }
            else if (inproceedingsMatcher.find()){
                Inproceedings inproceedings = new Inproceedings();
                inproceedings.setAuthor(inproceedingsMatcher.group(1).trim());
                String title = inproceedingsMatcher.group(2).replace(".", "");
                inproceedings.setTitle(title.trim());
                inproceedings.setBooktitle(inproceedingsMatcher.group(3).trim());
                inproceedings.setYear(inproceedingsMatcher.group(4).trim());
                return inproceedings;
            }else if (inproceedingsMatcher2.find()){
                Inproceedings inproceedings = new Inproceedings();
                inproceedings.setAuthor(inproceedingsMatcher2.group(1).trim());
                String title = inproceedingsMatcher2.group(2).replace(".", "");
                inproceedings.setTitle(title.trim());
                inproceedings.setYear(inproceedingsMatcher2.group(3).trim());
                return inproceedings;
            }
        }catch (IllegalStateException e){
            throw new RuntimeException("Can not resolve: [" + data + "]");
        }
        return null;
    }
}
