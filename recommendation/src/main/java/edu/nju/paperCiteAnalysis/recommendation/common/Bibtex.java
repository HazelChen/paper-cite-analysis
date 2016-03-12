package edu.nju.paperCiteAnalysis.recommendation.common;

import lombok.Data;

/**
 * Created by nathan on 16-2-27.
 * Bibtex
 */
@Data
public abstract class Bibtex {
    protected String author;
    protected String title;
    protected String year;

    public Bibtex() {}

    public Bibtex(String author, String title, String year) {
        this.author = author;
        this.title = title;
        this.year = year;
    }
}
