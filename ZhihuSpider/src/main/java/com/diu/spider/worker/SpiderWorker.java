package com.diu.spider.worker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import com.diu.spider.fetcher.PageFetcher;
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
		if(Initial.timeArray.size() != 0) {
			unixTime = Initial.timeArray.get(0);
			Initial.timeArray.remove(0);
		}
		System.out.println("Spider-" + threadIndex + ": running...");
		String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(unixTime*1000));
		System.out.println("=====Spider-" + threadIndex + ": time: " + time);
		if(time.equals("2015-10-15"))
		{
			int k = 0;
			k = 1;
		}
		while(!Initial.zhiHuUrlsQueue[threadIndex].isEmpty()) {
			String zhihuUrl = null;
			
			if(Initial.zhiHuUrlsQueue[threadIndex].size() != 0) {
				synchronized (Initial.zhiHuUrlsQueue[threadIndex]) {
					zhihuUrl = Initial.zhiHuUrlsQueue[threadIndex].outElement();
//					zhihuUrl = Initial.zhiHuDynamicUrlQueue.outElement();
				}
			}
			String test = zhihuUrl.substring(zhihuUrl.indexOf("=")+1, zhihuUrl.indexOf("&"));
			Long data_time = new Long(test);
			System.out.println("Spider-" + threadIndex + ": time: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date(data_time*1000)));
            if(zhihuUrl != null && unixTime != null) {
                Object targetData = parser.parseZhiHu(zhihuUrl, unixTime, threadIndex);
                if(targetData != null && targetData != "") {
                	store.storeVczhUrl(targetData.toString());
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

