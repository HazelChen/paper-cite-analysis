package edu.nju.classifier.common;

import lombok.Data;

/**
 * Created by nathan on 16-2-27.
 */
@Data
public abstract class Bibtex {
    protected String author;
    protected String title;
    protected String year;
}
