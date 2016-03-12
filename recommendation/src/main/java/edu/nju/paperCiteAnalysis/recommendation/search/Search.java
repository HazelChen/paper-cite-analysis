package edu.nju.paperCiteAnalysis.recommendation.search;

import edu.nju.classifier.searchEngine.InvertedIndexFactory;
import edu.nju.paperCiteAnalysis.recommendation.common.Article;
import edu.nju.paperCiteAnalysis.recommendation.common.Bibtex;
import edu.nju.paperCiteAnalysis.recommendation.common.Inproceedings;
import edu.nju.tokenAnalyzer.TokenAnalyzer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by margine on 16-3-12.
 */
public class Search {
    private TokenAnalyzer tokenAnalyzer;
    DataHelper dataHelper;
    private static Map<String, List<String>> invertedIndex = InvertedIndexFactory.createInvertedIndex();

    public List<Bibtex> search(String source) {
        ArrayList<String> tmp = new ArrayList<String>(1);
        tmp.add(source);
        return search(tmp);
    }

    public List<Bibtex> search(ArrayList<String> sources) {
        Set<String> rowkeySets = new HashSet<String>();
        List<String> keywords = new ArrayList<String>();
        for (String source : sources){
            keywords.addAll(getSearchKeys(source));
        }
        for (String keyword : keywords){
            List <String> rowkeys = invertedIndex.get(keyword);
            if (rowkeys != null && rowkeys.size() != 0){
               for (String rowkey: rowkeys){
                   //split<rowkey,num>
                   rowkeySets.add(rowkey.substring(0, rowkey.indexOf(",")));
               }
            }
        }
        dataHelper = new DataHelper();
        List<Bibtex> results = dataHelper.getResults(rowkeySets);
        return  results;
    }

    private List<String> getSearchKeys(String source){
        tokenAnalyzer = new TokenAnalyzer();
        //filter symbol
        String[] removeKeys = new String[]{"article", "inproceedings", "author", "title", "year","pages", "journal", "booktitle", "volume"};

        List<String> keys = new ArrayList<String>();
        keys.addAll(tokenAnalyzer.wordSplit(source));
        for (int i = 0; i < removeKeys.length ; i++){
            keys.remove(removeKeys[i]);
        }
        return  keys;
    }

    private class ConvertToBibtex {
        String startSymbol = "={";
        String endSymbol = "},";
        String setStr = "set";
        String article = "article";
        String inproceedings = "inproceedings";

        Bibtex convert(String citeStr) {
            Bibtex bibtex = null;
            /**get cite type*/
            if (citeStr.equals("") || !citeStr.contains("@")) {
            }
            String type = citeStr.substring(citeStr.indexOf("@") + 1, citeStr.indexOf("{"));
            if (article.equals(type)) {
                bibtex = new Article();
            } else if (inproceedings.equals(type)) {
                bibtex = new Inproceedings();
            }

            if (bibtex != null) {
                citeStr = preProcess(citeStr);
                String[] elements = citeStr.split(endSymbol);
                for (String element : elements) {
                    element = element.trim();
                    if (element.equals("") || !element.contains(startSymbol))
                        continue;

                    String propertyName = element.substring(0, element.indexOf(startSymbol));
                    String propertyValue = element.substring(element.indexOf(startSymbol) + startSymbol.length());

                    if (hasField(bibtex.getClass(), propertyName)) {
                        invokeMethod(bibtex.getClass(), bibtex, propertyName, propertyValue);
                    } else {
                        invokeMethod(bibtex.getClass().getSuperclass(),bibtex, propertyName, propertyValue);
                    }
                }
            }
            return bibtex;
        }

        private void invokeMethod(Class o, Bibtex bibtex, String fieldName, String value) {
            Class[] parameterTypes = new Class[1];
            String methodName = null;
            try {
                parameterTypes[0] = String.class;
                StringBuffer sb = new StringBuffer();
                sb.append(setStr);
                sb.append(fieldName.substring(0, 1).toUpperCase());
                sb.append(fieldName.substring(1));
                methodName = sb.toString();
                Method method = o.getDeclaredMethod(methodName, parameterTypes);
                method.invoke(bibtex, new Object[]{value});
            } catch (NoSuchMethodException e) {
                System.err.println("Error: while convert String to bibtex in method " + methodName + " " + e);
                System.exit(-1);
            } catch (IllegalAccessException e) {
                System.err.println("Error: while convert String to bibtex in method " + methodName + " " + e);
                System.exit(-2);

            } catch (InvocationTargetException e) {
                System.err.println("Error: while convert string to bibtex " +e);
                System.exit(-3);

            }
        }

        private boolean hasField(Class<?> clazz, String fieldName) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(fieldName))
                    return true;
            }
            return false;
        }

        private String preProcess(String s) {
            Pattern p = Pattern.compile("\t|\n|\r");
            Matcher m = p.matcher(s);
            s = m.replaceAll("");

            StringBuffer buffer = new StringBuffer(s);
            buffer.setLength(s.length() - 2);
            buffer.append(endSymbol);
            return buffer.substring(buffer.indexOf(",") + 1);
        }
    }

    public static void main(String[] args) {
        Search search = new Search();
        search.search("@article{Bowyer2000IEEETransPatternAnalMachIntell,\n" +
                "\t\t\t  title={A 20th Anniversary Survey: Introduction to 'Content-Based Image Retrieval at the End of the Early Years'},\n" +
                "\t\t\t  author={Kevin W. Bowyer and Patrick J. Flynn},\n" +
                "\t\t\t  journal={IEEE Trans. Pattern Anal. Mach. Intell.},\n" +
                "\t\t\t  year={2000},\n" +
                "\t\t\t  volume={22},\n" +
                "\t\t\t  pages={1348}\n" +
                "\t\t\t}");
    }


}
