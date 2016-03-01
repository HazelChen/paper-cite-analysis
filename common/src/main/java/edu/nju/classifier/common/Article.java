package edu.nju.classifier.common;

import lombok.Data;

/**
 * Created by nathan on 16-2-27.
 */
@Data
public class Article extends Bibtex {
    private String journal;
    private String pages;
    private String volume;

    @Override
    public boolean equals(Object o){
        if (o == null || !(o instanceof Article))
            return false;
        Article other = (Article) o;
        if (title != null && !title.equals(other.title) ||
                (title == null && other.title != null)){
            return false;
        }
        if (year != null && !year.equals(other.year) ||
                (year == null && other.year != null)){
            return false;
        }
        if (journal != null && !journal.equals(other.journal) ||
                (journal == null && other.journal != null)){
            return false;
        }
        if (volume != null && !volume.equals(other.volume) ||
                (volume == null && other.volume != null)){
            return false;
        }

        if (pages != null && !pages.equals(other.pages) ||
                (pages == null && other.pages != null)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        return title.hashCode()*33 +
                author.hashCode()*33 +
                year.hashCode()*33 +
                journal.hashCode()*33+
                volume.hashCode()*33+
                pages.hashCode()*33;
    }

    @Override
    public String toString() {
        return "type: article\n" +
                "title: " + title + "\n"+
                "author: " + author + "\n"+
                "year: " + year + "\n"+
                "journal: " + pages + "\n"+
                "volume: " + volume + "\n";
    }

}
