package edu.nju.classifier.searchAnalysis;

/**
 * Created by margine on 16-2-28.
 */
public class Weight {

    protected static final int TITLE_WEIGHT = 5;
//    protected static final int AUTHOR_WEIGHT = 3;
    protected static final int YEAR_WEIGHT = 1;
    protected static final int PAGES_WEIGHT = 1;
    protected static final int VOLUME_WEIGHT = 1;
    protected static final int JOURNAL_WEIGHT = 2;
    protected static final int BOOKTITLE_WEIGHT = 2;
    protected static final int TYPE_WEIGHT = 1;

    protected static final int INPROCCEDING_TOTAL_WEIGHT = TITLE_WEIGHT + YEAR_WEIGHT + BOOKTITLE_WEIGHT + TYPE_WEIGHT;
    protected static final int ARTICLE_TOTAL_WEIGHT = TITLE_WEIGHT  + YEAR_WEIGHT + PAGES_WEIGHT + VOLUME_WEIGHT + JOURNAL_WEIGHT + TYPE_WEIGHT;
}
