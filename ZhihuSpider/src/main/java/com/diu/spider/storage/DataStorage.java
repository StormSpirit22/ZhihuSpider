package com.diu.spider.storage;

import java.io.*;

import com.diu.spider.variables.Initial;


public class DataStorage {

    public void storeVczhUrl(Object data) {   //, String comicName

        try{
            String filePath = "fetchedData\\url.txt";
            File file = new File(filePath);
            
            if(Initial.tinyFoxUrls.contains(data.toString()))return;
            synchronized(Initial.tinyFoxUrls) {
            	Initial.tinyFoxUrls.add(data.toString());
            }
	                       
            BufferedWriter write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true),"UTF-8"));
            synchronized(write) {
	            write.write(data.toString());
	            write.flush();
            }
            write.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

}
