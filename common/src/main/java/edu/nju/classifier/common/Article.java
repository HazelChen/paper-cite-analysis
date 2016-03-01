package edu.nju.classifier.common;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

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
        if (StringUtils.isNotEmpty(title) && !title.equals(other.title) ||
                (StringUtils.isEmpty(title) && StringUtils.isNotEmpty(title))){
            return false;
        }
        if (StringUtils.isNotEmpty(year) && !year.equals(other.year) ||
                (StringUtils.isEmpty(year) && StringUtils.isNotEmpty(year))) {
            return false;
        }
        if (StringUtils.isNotEmpty(volume) && !volume.equals(other.volume) ||
                (StringUtils.isEmpty(volume) && StringUtils.isNotEmpty(volume))) {
            return false;
        }
        if (StringUtils.isNotEmpty(pages) && !pages.equals(other.pages) ||
                (StringUtils.isEmpty(pages) && StringUtils.isNotEmpty(pages))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode(){
        return (StringUtils.isEmpty(title)?0:title.hashCode()*33) +
                (StringUtils.isEmpty(author)?0:author.hashCode()*33) +
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
                "volume: " + volume + "\n";
    }

    public static void main(String[] args) {
        Article a = new Article();
        a.setVolume("abs/1301.6939");
        Article b = new Article();
        b.setVolume("CoRR, abs/1301.6939");

        System.out.print(a.equals(b));
    }
}
