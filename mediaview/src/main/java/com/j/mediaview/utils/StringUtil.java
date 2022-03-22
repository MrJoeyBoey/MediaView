package com.j.mediaview.utils;

import java.util.List;

public class StringUtil {

    public static String arrayToString(String[] array, String separator){
        StringBuilder sb = new StringBuilder();
        if (array != null) {
            for (String s : array) {
                sb.append(s).append(separator);
            }
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.lastIndexOf(separator));
        return sb.toString();
    }

}
