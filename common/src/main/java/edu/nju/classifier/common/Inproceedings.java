package edu.nju.classifier.common;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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

        if (StringUtils.isNotEmpty(title) && !title.equals(other.title) ||
                (StringUtils.isEmpty(title) && StringUtils.isNotEmpty(title))){
            return false;
        }
        if (StringUtils.isNotEmpty(year) && !year.equals(other.year) ||
                (StringUtils.isEmpty(year) && StringUtils.isNotEmpty(year))) {
            return false;
        }

        if (StringUtils.isNotEmpty(booktitle) && !booktitle.equals(other.booktitle) ||
                (StringUtils.isEmpty(booktitle) && StringUtils.isNotEmpty(booktitle))) {
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
