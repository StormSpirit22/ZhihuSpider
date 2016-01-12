package com.diu.spider.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ContentFilter {
    private static ContentFilter filter = null;

    private List<String> strToMatch = null;
    private ContentFilter() {
        strToMatch = new ArrayList<String>();
        File file = new File("conf/filter.conf");
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                strToMatch.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ContentFilter getInstance() {
        if (filter == null) {
            filter = new ContentFilter();
        }
        return filter;
    }

    public boolean filter(String content) {
        for (String str : strToMatch) {
            if (content.contains(str)) {
                return true;
            }
        }
        return false;
    }
}
