package edu.nju.classifier.common;

import lombok.Data;

/**
 * Created by nathan on 16-2-27.
 */
@Data
public class Inproceedings extends Bibtex {
    private String booktitle;

    @Override
    public boolean equals(Object o){
        if (o == null || !(o instanceof Inproceedings))
            return false;
        Inproceedings other = (Inproceedings)o;
        if (title != null && !title.equals(other.title) ||
                (title == null && other.title != null)){
            return false;
        }
        if (year != null && !year.equals(other.year) ||
                (year == null && other.year != null)){
            return false;
        }
        if (booktitle != null && !booktitle.equals(other.booktitle) ||
                (booktitle == null && other.booktitle != null)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        return title.hashCode()*33 +
                author.hashCode()*33 +
                year.hashCode()*33 +
                booktitle.hashCode()*33;
    }

    @Override
    public String toString() {
        return "type: article\n" +
                "title: " + title + "\n"+
                "author: " + author + "\n"+
                "year: " + year + "\n"+
                "booktitle: " + booktitle + "\n";
    }
}
