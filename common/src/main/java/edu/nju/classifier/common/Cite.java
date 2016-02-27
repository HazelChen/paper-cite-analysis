package edu.nju.classifier.common;

import lombok.Data;

@Data
public class Cite {
    private String bibtex;
    private String MLA;
    private String APA;
    private String Chicago;

    public Cite(String bibtex, String MLA, String APA, String Chicago) {
        this.bibtex = bibtex;
        this.MLA = MLA;
        this.APA = APA;
        this.Chicago = Chicago;
    }

    public String getBibtex() {
        return bibtex;
    }

    public String getMLA() {
        return MLA;
    }

    public String getAPA() {
        return APA;
    }

    public String getChicago() {
        return Chicago;
    }

}