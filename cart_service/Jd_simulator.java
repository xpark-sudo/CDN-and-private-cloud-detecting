package LoginTest;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.HasSiblingFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

//保存商品的一些必要信息方便删除的时候使用
class ProductInfo {
	public String venderId;
	public String pid;
	public String ptype;
	public String packId;
	public String targetId;
	// 默认生成
	public String outSkus;
	public String random;
	public String locationId;

	public ProductInfo() {

	}

	// 根据<a>的属性计算获得
	public ProductInfo(String params) {
		String[] ss = params.split("_");
		venderId = ss[1];
		pid = ss[2];
		ptype = ss[3];
		packId = "0";
		targetId = "0";
		if (ss.length == 5 || ss.length == 6) {
			targetId = ss[4];
			if (ss.length == 6) {
				packId = ss[5];
			}
		}
		// 生成的默认
		outSkus = "";
		random = String.valueOf(Math.random());
		locationId = "1-0-0";
	}
}

// 考量一下验证码的问题
public class Jd_simulator extends Config{

	// 初始化连接资源
	private NetWork mnet;
	// 通过pid进行查询
	private HashMap<String, ProductInfo> map;

	public Jd_simulator() {
		mnet = new NetWork();
		map = new HashMap<String, ProductInfo>();
	}

	public void closeAll() {
		mnet.closeAll();
	}
	
	class Temp{
		List<NameValuePair> nvps;
		String authurl;
		int state=1;
	}
	
	//这个方法可以暂时不使用
	public boolean isShowAuthCode(String uname)
	{
		try{
		  String url="https://passport.jd.com/uc/showAuthCode?r="+Math.random()+"&version=2015";
		  HttpPost hp=new HttpPost(url);
		  List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		  nvps.add(new BasicNameValuePair("loginName",uname));
		  hp.setEntity(new UrlEncodedFormEntity(nvps));
		  Response resp=mnet.execPost(hp);
		  if(resp.getState()==0&&resp.getResp().getStatusLine().getStatusCode()==200)
		  {
			  String content=resp.getContent();
			  System.out.println(content);
			  if(content.contains("true"))
			  {
				  return true;
			  }
		  }
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}
		finally{
			
		}
		return false;
	}
	
	private HttpPost getJdPost(String url)
	{
		HttpPost hp=new HttpPost(url);
		hp.setHeader("Accept","text/plain, */*; q=0.01");
		hp.setHeader("Accept-Encoding","gzip, deflate");
		hp.setHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
		hp.setHeader("Connection","keep-alive");
		hp.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		hp.setHeader("Host","passport.jd.com");
		hp.setHeader("Origin","https://passport.jd.com");
		hp.setHeader("Referer","https://passport.jd.com/uc/login?ltype=logout");
		hp.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36");
		hp.setHeader("X-Requested-With","XMLHttpRequest");
		return hp;
	}
	
	private long getTime()
	{
		return new Date().getTime();
	}

	
    //对于请求的，尽量只是请求一次
	public boolean Jd_login(String uname, String passwd) 
	{
		try{
		 System.out.println("jd begin to login");
		 //验证码的url
		 String authurl="";
		 String uuid="";
		 List<NameValuePair> nvps=new ArrayList<NameValuePair>();
		 //首先是获得一些基本的信息，这些可能会多次利用
		 String loginurl="https://passport.jd.com/uc/login";
		 HttpGet hg=new HttpGet(loginurl);
		 Response resp1=mnet.execGet(hg);
		 if(resp1.getState()==0&&resp1.getResp().getStatusLine().getStatusCode()==200)
		 {
			 String content1=resp1.getContent();
			 Parser parser = new Parser(content1);
			 NodeFilter trFilter = new TagNameFilter("input");
			 NodeList formNodes = parser.extractAllNodesThatMatch(trFilter);
			 for (int j = 0; j < formNodes.size(); j++) 
			 {
				InputTag node = (InputTag) formNodes.elementAt(j);
				String name = node.getAttribute("name");
				String value = node.getAttribute("value");
				if(name.equals("uuid"))
					uuid=value;
				if(name.equals("loginname"))
					value = uname;
				if(name.equals("nloginpwd") || name.equals("loginpwd"))
					value = passwd;
				if(name.equals("chkOpenCtrl"))
				{
					continue;
				}
				if(value == null)
					value = "";
				if(name.equals("chkRememberMe"))
					value = "on";
				if (name.equals("authcode")) 
				{
				     //long time=new Date().getTime();
					 authurl = "https://authcode.jd.com/verify/image?a=1&acid="
									+ uuid
									+ "&uid="
									+ uuid
									+ "&yys=";
									//+ time;
					 
					continue;
				}
				//System.out.println(name+":"+value);
			    nvps.add(new BasicNameValuePair(name, value));
			}
			 //获得必要的数据后开始post
			 //post url
			 String posturl="https://passport.jd.com/uc/loginService"
			 +"?uuid="+uuid
			 +"&ltype=logout&r="
			 +Math.random()
			 +"&version=2015";
			 List<NameValuePair> lp1=new ArrayList<NameValuePair>();
			 lp1.addAll(nvps);
			 HttpPost hp=new HttpPost(posturl);
			 hp.setEntity(new UrlEncodedFormEntity(nvps));
			 resp1=mnet.execPost(hp);
			 if(resp1.getState()==0&&resp1.getResp().getStatusLine().getStatusCode()==200)
			 {
				 content1=resp1.getContent();
				 if(content1.contains("success"))
				 {
					 //System.out.println(content1);
					 System.out.println("login success!");
					 return true;
				 }
				 else if(content1.contains("empty"))
				 {
					 System.out.println("需要输入验证码");
					 VFrame fr=new VFrame(mnet,"jd",authurl+getTime(),"验证码");
					 String authcode=fr.getText();
					 System.out.println(authcode);
					 lp1=new ArrayList<NameValuePair>();
					 lp1.addAll(nvps);
					 lp1.add(new BasicNameValuePair("authcode",authcode));
					 hp=getJdPost(posturl);
					 hp.setEntity(new UrlEncodedFormEntity(lp1));
					 resp1=mnet.execPost(hp);
					 if(resp1.getState()==0&&resp1.getResp().getStatusLine().getStatusCode()==200)
					 {
						 content1=resp1.getContent();
						 //System.out.println(content1);
						 if(content1.contains("success"))
						 {
							 //System.out.println("========================");
							 //System.out.println(content1);
							 System.out.println("login success!");
							 return true;
						 }//验证码错误的循环中
						 else{
							 while(content1.contains("empty"))
							 {
								 System.out.println("需要重新输入验证码");
								 fr=new VFrame(mnet,"jd",authurl+getTime(),"验证码错误");
								 authcode=fr.getText();
								 lp1=new ArrayList<NameValuePair>();
								 lp1.addAll(nvps);
								 lp1.add(new BasicNameValuePair("authcode",authcode));
								 hp=getJdPost(posturl);
								 hp.setEntity(new UrlEncodedFormEntity(lp1));
								 resp1=mnet.execPost(hp);
								 if(resp1.getState()==0&&resp1.getResp().getStatusLine().getStatusCode()==200)
								 {
									 content1=resp1.getContent();
									// System.out.println(content1);
									 if(content1.contains("success"))
									 {
										 //System.out.println("========================");
										// System.out.println(content1);
										 System.out.println("login success!");
										 return true;
									
									 }
								 }
								 else{
									 break;
								 }
							 }
						 }
					 }
				 }
			 }
		 }
		 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("login fail!");
		return false;
	}
	
	
	//返回特定的result,来说明相应的情况
	public Result getCart() {
		System.out.println("jd begin to get cart");
		Result result=new Result();
		result.vpn=getVpn();//1
		result.shop="jd";//2
		result.extype="getcart";//3
		String cart_url = "http://cart.jd.com/cart";
		result.url=cart_url;//4
		result.httptype="get";//5
		result.filetype="html";//6
		HttpGet httpget = new HttpGet(cart_url);
		try {
			result.ctime=new Date().getTime();//7
			Response resp = mnet.execGet(httpget);
			if (resp.getState() == 0&& resp.getResp().getStatusLine().getStatusCode() == 200)
			{
				String content = resp.getContent();
				Parser parser = new Parser(content);
				// 首先是有<div class="cart-item-list"...这样一个parent
				NodeFilter ft1 = new TagNameFilter("div");
				NodeFilter ft2 = new HasAttributeFilter("class",
						"cart-item-list");
				NodeFilter ft3 = new AndFilter(ft1, ft2);
				NodeList formNodes = parser.extractAllNodesThatMatch(ft3);
				// System.out.println("一共有"+formNodes.size()+"个cart-item");
				// 具体的商品的数目
				int kk = 0;
				map.clear();
				for (int i = 0; i < formNodes.size(); i++) {
					// 再利用cart-item-list进行具体的分析
					Node node = formNodes.elementAt(i);
					// 还要在做一个层次的解析哦
					Parser ps1 = new Parser(node.toHtml());
					NodeFilter ft4 = new TagNameFilter("div");
					NodeFilter ft5 = new HasAttributeFilter("class","item-form");
					NodeFilter ft6 = new AndFilter(ft4, ft5);
					NodeList nl1 = ps1.extractAllNodesThatMatch(ft6);
					for (int j = 0; j < nl1.size(); j++) {
						Node nd1 = nl1.elementAt(j);
						Parser ps2 = new Parser(nd1.toHtml());
						NodeFilter ft7 = new TagNameFilter("div");
						NodeFilter ft8 = new HasAttributeFilter("class","cell p-ops");
						NodeFilter ft9 = new AndFilter(ft7, ft8);
						NodeFilter ft10 = new HasParentFilter(ft9);
						NodeFilter ft11 = new TagNameFilter("a");
						NodeFilter ft12 = new AndFilter(ft10, ft11);
						NodeList nl2 = ps2.extractAllNodesThatMatch(ft12);
						for (int k = 0; k < nl2.size(); k++) {
							LinkTag nd3 = (LinkTag) nl2.elementAt(k);
							String params = nd3.getAttribute("id");
							ProductInfo pi = new ProductInfo(params);
							map.put(pi.pid, pi);
						}
					}
				}
				System.out.println("there are " + map.size() + " products!");
				//知道购物车成功的条件就是里面确实有商品的数目
				if(map.size()>0)
				{
					System.out.println("get cart success");
					result.status=0;//8
					result.filesize=resp.getFileSize();//9
					result.duration=resp.getDuration();//10
					result.ip=mnet.getIp();//11
					result.ptime=mnet.getPing(result.ip);//12
					return result;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		System.out.println("get cart fail");
		return result;
	}

	// 购物车添加
	// 返回result说明情况
	public Result addCart(String curl) {
		System.out.println("jd begin to add cart");
		Result result=new Result();
		String pid=Tool.getValByPattern(curl, "\\d*(?=\\.html)");
		String url = "http://cart.jd.com/cart/dynamic/gate.action?pid=" + pid
				+ "&pcount=1&ptype=1";
		result.vpn=getVpn();//1
		result.extype="addcart";//2
		result.httptype="get";//3
		result.shop="jd";//4
		result.url=url;//5
		result.filetype="html";//6
		HttpGet hg = new HttpGet(url);
		try {
			result.ctime=new Date().getTime();//7
			Response rep=mnet.execGet(hg);
			if (rep.getState()==0&&rep.getResp().getStatusLine().getStatusCode() == 200) {
				String content=rep.getContent();
				if(content.contains("成功加入购物车"))
				{
					System.out.println("add success");
					result.status=0;//8
					result.filesize=rep.getFileSize();//9
					result.duration=rep.getDuration();//10
					result.ip=mnet.getIp();//11
					result.ptime=mnet.getPing(result.ip);//12
					return result;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		System.out.println("add fail");
		return result;
	}
	
	//删除购物车
	//返回result来说明情况
	public Result delCart(String curl)
	{
		System.out.println("jd begin to del cart");
		Result result=new Result();
		result.vpn=getVpn();//1
		result.extype="delcart";//2
		result.httptype="post";//3
		String pid=Tool.getValByPattern(curl, "\\d*(?=\\.html)");
		String delurl="http://cart.jd.com/removeSkuFromCart.action?rd=0.8895351418759674";
		result.url=delurl;//4
		result.shop="jd";//5
		result.filetype="json";//6
		HttpPost hp=new HttpPost(delurl);
		ProductInfo pi=map.get(pid);
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("verderId",pi.venderId));
		nvps.add(new BasicNameValuePair("pid",pi.pid));
		nvps.add(new BasicNameValuePair("ptype",pi.ptype));
		nvps.add(new BasicNameValuePair("packId",pi.packId));
		nvps.add(new BasicNameValuePair("targetId",pi.targetId));
		nvps.add(new BasicNameValuePair("outSkus",pi.outSkus));
		nvps.add(new BasicNameValuePair("random",pi.random));
		nvps.add(new BasicNameValuePair("locationId",pi.locationId));
		try{
		hp.setEntity(new UrlEncodedFormEntity(nvps));
		result.ctime=new Date().getTime();//7
		Response resp=mnet.execPost(hp);
		if(resp.getState()==0&&resp.getResp().getStatusLine().getStatusCode()==200)
		{
			String content2=resp.getContent();
		    if(content2.contains("\"success\":true"))
		    {
		    	map.remove(pid);
		    	System.out.println("del cart success");
		    	//接着就是成功的胜利消息
		    	result.status=0;//8
		    	result.filesize=resp.getFileSize();//9
		    	result.duration=resp.getDuration();//10
		    	result.ip=mnet.getIp();//11
		    	result.ptime=mnet.getPing(result.ip);//1
		    	return result;
		    }
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
		}
		System.out.println("del cart fail");
		return result;
	}
	
	public void loadCookie()
	{
		mnet.readCookie("jd");
	}
	
	public void saveCookie()
	{
		mnet.storeCookie("jd");
	}
	

	public static void main(String[] args) throws Exception
	{
		Jd_simulator js = new Jd_simulator();
		js.setVpn(Result.vpn);
		String uname = "keepcode";
		String passwd = "123456..abc";
		String ip=Result.vpn.split("\\+")[1];
		//record begin time
		Log.getDateTime("Begin_time:");
		Log.recordLogFile("\t\t"+Result.vpc+"\t"+new Ping().doPing(ip, 3)+"\n");
		js.Jd_login(uname, passwd);
		Result result=js.getCart();
		Log.recordLogFile("\t\t"+result.toString()+"\n");
		System.out.println(result);
		result=js.addCart("http://item.jd.com/1041947008.html");
		Log.recordLogFile("\t\t"+result.toString()+"\n");
		System.out.println(result);
		result=js.getCart();
		Log.recordLogFile("\t\t"+result.toString()+"\n");
		System.out.println(result);
		result=js.delCart("http://item.jd.com/1041947008.html");
		Log.recordLogFile("\t\t"+result.toString()+"\n");
		//record end time
		Log.getDateTime("End_time:");
		System.out.println(result);
		js.closeAll();
	}
}
