package com.diu.spider.worker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import com.diu.spider.model.SpiderParams;
import com.diu.spider.parser.ContentParser;
import com.diu.spider.storage.DataStorage;
import com.diu.spider.variables.Initial;

public class SpiderWorker implements Runnable{
	
	private static final Logger Log = Logger.getLogger(SpiderWorker.class.getName());
	private ContentParser parser;
	private DataStorage store;
	private int threadIndex;
	
	public SpiderWorker(int threadIndex){
		this.threadIndex = threadIndex;
		this.parser = new ContentParser();
		this.store = new DataStorage();			
		
	}
	
		
	public void run() {
		Long unixTime = null;
		if(Initial.timeArray.size() == 0){
			System.out.println("SpiderWorker 30: null timeArray");
		}
		synchronized(Initial.timeArray) {
			if(Initial.timeArray.size() != 0 && !Initial.threadTimeArray.contains(threadIndex)) {
				unixTime = Initial.timeArray.get(0);
				Initial.timeArray.remove(0);
				Initial.threadTimeArray.add(threadIndex);
//				System.out.println(">>>>>>>>>>>>>>>>>thread " + threadIndex + " :" + Initial.timeArray.size() + ">>>>>>>>>>>>>>>>>>");
			}
		}
		
//		System.out.println("Spider-" + threadIndex + ": running...");
		if(unixTime != null)
		{
			String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(unixTime*1000));
			System.out.println("=====Spider-" + threadIndex + ": time: " + time);
		}
		else System.out.println("SpiderWorker 44: null unixTime");
		//为什么一个线程拿不到应有的url?Initial.zhiHuUrlsQueue[threadIndex]都是空
		while(!Initial.zhiHuUrlsQueue[threadIndex].isEmpty()) {
			String zhihuUrl = null;
			
			if(Initial.zhiHuUrlsQueue[threadIndex].size() != 0) {
				synchronized (Initial.zhiHuUrlsQueue[threadIndex]) {
					zhihuUrl = Initial.zhiHuUrlsQueue[threadIndex].outElement();
				}
			}
			//data_time截取zhihuUrl的starttime，zhihuUrl=http://zhihu...?start=data_time&_xsrf=...
			if(zhihuUrl == null || zhihuUrl == "" || zhihuUrl.indexOf("=")+1 > zhihuUrl.indexOf("&"))break;
			Long data_time = new Long(zhihuUrl.substring(zhihuUrl.indexOf("=")+1, zhihuUrl.indexOf("&")));
			System.out.println("SpiderWorker rows 61: Spider-" + threadIndex + ": time: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date(data_time*1000)));
            if(zhihuUrl != null && unixTime != null) {
                HashMap<String, String> result = parser.parseZhiHu(zhihuUrl, unixTime, threadIndex);
                
                String targetData = null, moreContentUrl = null, resultContentUrl = null;                
                //遍历map中的键
                for (String key : result.keySet()) {
                    targetData = key;
                }
                //遍历map中的值
                for (String value : result.values()) {
                	moreContentUrl = value;
                }
                
                if(targetData != null && targetData != "") {
                	store.storeVczhUrl(targetData.toString());
                }
                
                if(moreContentUrl != null & moreContentUrl != "") {
            		synchronized (Initial.zhiHuUrlsQueue[threadIndex]) {
            			Initial.zhiHuUrlsQueue[threadIndex].addElement(moreContentUrl);
            		}
                }
                
            }
		}
            
		try {
            Thread.sleep(SpiderParams.DEYLAY_TIME);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.info("Spider-" + threadIndex + ": stop...");
		
        }

}

