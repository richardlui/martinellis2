package com.martinellis.rest.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StringUtil {
	/**
     * Input string is a list of key value pairs separated by comma.
     * e.g. 2:allstate,4001:unilever
     * @param str
     * @return a HashMap
     */
    public static Map<String, String> splitIntoMap(String str) {
        if (str == null) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String pair : str.split(",")) {
            String clean = pair.trim();
            String[] kv = clean.split(":");
            if (kv.length == 2) {
                result.put(kv[0].trim(), kv[1].trim());
            }
        }
        return result;
    }
    
    public static List<String> splitIntoList(String str) {
        if (str == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<String>();
        for (String element : str.split(",")) {
            String clean = element.trim();
            result.add(clean);
        }
        return result;
    }
}
