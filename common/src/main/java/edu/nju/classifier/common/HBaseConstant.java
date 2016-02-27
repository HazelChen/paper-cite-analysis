package edu.nju.classifier.common;

public class HBaseConstant {

    public static final String REGION_NAME = "cite";

    public static final String BIBTEX = "Bibtex";
    public static final String MLA = "MLA";
    public static final String APA = "APA";
    public static final String CHICAGO = "Chicago";

    public static String getFileName(String column) {
        return column.toLowerCase();
    }
}
