package com.cdnspider.spider;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.csdnspider.entity.SpiderReport;

//爬取结果报告生成
public class ReportProduce {

	//爬取报告路径
	private static final String path="C:/Users/Administrator/Desktop/";
		
	public static void writeToFile(SpiderReport spiderReport) {
		String reportName=spiderReport.getReportName();
		String state=spiderReport.getState();
		String executeTime=spiderReport.getExecuteTime();
		long batchCount=spiderReport.getBatchCount();
		long pageNumber=spiderReport.getPageNumber();
		long numbers=spiderReport.getNumber();
		long count=spiderReport.getCount();
		String filePath=path+reportName+".txt";
		try {
			BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, false),"UTF-8"));
			bufferedWriter.write("爬取状态："+state);
			bufferedWriter.newLine();
			bufferedWriter.write("执行总时间："+executeTime);
			bufferedWriter.newLine();
			bufferedWriter.write("爬取总批次数："+batchCount);
			bufferedWriter.newLine();
			bufferedWriter.write("爬取总页数："+pageNumber);
			bufferedWriter.newLine();
			bufferedWriter.write("爬取总记录数："+numbers);
			bufferedWriter.newLine();
			bufferedWriter.write("HBase总行数："+count);
			bufferedWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
