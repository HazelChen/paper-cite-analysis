package edu.nju.classifier.common;

import lombok.Data;

/**
 * Created by nathan on 16-2-27.
 */
@Data
public class MatchScore implements Comparable<MatchScore> {
    private String rowKey;
    private double score;

    public int compareTo(MatchScore o) {
        if(score < o.score)
            return +1;
        else if(score > o.score)
            return -1;
        else
            return 0;
    }
}
