package com.cdnspider.spider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.csdnspider.entity.Post;
import com.csdnspider.entity.SpiderReport;
import com.csdnspider.hbase.HBaseProcess;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

//CSDN论坛热帖-技术区历史数据爬取：http://bbs.csdn.net/tech_hot_topics
public class CSDNHistoricalSpider implements PageProcessor {

	//重试次数
	private static final int RETRY_TIMES=3;
	//抓取间隔
	private static final int INTERVAL_TIME=2000;
	//超时时间
	private static final int TIME_OUT=20000;
	
	private Site site=Site.me().setTimeOut(TIME_OUT).setRetryTimes(RETRY_TIMES).setSleepTime(INTERVAL_TIME).setCharset("UTF-8");
	
	private static final String URL_LIST="http://bbs.csdn.net/tech_hot_topics";
	
	private static int pageNumber=1;
	
	private static int count=0;
	
	//执行批次数
	private static int batch=0;
	
	//爬取帖子总数
	private static int number=0;
	
	private static boolean state=true;
	
	private static List<Post> posts=new ArrayList<Post>(10);
	
	private static final int BATCH_SIZE=10;
	
	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		Set<Integer> acceptStatCode=new HashSet<Integer>();
		acceptStatCode.add(200);
		site=site.setAcceptStatCode(acceptStatCode).addHeader("Accept-Encoding", "/")
				.setUserAgent(UserAgentUtils.radomUserAgent());
		return site;
	}
	
	////定制爬虫逻辑的核心接口，在这里编写抽取逻辑
	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		//列表页正则匹配表达式
		String listPage="^http://bbs.csdn.net/tech_hot_topics(|[?]page=\\d)$";
		//爬取列表页
		if(page.getUrl().regex(listPage).match()&&pageNumber<=2) {
			try {
				Thread.sleep(3000);
				page.addTargetRequests(page.getHtml().xpath("//*[@class=\"list_1\"]/ul/li/a/@href").all());
				//模拟get请求
				Request request=new Request();
				request.setMethod(HttpConstant.Method.GET);
				pageNumber++;
				request.setUrl(URL_LIST+"?page="+ pageNumber);
				page.addTargetRequest(request);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//爬取帖子页
		else {
			//获得帖子链接
			String url=page.getUrl().get();
			//System.out.println(url);
			//获得帖子ID
			String id=url.split("/")[url.split("/").length-1];
			//System.out.println(id);
			//爬取帖子类别
			String typeName="";
			List<String> types=page.getHtml().xpath("//div[@class='bread_nav']/a/text()").all();
			int n=types.size();
			for(int i=0;i<n;i++) {
				if(i<n-1) {
					typeName=typeName+types.get(i)+"/";
				}
				else {
					typeName=typeName+types.get(i);
				}
			}
			//System.out.println(typeName);
			//爬取帖子标题
			String title=page.getHtml().xpath("//span[contains(@class, 'title text_overflow']/text()").get().trim();
			//System.out.println(title);
			//爬取提问内容
			String content=page.getHtml().xpath("//*div[@class='detailed']/table[@class='post topic ']//div[@class='post_body']/text()").get().trim();
			//System.out.println(content);
			//爬取帖子提问时间
			String time=page.getHtml().xpath("//*div[@class='detailed']//div[@class='control]/span[@class='time']/text()").get().trim();
			String regex="发表于： ";
			time=time.split(regex)[1];
			//System.out.println(time);
			//爬取回复次数
			String responseCount=page.getHtml().xpath("//*div[@class='detailed']//div[@class='control]/span[@class='return_time']/text()").get().trim();
			responseCount=responseCount.split("回复次数：")[1];
			//System.out.println(responseCount);
			//爬取所有回复内容
			List<String> response=page.getHtml().xpath("//*div[@class='detailed']/table[@class='post  ']//div[@class='post_body']/text()").all();
			Post post=new Post();
			post.setPostId(Long.valueOf(id));
			post.setPostTitle(title);
			post.setPostUrl(url);
			post.setPostType(typeName);
			post.setPostContent(content);
			post.setPostTime(time);
			post.setResponseCount(Long.valueOf(responseCount));
			post.setResponse(response);
			if(count<BATCH_SIZE) {
				posts.add(post);
				count++;
			}
			else {
				batch++;
				List<Post> temp=new ArrayList<Post>(posts);
				number=number+temp.size();
				page.putField("posts", temp);
				count=1;
				posts.clear();
				posts.add(post);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		long start=System.currentTimeMillis();
		HBaseProcess hBaseProcess=HBaseProcess.getInstance();
		try {
			hBaseProcess.connection();
			hBaseProcess.createTable();
			System.out.println("HBase连接成功！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String path="http://bbs.csdn.net/tech_hot_topics";
		Spider.create(new CSDNHistoricalSpider()).addUrl(path).addPipeline(new DataPipeline()).thread(5).run();
		hBaseProcess.insert(posts);
		batch++;
		number=number+posts.size();
		System.out.println("爬虫数据插入成功！");
		long rowCount=hBaseProcess.getCount();
		hBaseProcess.close();
		long end=System.currentTimeMillis();
		SpiderReport spiderReport=new SpiderReport();
		String executeState="";
		if(state) {
			executeState="爬取成功";
		}
		else {
			executeState="爬取失败";
		}
		String executeTime=1.0*(end-start)/1000+"s";
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd"); 
		String reportTime=format.format(date);
		String reportName=reportTime+"爬取结果报告";
		spiderReport.setReportName(reportName);
		spiderReport.setState(executeState);
		spiderReport.setExecuteTime(executeTime);
		spiderReport.setBatchCount(batch); 
		spiderReport.setPageNumber(pageNumber-1);
		spiderReport.setNumber(number);
		spiderReport.setCount(rowCount);
		ReportProduce.writeToFile(spiderReport);
	}
	
}
