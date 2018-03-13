package com.csdnspider.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.csdnspider.entity.Post;
import com.csdnspider.entity.PostDTO;

public class HBaseProcess {

	private static HBaseProcess hBaseProcess=new HBaseProcess();
	
	private Connection connection;
	private static final String ZOOKEEPER_QUORUM="10.196.83.90,10.196.83.91,10.196.83.92";
	private static final String ZOOKEEPER_CLIENTPORT="2181";
	private static final String HBASE_ROOTDIR="hdfs://10.196.83.90:9000/hbase";
	private static final String RETRIES_NUMBER="3";
	private static final String TABLE_NAME="post";
	private static final String FAMILY_NAME="info";
	private static final String TITLE_COL="title";
	private static final String URL_COL="url";
	private static final String TYPE_COL="type";
	private static final String TIME_COL="time";
	private static final String RESPONSECOUNT_COL="responseCount";
	private static final String CONTENT_COL="content";
	private static final String regex="。";
	
	public static HBaseProcess getInstance() {
		return hBaseProcess;
	}
	
	//连接HBase
	public void connection() throws IOException {
		Configuration conf=HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", ZOOKEEPER_QUORUM);
	    conf.set("hbase.zookeeper.property.clientPort", ZOOKEEPER_CLIENTPORT);//端口号
	    conf.set("hbase.rootdir", HBASE_ROOTDIR);
	    conf.set("hbase.client.retries.number", RETRIES_NUMBER);	    
	    Connection connection=ConnectionFactory.createConnection(conf);
	    this.connection=connection;
	    //this.table=(HTable) connection.getTable(TableName.valueOf(TABLE_NAME));
	}
	
	//建表
	public void createTable() throws IOException {
		HBaseAdmin admin=null;
		try {
			admin=(HBaseAdmin) connection.getAdmin();
			if(admin.tableExists(TABLE_NAME)) {
				System.out.println("表已存在！");
				return;
			}
			HTableDescriptor descriptor=new HTableDescriptor(TableName.valueOf(TABLE_NAME));
			HColumnDescriptor columnDescriptor=new HColumnDescriptor(Bytes.toBytes(FAMILY_NAME));
			descriptor.addFamily(columnDescriptor);
			admin.createTable(descriptor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			admin.close();
		}
	}
	
	//插入数据
	@SuppressWarnings("deprecation")
	public void insert(List<Post> posts) {
		HTable table=null;
		try {
			table=(HTable) connection.getTable(TableName.valueOf(TABLE_NAME));
			List<Put> puts=new ArrayList<Put>();
			for(Post post : posts) {
				long rowKey=post.getPostId();
				StringBuilder stringBuilder=new StringBuilder();
				List<String> responses=post.getResponse();
				stringBuilder.append(post.getPostTitle()+regex+post.getPostContent()+regex);
				if(responses!=null&&responses.size()!=0) {
					int n=responses.size();
					for(int i=0;i<n;i++) {
						if(i!=n-1) {
							stringBuilder.append(responses.get(i)+regex);
						}
						else {
							stringBuilder.append(responses.get(i));
						}
					}
				}
				Put put=new Put(Bytes.toBytes(String.valueOf(rowKey)));
				put.addColumn(Bytes.toBytes(FAMILY_NAME), Bytes.toBytes(TITLE_COL), Bytes.toBytes(post.getPostTitle()));
				put.addColumn(Bytes.toBytes(FAMILY_NAME), Bytes.toBytes(URL_COL), Bytes.toBytes(post.getPostUrl()));
				put.addColumn(Bytes.toBytes(FAMILY_NAME), Bytes.toBytes(TYPE_COL), Bytes.toBytes(post.getPostType()));
				put.addColumn(Bytes.toBytes(FAMILY_NAME), Bytes.toBytes(TIME_COL), Bytes.toBytes(post.getPostTime()));
				put.addColumn(Bytes.toBytes(FAMILY_NAME), Bytes.toBytes(RESPONSECOUNT_COL), Bytes.toBytes(String.valueOf(post.getResponseCount())));
				put.addColumn(Bytes.toBytes(FAMILY_NAME), Bytes.toBytes(CONTENT_COL), Bytes.toBytes(stringBuilder.toString()));
				puts.add(put);
			}
			table.put(puts);
			puts=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				table.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	//获得表的总行数
	public long getCount() {
		HTable table=null;
		long rowCount=0;
		try {
			table=(HTable) connection.getTable(TableName.valueOf(TABLE_NAME));
			Scan scan=new Scan();
			scan.setFilter(new FirstKeyOnlyFilter());
			ResultScanner resultScanner=table.getScanner(scan);
			for(Result result : resultScanner) {
				rowCount+=result.size();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rowCount;
	}
	
	//根据rowKey获取数据
	public PostDTO getDataByRowKey(long rowKey) {
		HTable table=null;
		PostDTO post=new PostDTO();
		try {
			table=(HTable) connection.getTable(TableName.valueOf(TABLE_NAME));
			Get get=new Get(Bytes.toBytes(String.valueOf(rowKey)));
			get.addFamily(Bytes.toBytes(FAMILY_NAME));
			Result result=table.get(get);
			post.setPostId(rowKey);
			post.setPostTitle(Bytes.toString(result.getValue(Bytes.toBytes(FAMILY_NAME),Bytes.toBytes(TITLE_COL))));
			post.setPostUrl(Bytes.toString(result.getValue(Bytes.toBytes(FAMILY_NAME),Bytes.toBytes(URL_COL))));
			post.setPostType(Bytes.toString(result.getValue(Bytes.toBytes(FAMILY_NAME),Bytes.toBytes(TYPE_COL))));
			post.setPostContent(Bytes.toString(result.getValue(Bytes.toBytes(FAMILY_NAME),Bytes.toBytes(CONTENT_COL))));
			post.setPostTime(Bytes.toString(result.getValue(Bytes.toBytes(FAMILY_NAME),Bytes.toBytes(TIME_COL))));
			post.setResponseCount(Long.valueOf(Bytes.toString(result.getValue(Bytes.toBytes(FAMILY_NAME),Bytes.toBytes(RESPONSECOUNT_COL)))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return post;
	}
	
	//关闭HBase连接
	public void close() throws IOException {
		//table.close();
		connection.close();
	}
}
