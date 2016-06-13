package LoginTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * java官方的ping可以试着学习它的代码
 * http://docs.oracle.com/javase/1.5.0/docs/guide/nio/example/Ping.java
 * @author xiaoguodong
 *
 */
public class Ping {

	public Ping()
	{
		
	}
	
	//返回ping的结果单位是ms
	//如果出错返回-1
	public int  doPing(String ip,int times)
	{
		Process p=null;
		try{
		String s = null;
		String result="";
	    p = Runtime.getRuntime().exec("ping "+ip+" -n "+times);
	    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	    // read the output from the command
	   // System.out.println("Here is the standard output of the command:\n");
	    while ((s = stdInput.readLine()) != null)
	    {
	       result+=s;
	    }
	    //针对s做相应的分析
	    Pattern pa=Pattern.compile("(?<=平均 = ).*(?=ms)");
		Matcher m=pa.matcher(new String(result.getBytes("gbk"),"gbk"));
		if(m.find())
			result=result.substring(m.start(),m.end());
		
	    // read any errors from the attempted command
	    //System.out.println("Here is the standard error of the command (if any):\n");
	     while ((s = stdError.readLine()) != null)
	      {
	         System.out.println(s);
	      }
	     return Integer.parseInt(result);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(p!=null)
				p.destroy();
		}
		return -1;
	}
	
	public static void main(String[] args)
	{
		Ping ping=new Ping();
		int result=ping.doPing("211.151.155.11", 5);
		System.out.println(result);
	}
}
