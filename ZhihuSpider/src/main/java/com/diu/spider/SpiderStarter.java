package com.diu.spider;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.diu.spider.model.SpiderParams;
import com.diu.spider.queue.UrlQueue;
import com.diu.spider.variables.Initial;
import com.diu.spider.worker.SpiderWorker;

public class SpiderStarter {

//    private static final Logger Log = Logger.getLogger(SpiderStarter.class.getName());

	public static void main(String[] args){
		// 初始化配置参数
		initializeParams();

		// 初始化爬取队列
		initializeQueue();
		
		// 创建worker线程并启动
//		new Thread(new SpiderWorker(1)).start();
		for(int i = 0; i <= Initial.threadNum-1; i++){
			new Thread(new SpiderWorker(i)).start();
		}	
	}

    /**
	 * 初始化配置文件参数
	 */
	private static void initializeParams(){
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream("conf/spider.properties"));
			Properties properties = new Properties();
			properties.load(in);
			
			// 从配置文件中读取参数
			SpiderParams.WORKER_NUM = Integer.parseInt(properties.getProperty("spider.threadNum"));
			SpiderParams.DEYLAY_TIME = Integer.parseInt(properties.getProperty("spider.fetchDelay"));

			in.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 准备初始的爬取链接以及unix时间列表
	 */
	private static void initializeQueue(){
		// 从今天开始往前爬取
		Date date = new Date();
		
		int j = 0;
		for(int i = 0; i < Initial.dayCount; i += 10) {
			
//		    System.out.println(Math.round(date.getTime()/1000));
		    Long unixTime = new Long(Math.round(date.getTime()/1000));
		    Initial.timeArray.add(unixTime);
		    String xsrf = "68a22c4665852a3ed2353bd9a6877010";
		    String url = "http://www.zhihu.com/people/excited-vczh/activities" + "?start=" + 
		    	unixTime.toString() + "&_xsrf=" + xsrf;
		    
		    
		    Initial.zhiHuUrlsQueue[j] = new UrlQueue();
		    Initial.zhiHuUrlsQueue[j].addElement(url);
		    
			Calendar calendar = new GregorianCalendar(); 
		    calendar.setTime(date); 
		    calendar.add(calendar.DATE,-10);
		    date=calendar.getTime();
		    j++;
		}
	}
}
/**
 * 如果不知道在filter.conf里加入什么关键词，试试加入
 * 大胸
 * 胸大
 * 美女
 * 等等...此项目初衷即为此→_→ 之后有空会加入下载图片功能^_^
 */
