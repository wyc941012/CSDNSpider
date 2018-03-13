package com.cdnspider.spider;

import java.util.List;

import com.csdnspider.entity.Post;
import com.csdnspider.hbase.HBaseProcess;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

//将爬取到的数据以页面为单位写入到HBase中
public class DataPipeline implements Pipeline {

	@Override
	public void process(ResultItems resultItems, Task task) {
		// TODO Auto-generated method stub
		List<Post> posts=resultItems.get("posts");
		HBaseProcess hBaseProcess=HBaseProcess.getInstance();
		hBaseProcess.insert(posts);
		System.out.println("批次执行成功！");
	}

}
