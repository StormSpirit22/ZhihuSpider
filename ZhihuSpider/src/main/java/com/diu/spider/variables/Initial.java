package com.diu.spider.variables;

import java.util.ArrayList;

import com.diu.spider.queue.UrlQueue;

public class Initial {
    public static UrlQueue WebUrlQueue = new UrlQueue();
    
//    public static UrlQueue zhiHuDynamicUrlQueue = new UrlQueue();
    
    public static int REQUEST_COUNT = 3;
    
    public static int dayCount = 100;										//总共从当前时间往前爬100天
    public static int threadNum = 10;
    public static ArrayList<Long> timeArray = new ArrayList<Long>();		//unix时间戳数组，分给线程分别去爬
    
    public static ArrayList<String> tinyFoxUrls = new ArrayList<String>(); 	//已经爬取到的urls
    
    public static UrlQueue[] zhiHuUrlsQueue = new UrlQueue[threadNum];

}
