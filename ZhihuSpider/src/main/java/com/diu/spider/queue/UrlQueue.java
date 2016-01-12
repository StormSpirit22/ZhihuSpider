package com.diu.spider.queue;

import java.util.LinkedList;

public class UrlQueue {
	private  LinkedList<String> urlQueue = new LinkedList<String>();

	public synchronized  void addElement(String url){
		urlQueue.add(url);
	}
	
	public synchronized  void addFirstElement(String url){
		urlQueue.addFirst(url);
	}
	
	public synchronized  String outElement(){
		return urlQueue.removeFirst();
	}
	
	public synchronized  boolean isEmpty(){
		return urlQueue.isEmpty();
	}
	
	public  int size(){
		return urlQueue.size();
	}
	
	public  boolean isContains(String url){
		return urlQueue.contains(url);
	}

    public  String get(int index)
    {
        return urlQueue.get(index);
    }

}
