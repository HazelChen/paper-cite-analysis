package edu.nju.classifier.common;

import lombok.Data;

/**
 * Created by nathan on 16-2-27.
 */
@Data
public class Article extends Bibtex{
    private String journal;
    private String pages;
    private String volume;
}
