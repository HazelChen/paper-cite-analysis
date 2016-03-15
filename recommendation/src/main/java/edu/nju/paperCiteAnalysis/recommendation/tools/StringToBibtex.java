package edu.nju.paperCiteAnalysis.recommendation.tools;

import edu.nju.paperCiteAnalysis.recommendation.common.Article;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.common.Inproceedings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringToBibtex {
    private static final String START_SYMBOL = "={";
    private static final String END_SYMBOL = "},";
    private static final String SET_STR = "set";
    private static final String ARTICLE = "article";
    private static final String INPROCEEDINGS = "inproceedings";

    public static Bibtex convert(String citeStr) {
        Bibtex bibtex = null;
        /**get cite type*/
        if (citeStr.equals("") || !citeStr.contains("@")) {
            return null;
        }
        String type = citeStr.substring(citeStr.indexOf("@") + 1, citeStr.indexOf("{"));
        if (ARTICLE.equals(type.toLowerCase())) {
            bibtex = new Article();
        } else if (INPROCEEDINGS.equals(type)) {
            bibtex = new Inproceedings();
        }

        if (bibtex != null) {
            citeStr = preProcess(citeStr);
            String[] elements = citeStr.split(END_SYMBOL);
            for (String element : elements) {
                element = element.trim();
                if (element.equals("") || !element.contains(START_SYMBOL))
                    continue;

                String propertyName = element.substring(0, element.indexOf(START_SYMBOL));
                String propertyValue = element.substring(element.indexOf(START_SYMBOL) + START_SYMBOL.length());

                if (hasField(bibtex.getClass(), propertyName)) {
                    invokeMethod(bibtex.getClass(), bibtex, propertyName, propertyValue);
                } else {
                    invokeMethod(bibtex.getClass().getSuperclass(), bibtex, propertyName, propertyValue);
                }
            }
        }
        return bibtex;
    }

    private static void invokeMethod(Class o, Bibtex bibtex, String fieldName, String value) {
        Class[] parameterTypes = new Class[1];
        String methodName = null;
        try {
            parameterTypes[0] = String.class;
            methodName = SET_STR +
                    fieldName.substring(0, 1).toUpperCase() +
                    fieldName.substring(1);
            Method method = o.getDeclaredMethod(methodName, parameterTypes);
            method.invoke(bibtex, new Object[]{value});
        } catch (NoSuchMethodException e) {
            System.err.println("Error: while convert String to bibtex in method " + methodName + " " + e);
            System.exit(-1);
        } catch (IllegalAccessException e) {
            System.err.println("Error: while convert String to bibtex in method " + methodName + " " + e);
            System.exit(-2);

        } catch (InvocationTargetException e) {
            System.err.println("Error: while convert string to bibtex " + e);
            System.exit(-3);

        }
    }

    private static boolean hasField(Class<?> clazz, String fieldName) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName))
                return true;
        }
        return false;
    }

    private static String preProcess(String s) {
        Pattern p = Pattern.compile("\t|\n|\r");
        Matcher m = p.matcher(s);
        s = m.replaceAll("");

        StringBuilder buffer = new StringBuilder(s);
        buffer.setLength(s.length() - 2);
        buffer.append(END_SYMBOL);
        return buffer.substring(buffer.indexOf(",") + 1);
    }
}
