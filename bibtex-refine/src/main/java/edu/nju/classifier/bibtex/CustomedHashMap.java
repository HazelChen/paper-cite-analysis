package edu.nju.classifier.bibtex;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by margine on 16-1-6.
 */
public class CustomedHashMap extends HashMap {
    @Override
    public Object get(Object key) {
       Object val = super.get(key);
      return  val == null ? "undefined" : val;
    }
}
