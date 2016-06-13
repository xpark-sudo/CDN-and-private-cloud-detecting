package LoginTest;

import org.apache.http.client.methods.CloseableHttpResponse;

//用一个类专门管理返回的数据
public class Response {
   
	//表明执行的状态
	/*
	 * equal 0 表示请求正常，但是请求返回的状态还需要check了
	 * equal 1 表示请求异常，请求的状态什么的就不用check了
	 */
	private int state;
	//请求返回的内容
	private String content;
	private CloseableHttpResponse resp;
	//请求花费的时间
	private long duration;
	//返回的文件大小
	private long filesize;
	
	public Response()
	{
		
	}
	
	public Response(CloseableHttpResponse resp,String content,int state)
	{
		this.resp=resp;
		this.content=content;
		this.state=state;
	}
	
	public void setResp(CloseableHttpResponse resp)
	{
		this.resp=resp;
	}
	
	public CloseableHttpResponse getResp()
	{
		return resp;
	}
	
	public void setContent(String content)
	{
		this.content=content;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public void setState(int scode)
	{
		this.state=scode;
	}
	
	public int getState()
	{
		return this.state;
	}
	
	public void setDuration(long l)
	{
		this.duration=l;
	}
	
	public long getDuration()
	{
		return duration;
	}
	
	public void setFileSize(long size)
	{
		this.filesize=size;
	}
	
	public long getFileSize()
	{
		return filesize;
	}
}
