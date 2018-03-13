package com.csdnspider.entity;

//爬取结果报告
public class SpiderReport {

	//报告名称
	private String reportName;
	//执行状态
	private String state;
	//执行总时间
	private String executeTime;
	//爬取总批次数
	private long batchCount;
	//爬取总页数
	private long pageNumber;
	//爬取总记录数
	private long number;
	//HBase总行数
	private long count;
	
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(String executeTime) {
		this.executeTime = executeTime;
	}
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public long getNumber() {
		return number;
	}
	public void setNumber(long number) {
		this.number = number;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public long getBatchCount() {
		return batchCount;
	}
	public void setBatchCount(long batchCount) {
		this.batchCount = batchCount;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
}
