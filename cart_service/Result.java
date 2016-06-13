package LoginTest;
//专门用来作为返回的结果
public class Result {
   //执行操作的状态
   //-1表示没有成功
   public int status=-1;
   //执行操作时的vpn
   public static String vpn;
   //record vpc
   public static String vpc;
   //执行操作的店铺
   public String shop;
   //执行操作的当前时间(网络的标准时间)
   public long ctime;
   //执行的操作类型
   public String extype;
   //http的类型
   public String httptype;
   //操作的持续时间
   public long duration;
   //ping的时间
   public int ptime;
   //返回的类型
   public String filetype;
   //文件大小(单位初步为byte)
   public long filesize;
   //ip地址
   public String ip;
   //执行操作的url地址
   public String url;
   
   public Result()
   {
	   
   }
   
   public String toString()
   {
	   return status+"\t"+
			  vpn+"\t"+
			  shop+"\t"+
			  ctime+"\t"+
			  extype+"\t"+
			  httptype+"\t"+
			  duration+"\t"+
			  ptime+"\t"+
			  filetype+"\t"+
			  filesize+"\t"+
			  ip+"\t"+
	          url;
   }
}
