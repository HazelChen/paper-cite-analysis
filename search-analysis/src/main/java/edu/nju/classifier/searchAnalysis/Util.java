package edu.nju.classifier.searchAnalysis;

/**
 * Created by margine on 16-2-28.
 */
public class Util {
    private static String preps = "and";
    public static String longestCommonSubstring(String strA, String strB) {
        char[] chars_strA = strA.toCharArray();
        char[] chars_strB = strB.toCharArray();
        int m = chars_strA.length;
        int n = chars_strB.length;
        int[][] matrix = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (chars_strA[i - 1] == chars_strB[j - 1])
                    matrix[i][j] = matrix[i - 1][j - 1] + 1;
                else
                    matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i - 1][j]);
            }
        }

        char[] result = new char[matrix[m][n]];
        int currentIndex = result.length - 1;
        while (matrix[m][n] != 0) {
            if (matrix[m][n] == matrix[m][n - 1])
                n--;
            else if (matrix[m][n] == matrix[m - 1][n])
                m--;
            else {
                result[currentIndex] = chars_strA[m - 1];
                currentIndex--;
                n--;
                m--;
            }
        }
        return new String(result);
    }

    public static String removeSign(String str) {
        String newStr = str.replace(preps, "");
        StringBuffer sb = new StringBuffer();
        for (char item : newStr.toCharArray())
            if (charReg(item)){
                sb.append(item);
            }
        return sb.toString().replace("and", "");

    }



    private static boolean charReg(char charValue) {
        return (charValue >= 'a' && charValue <= 'z')
                || (charValue >= 'A' && charValue <= 'Z')
                || (charValue >= '0' && charValue <= '9');
    }

}
