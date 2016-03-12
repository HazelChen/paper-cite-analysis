package edu.nju.paperCiteAnalysis.recommendation.common;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by nathan on 16-2-27.
 * Article
 */
@Data
public class Article extends Bibtex {
    private String journal;
    private String pages;
    private String volume;

    public Article(){}

    public Article(String author, String title, String year,
                   String journal, String pages, String volume) {

        super(author, title, year);

        this.journal = journal;
        this.pages = pages;
        this.volume = volume;
    }

    @Override
    public boolean equals(Object o){
        if (o == null || !(o instanceof Article))
            return false;

        Article other = (Article) o;
        if ((StringUtils.isEmpty(author) && StringUtils.isNotEmpty(author)) ||
                (StringUtils.isNotEmpty(author) && StringUtils.isNotEmpty(author) && !author.equals(other.author))){
            return false;
        }
        if ((StringUtils.isEmpty(title) && StringUtils.isNotEmpty(title)) ||
                (StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(title) && !title.equals(other.title))){
            return false;
        }
        if ((StringUtils.isEmpty(year) && StringUtils.isNotEmpty(year)) ||
                (StringUtils.isNotEmpty(year) && StringUtils.isNotEmpty(year) && !year.equals(other.year))){
            return false;
        }
        if ((StringUtils.isEmpty(journal) && StringUtils.isNotEmpty(journal)) ||
                (StringUtils.isNotEmpty(journal) && StringUtils.isNotEmpty(journal) && !journal.equals(other.journal))){
            return false;
        }
        if ((StringUtils.isEmpty(volume) && StringUtils.isNotEmpty(volume)) ||
                (StringUtils.isNotEmpty(volume) && StringUtils.isNotEmpty(volume) && !volume.equals(other.volume))){
            return false;
        }
        if ((StringUtils.isEmpty(pages) && StringUtils.isNotEmpty(pages)) ||
                (StringUtils.isNotEmpty(pages) && StringUtils.isNotEmpty(pages) && !pages.equals(other.pages))){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        return (StringUtils.isEmpty(author)?0:author.hashCode()*33) +
                (StringUtils.isEmpty(title)?0:title.hashCode()*33) +
                (StringUtils.isEmpty(year)?0:year.hashCode()*33) +
                (StringUtils.isEmpty(journal)?0:journal.hashCode()*33) +
                (StringUtils.isEmpty(volume)?0:volume.hashCode()*33) +
                (StringUtils.isEmpty(pages)?0:pages.hashCode()*33);
    }

    @Override
    public String toString() {
        return "type: article\n" +
                "title: " + title + "\n"+
                "author: " + author + "\n"+
                "year: " + year + "\n"+
                "journal: " + pages + "\n"+
                "volume: " + volume;
    }
}
