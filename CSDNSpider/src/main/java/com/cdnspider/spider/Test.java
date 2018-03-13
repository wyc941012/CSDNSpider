package com.cdnspider.spider;

import java.io.IOException;

import com.csdnspider.entity.PostDTO;
import com.csdnspider.hbase.HBaseProcess;

public class Test {

	public static void main(String[] args) throws IOException {
		HBaseProcess hBaseProcess=HBaseProcess.getInstance();
		hBaseProcess.connection();
		long rowKey=392269002;
		PostDTO postDTO=hBaseProcess.getDataByRowKey(rowKey);
		System.out.println(postDTO.getPostContent());
	}
}
