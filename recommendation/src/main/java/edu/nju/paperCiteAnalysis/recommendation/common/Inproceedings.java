package edu.nju.paperCiteAnalysis.recommendation.common;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by nathan on 16-2-27.
 */
@Data
public class Inproceedings extends Bibtex {
    private String booktitle;

    public Inproceedings() {}

    public Inproceedings(String author, String title, String year,
                   String booktitle) {

        super(author, title, year);

        this.booktitle = booktitle;
    }

    @Override
    public boolean equals(Object o){
        if (o == null || !(o instanceof Inproceedings))
            return false;
        Inproceedings other = (Inproceedings)o;

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
        if ((StringUtils.isEmpty(booktitle) && StringUtils.isNotEmpty(booktitle)) ||
                (StringUtils.isNotEmpty(booktitle) && StringUtils.isNotEmpty(booktitle) && !booktitle.equals(other.booktitle))){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        return (StringUtils.isEmpty(title)?0:title.hashCode()*33) +
                (StringUtils.isEmpty(author)?0:author.hashCode()*33) +
                (StringUtils.isEmpty(year)?0:year.hashCode()*33) +
                (StringUtils.isEmpty(booktitle)?0:booktitle.hashCode()*33);
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
