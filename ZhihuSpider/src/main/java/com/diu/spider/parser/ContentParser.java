package com.diu.spider.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.diu.spider.fetcher.PageFetcher;
import com.diu.spider.model.FetchedPage;
import com.diu.spider.storage.DataStorage;
import com.diu.spider.variables.Initial;

public class ContentParser {
	
	public Object parseZhiHu(String url, Long unixTime, int threadIndex){
 		HashSet<String> contentSet = new HashSet<String>();
		DataStorage store = new DataStorage();
		Object targetObject = "";
        Document doc = null;
        String data_time = String.valueOf(unixTime);
        Date tmpDate = new Date(Long.valueOf(data_time)*1000);
        Calendar calendar = new GregorianCalendar(); 
	    calendar.setTime(tmpDate); 
	    calendar.add(calendar.DATE,-10);//把日期往后增加一天.整数往后推,负数往前移动 
	    tmpDate=calendar.getTime();   //这个时间就是日期往后推一天的结果 
        Long end_time = new Long(Math.round(tmpDate.getTime()/1000));
        
        if(url.contains("http://www.zhihu.com/people/excited-vczh"))
        {
        	FetchedPage fetchedPage2 = new FetchedPage();
        	PageFetcher pageFetcher = new PageFetcher();
        	
        	String moreContentUrl = "http://www.zhihu.com/people/excited-vczh/activities";
        	//每次重新爬取xsrf值作为参数不一定起作用，于是乎将一个一直可以使用的常量直接赋值给它
        	String xsrf = "68a22c4665852a3ed2353bd9a6877010";//doc.select("input[name=_xsrf]").get(0).attr("value").toString();
        	String content = sendRequest(url, "get");
        	if(content == null)return null;
        	
        	Elements dynamicsList = doc.select("div.zm-profile-section-item");//doc.getElementsByClass("zm-profile-section-item");
        	for(Element dynamic : dynamicsList) {
        		Elements questionLink = dynamic.getElementsByClass("question_link");
        		String questionContent = questionLink.text();
        		if(ContentFilter.getInstance().filter(questionContent) && // read keywords from file.
                        contentSet.add(questionContent)) {
        			String questionUrl = questionLink.attr("href").toString();
        			//questionLinkAddress为符合条件的url
        			questionUrl = "http://www.zhihu.com" + questionUrl.subSequence(0, questionUrl.substring(0,questionUrl.lastIndexOf("/")).lastIndexOf("/")).toString();
        			//准备爬取链接中包含的图片
        			Initial.resultUrlsQueue.addElement(questionContent + ":" + questionUrl);
        			targetObject = targetObject + questionContent + ":" + questionUrl + "\n"; 
        		}
        	}
        	data_time = dynamicsList.get(dynamicsList.size()-1).attr("data-time");
        	if(Long.valueOf(data_time) >= end_time) {
        		moreContentUrl += "?start=" + data_time + "&_xsrf=" + xsrf;
        		synchronized (Initial.zhiHuUrlsQueue[threadIndex]) {
        			Initial.zhiHuUrlsQueue[threadIndex].addElement(moreContentUrl);
        		}
        		
        	}
        	
        	
      
        	//Login
//        	String loginActionUrl = "http://www.zhihu.com/login/email";
//        	HashMap<String, String> loginParameters = new HashMap<String, String>();
//        	loginParameters.put("email", "XXXX");
//        	loginParameters.put("password", "XXXX");
//        	loginParameters.put("_xsrf", "a8891170a5766a1ca884d188f4d6e3c3");
//        	loginParameters.put("remember_me", "true");
//        	pageFetcher.sendPostToUrl(loginActionUrl, loginParameters);
        	
        }
        else targetObject = "hello world";

		return targetObject;
	}
	
	
	public void analysisAnswer() {
		while(Initial.resultUrlsQueue.size() > 0) {
			String url = Initial.resultUrlsQueue.outElement();
			String questionContent = url.substring(0, url.indexOf(":"));
			String questionUrl = url.substring( url.indexOf(":") + 1);
			
			String content = sendRequest(questionUrl, "get");
			if(content == null)continue;
			
        	Document doc = Jsoup.parse(content);
        	Elements answerList = doc.select("a.zg-anchor-hidden");
        	for(int i = 0; i < 10; i++) {
        		Element answer = answerList.get(i);
        		String answerContent = answer.getElementsByClass("zm-editable-content clearfix").text();
        		
        	}
		}
	}
	
	public void downloadImg(String urlString) {
		
		try {
			  // 构造URL  
	        URL url = new URL(urlString);  
	        // 打开连接  
	        URLConnection con = url.openConnection();  
	        //设置请求超时为5s  
	        con.setConnectTimeout(5*1000);  
	        // 输入流  
	        InputStream is = con.getInputStream();  
	      
	        // 1K的数据缓冲  
	        byte[] bs = new byte[1024];  
	        // 读取到的数据长度  
	        int len;  
	        // 输出的文件流  
	        String savePath = "";
	        File sf=new File(savePath);  
	        if(!sf.exists()){
	            sf.mkdirs();  
	        }
	        String filename = "";
	        OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);  
	        // 开始读取  
	        while ((len = is.read(bs)) != -1) {
	            os.write(bs, 0, len);  
	        }  
	        // 完毕，关闭所有链接  
	        os.close();  
	        is.close();  
		}
		catch(Exception e) {
			e.printStackTrace();
		}
    }   
	
	public String sendRequest(String url, String request) {
		
		FetchedPage fetchedPage = new FetchedPage();
    	PageFetcher pageFetcher = new PageFetcher();
    	
    	if(request.equalsIgnoreCase("get")) {
    		fetchedPage = pageFetcher.getRequest(url);
    	}
    	if(request.equalsIgnoreCase("post")) {
    		fetchedPage = pageFetcher.postRequest(url);
    	}
    	else return null;
    	
    	String content = fetchedPage.getContent();
    	if(content == null || content.equals(""))return null;
    	content = content.replaceAll("\\\\\"", "").replaceAll("\\\\n", "<br>");
    	content = content.replaceAll("\\\\", "");
    	return content;
    	
	}
	
	public Object parse(FetchedPage fetchedPage){
		Object targetObject = "";
        Document doc;
        if(fetchedPage.getContent() != null)
        {
		     doc = Jsoup.parse(fetchedPage.getContent());//解析HTML字符串返回一个Document实现
        }
        else
        {
            return "";
        }
		return targetObject;
	}
	
	private boolean containsTargetData(String url, Document contentDoc){
		// 通过URL判断
		// TODO
		
		// 通过content判断，比如需要抓取class为grid_view中的内容
		if(contentDoc.getElementsByClass("grid_view") != null){
			System.out.println(contentDoc.getElementsByClass("grid_view").toString());
			return true;
		}
		
		return false;
	}

}