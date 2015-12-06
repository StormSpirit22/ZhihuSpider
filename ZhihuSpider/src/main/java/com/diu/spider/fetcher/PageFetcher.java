package com.diu.spider.fetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.diu.spider.model.FetchedPage;
import com.diu.spider.variables.Initial;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class PageFetcher {
	private static final Logger Log = Logger.getLogger(PageFetcher.class.getName());
	private HttpClient client;
	
	/**
	 * 创建HttpClient实例，并初始化连接参数
	 */
	@SuppressWarnings("deprecation")
	public PageFetcher(){
		// 设置超时时间
		@SuppressWarnings("deprecation")
		HttpParams params = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
	    HttpConnectionParams.setSoTimeout(params, 10 * 1000);	    
		client = new DefaultHttpClient(params);
	}
	
	/**
	 * 主动关闭HttpClient连接
	 */
	public void close(){
		client.getConnectionManager().shutdown();
	}
	
	/**
	 * 根据url爬取网页内容
	 * @param url
	 * @return
	 */
	public FetchedPage getContentFromUrl(String url){
		String content = null;
		int statusCode = 500;
		
		// 创建Get请求，并设置Header


            try{
//                String encodedUrl =
//                        String.format("http://something.com/%s/%s",
//                                URLEncoder.encode("test", "UTF-8"),
//                                URLEncoder.encode("anything 10%-20% 04-03-07", "UTF-8")
//                );
//            url = "http://apk.gfan.com/Product/App287697.html###";
            HttpGet getHttp = new HttpGet(url);
            getHttp.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
            HttpResponse response;


                // 获得信息载体
                response = client.execute(getHttp);
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();

                if(entity != null){
                    // 转化为文本信息, 设置爬取网页的字符集，防止乱码
                    content = EntityUtils.toString(entity, "UTF-8");
                }
        }

		catch(Exception e){
			e.printStackTrace();

            if(isConnect(url))      //该网页是否可以被访问（已失效或者超时问题引起的不可访问）
            {
                // 因请求超时等问题产生的异常，将URL放回待抓取队列，重新爬取
                Log.info(">> Put back url: " + url);
                Initial.WebUrlQueue.addFirstElement(url);//UrlQueue
            }

			//else do nothing;

		}

		return new FetchedPage(url, content, statusCode);
	}
	
	/**
	 * 向网页传输POST请求
	 * @param url
	 * @return
	 */
	public FetchedPage sendPostToUrl(String url, HashMap <String, String> parameters){
		
		String content = null;
		int statusCode = 500;
		
        try {
        	
	        HttpPost httpPost = new HttpPost(url);
	        
	        httpPost.setHeader("(Request-Line)","POST /login HTTP/1.1");  
	        httpPost.setHeader("Accept","*/*");  
	        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36");    
	        httpPost.setHeader("Referer", "http://www.zhihu.com/people/excited-vczh");
	        httpPost.setHeader("Origin", "http://www.zhihu.com");
	        httpPost.setHeader("Host", "www.zhihu.com"); 
	        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");    //
	        httpPost.setHeader("Accept-Language","zh-CN,zh;q=0.8");  
	//      httpPost.setHeader("Accept-Encoding","gzip, deflate");  
	        
	        httpPost.setHeader("Connection","keep-alive");
	        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
	        List <NameValuePair> params = new ArrayList<NameValuePair>();
	    	
	        Iterator iter = parameters.entrySet().iterator();
	        while (iter.hasNext()) {
	        	Map.Entry entry = (Map.Entry) iter.next(); 
	        	Object key = entry.getKey();
	        	Object val = entry.getValue();
	        	params.add(new BasicNameValuePair(key.toString(), val.toString()));
	        }
    	
	    	try {
	    		httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
	    		HttpResponse httpResponse = client.execute(httpPost);
	    		httpResponse.setHeader("Content-Type", "application/json");
	    		httpResponse.setHeader("Content-Encoding", "gzip");
			    if(httpResponse.getStatusLine().getStatusCode() == 200) {
			    	HttpEntity entity = httpResponse.getEntity();
			    	if(entity != null) {
	                   	//转化为文本信息, 设置爬取网页的字符集，防止乱码
			    		content = EntityUtils.toString(entity, "UTF-8");
			    		JSONObject jsonObject = new JSONObject(content);
			    		content = jsonObject.get("msg").toString();
			    		content = content.substring(0, content.length()-2).substring(5);
	               }
			    	
			    }
		    
			    // 获得信息载体
			    statusCode = httpResponse.getStatusLine().getStatusCode();
            
	    	} catch (UnsupportedEncodingException e) {
	    		e.printStackTrace();
	    		content = e.getMessage().toString();
	    	} catch (ClientProtocolException e) {
	    		e.printStackTrace();
	    		content = e.getMessage().toString();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    		content = e.getMessage().toString();
	    	}
        } catch(Exception e){
        	e.printStackTrace();

            if(isConnect(url))      //该网页是否可以被访问（已失效或者超时问题引起的不可访问）
            {
                // 因请求超时等问题产生的异常，将URL放回待抓取队列，重新爬取
                Log.info(">> Put back url: " + url);
                Initial.WebUrlQueue.addFirstElement(url);//UrlQueue
            }

			//else do nothing;
		}

		return new FetchedPage(url, content, statusCode);
	}

	private static String convertStreamToString(InputStream is) {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
    public static synchronized boolean isConnect(String urlStr) {
        int counts = 0;
        boolean isValid = false;
        if (urlStr == null || urlStr.length() <= 0) {
            return false;
        }
        while (counts < Initial.REQUEST_COUNT) {
//            long start = 0;
            try {
                URL url = new URL(urlStr);
//                start = System.currentTimeMillis();
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                int state = con.getResponseCode();
                if (state == 200) {
                    isValid = true;
//                    Log.info(urlStr+"--可用");
                    break;
                }
                break;
            }catch (Exception ex) {
                counts++;
//                Log.info("请求断开的URL一次需要："+(System.currentTimeMillis()-start)+"毫秒");
//                Log.info("连接第 "+counts+" 次，"+urlStr+"--不可用");
                continue;
            }
        }
        return isValid;
    }
}
