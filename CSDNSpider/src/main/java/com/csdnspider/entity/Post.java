package com.csdnspider.entity;

import java.util.List;

//帖子类
public class Post {

	//帖子ID
	private long postId;
	//帖子标题
	private String postTitle;
	//帖子链接
	private String postUrl;
	//帖子类别
	private String postType;
	//帖子提问内容
	private String postContent;
	//帖子提问时间
	private String postTime;
	//帖子回复次数
	private long responseCount;
	//帖子回复内容
	private List<String> response;
	
	public long getPostId() {
		return postId;
	}
	public void setPostId(long postId) {
		this.postId = postId;
	}
	public String getPostTitle() {
		return postTitle;
	}
	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}
	public String getPostUrl() {
		return postUrl;
	}
	public void setPostUrl(String postUrl) {
		this.postUrl = postUrl;
	}
	public String getPostType() {
		return postType;
	}
	public void setPostType(String postType) {
		this.postType = postType;
	}
	public String getPostContent() {
		return postContent;
	}
	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}
	public String getPostTime() {
		return postTime;
	}
	public void setPostTime(String postTime) {
		this.postTime = postTime;
	}
	public long getResponseCount() {
		return responseCount;
	}
	public void setResponseCount(long responseCount) {
		this.responseCount = responseCount;
	}
	public List<String> getResponse() {
		return response;
	}
	public void setResponse(List<String> response) {
		this.response = response;
	}
	
	@Override
	public String toString() {
		return "Post [postId=" + postId + ", postTitle=" + postTitle + ", postUrl=" + postUrl + ", postType=" + postType
				+ ", postContent=" + postContent + ", postTime=" + postTime + ", responseCount=" + responseCount
				+ ", response=" + response + "]";
	}
	
	
}
