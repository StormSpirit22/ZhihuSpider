使用JAVA编写的知乎爬虫，根据“轮带逛”这一原理，搜索轮子哥的所有动态，根据关键字来筛选感兴趣的问题。

Tips:

	1.运行程序之前需要在/ZhihuSpider/conf/filter.conf里写入自己需要的关键词，如“轮子哥”，保存。
	2.程序入口/ZhihuSpider/src/main/java/com/diu/spider/SpiderStarter.java，直接运行即可。	
	3.搜索到的问题链接会在/ZhihuSpider/fetchedData/url.txt里~

感谢@BowenBao的意见，项目加入了多线程爬取。项目使用java的线程池管理，开启10个线程分别爬取近100天的动态，每个线程分配10天的量，大概花了6分钟全部爬完~速度感觉还可以^_^

感谢@gaocegege的意见，项目修改为使用maven管理library，果然小了很多~
