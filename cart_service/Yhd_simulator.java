package LoginTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

class Yhd_productinfo{
	//productId=\"23287558\" cartItemName=
	public String productId;
	public String cartItemName;
	//cart2Checkbox\" cod=\"0\" isyihaodian=\"0\" href=\"javascript:void(0)\" value=\"27371021_0-2_0_0_1\
	public String cart2Checkbox_value;
	//cartItemVoId=\"27371021_0-2
	public String cartItemVoID;
	
	public Yhd_productinfo(){
		
	}
	
	public Yhd_productinfo(String params){
		String []contents=params.split(":");
		productId=contents[0];
		cartItemName=contents[1];
		cart2Checkbox_value=contents[2];
		cartItemVoID=contents[3];
	}
}

public class Yhd_simulator extends Config{
	private NetWork mnet;
	private HashMap<String,Yhd_productinfo> map;
	
	public Yhd_simulator(){
		mnet=new NetWork();
		map= new HashMap<String,Yhd_productinfo>();
	}
	public void closeAll(){
		mnet.closeAll();
	}
	
	public boolean Yhd_login(String uname,String pwd){
		System.out.println("yhd begin to login");
		try{
			//post url
			String url="https://passport.yhd.com/publicPassport/login.do";
			String sigs="";
			String sig="";
			String verifyCode="";
			
			//get sig
			HttpGet hg=new HttpGet("https://captcha.yhd.com/public/getsig.do");
			Response hp=mnet.execGet(hg);
			String content=hp.getContent();
			//System.out.println(content);
			sigs=Tool.getValByPattern(content,"\"sig\":.+\"");
			//System.out.println(sigs);
			sig=sigs.split(":")[1];
			sig=sig.split("\"")[1];
			System.out.println(sig);
			
			//post data
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	    	nvps.add(new BasicNameValuePair("credentials.username","ZteI5bhPrheWL/WkK4yB7jS2sWAvgTReC+wOB/2C9kVDgC3Bp1b9nL3prRqLhTC8ZlwfYdN3tDS6uiGbQ3PqyXV7jrerrLWFWpNBKUPblndJJmsIz4ymViRZ6+3Nd27MVQM8uZssBJmJEoHgzamKLd2Svcf0WC8NDGbqLU4SMao="));
	    	nvps.add(new BasicNameValuePair("credentials.password","NYT0KkDrrcwETizwTa+7EKUWcrpZw9nn62MiTQY2qGjIHz85WxFamqGrYPmSLijRObQF/HpDKLlRUOaxDuiFp0xjLoKTGVHXvOkX3+eJUWI4IKxqzpY3VFIhdzqdq0Pf+G7s+I9Vc5Lnd5v/Z/ud3qrJxjFARi+a0C0uLt0FAlE="));
	    	nvps.add(new BasicNameValuePair("sig",sig));
	    	nvps.add(new BasicNameValuePair("captchaToken",""));
	    	nvps.add(new BasicNameValuePair("loginSource","1"));
	    	nvps.add(new BasicNameValuePair("returnUrl","http://www.yhd.com/?tracker_u=12417&adgroupKeywordID=25164795"));
	    	nvps.add(new BasicNameValuePair("isAutoLogin","0"));
	    	HttpPost hp1=new HttpPost(url);
	    	hp1.setEntity(new UrlEncodedFormEntity(nvps));
	    	Response resp1=mnet.execPost(hp1) ;
	    	//String content1=resp1.getContent();
	    	//System.out.println(content1);
	    	if (resp1.getState()==0 && resp1.getResp().getStatusLine().getStatusCode()==200){
	    		String content1=resp1.getContent();
	    		//System.out.println(content1);
	    		//System.out.println("ok");
	    		if (content1.contains("\"errorCode\":0")){
	    			System.out.println("login sucess");
	    			return true;
	    		}
	    		//需要确认是否是needVerifyCode
	    		else if(content1.contains("\"errorCode\":30")){
	    			//System.out.println("here");
	    			String url_vfc = "https://captcha.yhd.com/public/getjpg.do?sig="+sig;
	    			//System.out.println(url_vfc);
	    			HttpGet hg1=new HttpGet(url_vfc);
	    			Response resp2=mnet.execGet(hg1);
	    			if(resp2.getState()==0&&resp2.getResp().getStatusLine().getStatusCode()==200){
	    				System.out.println("需要输入验证码");
	    				//System.out.println(src);
	                    VFrame vf1=new VFrame(mnet,"sn",url_vfc,"验证码");
	   				    verifyCode = vf1.getText();
	    				//verifyCode=Tool.getScan().next();
	    				NameValuePair bp1=new BasicNameValuePair("validCode",verifyCode);
	    				nvps.add(bp1);
	    				//nvps.add(bp2);
	    				hp1=new HttpPost(url);
	    				hp1.setEntity(new UrlEncodedFormEntity(nvps));
	    				Response resp3=mnet.execPost(hp1);
	    				if(resp3.getState()==0&&resp3.getResp().getStatusLine().getStatusCode()==200)
	    				{
	    					String content3=resp3.getContent();
	    					//System.out.println(content3);
	    					if(content3.contains("\"errorCode\":0"))
	    					{
	    						System.out.println("login success!");
	    						//System.out.println("here");
	    						return true;
	    					}
	    					else 
	    					{
	    						//如果验证码有问题
	    						while(content3.contains("\"errorCode\":10"))
	    						{
	    							//刷新一下浏览器，重新获得一个验证码
	    							System.out.println("验证码错误");
	    							//System.out.println("请重新刷新验证码!(刷新浏览器)");
	    							//verifyCode=Tool.getScan().next();
	    							VFrame vf2=new VFrame(mnet,"sn",url_vfc,"验证码错误");
	    			   				verifyCode = vf2.getText();
	    							nvps.remove(bp1);
	    							bp1=new BasicNameValuePair("validCode",verifyCode);
	    							nvps.add(bp1);
	    							hp1=new HttpPost(url);
	    		    				hp1.setEntity(new UrlEncodedFormEntity(nvps));
	    		    				resp3=mnet.execPost(hp1);
	    		    				if(resp3.getState()==0&&resp3.getResp().getStatusLine().getStatusCode()==200)
	    		    				{
	    		    					content3=resp3.getContent();
	    		    					if(content3.contains("\"errorCode\":0"))
	    		    					{
	    		    						System.out.println("login success!");
	    		    						return true;
	    		    					}
	    		    				}
	    		    				else{
	    		    					System.out.println("状态异常!");
	    		    					break;
	    		    				}
	    						}
	    					}
	    				}
	    			}
	    		}
	    		
	    	}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
		}
		System.out.println("login fail");
		return false;
	}
	
	public Result getCart(){
		Result result=new Result();
		System.out.println("yhd begin to get cart");
		String url="http://cart.yhd.com/cart/mod/newMainbody.do";
		
		/*String uname="*********";
		String pwd="*******";
		HttpGet login_hg=new HttpGet(url);
		Response login_resp=mnet.execGet(login_hg);
		String login_content=login_resp.getContent();
		if(login_content.contains("请立即登录查看"))
			Yhd_login(uname,pwd);*/
		
		
		
		result.vpn=getVpn();//1
		result.shop="yhd";//2
		result.extype="getcart";//3
		result.url=url;//4
		result.httptype="get";//5
		result.filetype="json";//6
		HttpGet hg=new HttpGet(url);
		try{
			result.ctime=new Date().getTime();//7
			Response resp=mnet.execGet(hg);
			//String content=resp.getContent();
			//System.out.println(content);
			//get html
			//Tool.getValue(content2, "Yhd");
			if (resp.getState() == 0&& resp.getResp().getStatusLine().getStatusCode() == 200)
			{
				String content = resp.getContent();
				String content1=content.split("\"data\":\"")[1];
				String content2=content1.split("\",\"msg")[0];
				//System.out.println(content2);
				//字符串处理
				String []products=content2.split("productId=\\\\\"");
				String []cartItemVoIDs=content2.split("cartItemVoId=\\\\\"");
				String []cart2Checkboxs=content2.split("cart2Checkbox\\\\\"");
				int length=products.length;
				int length1=cartItemVoIDs.length;
				int length2=cart2Checkboxs.length;
				//System.out.println(length);
				int size=length-1;//total item
				Yhd_productinfo[] a=new Yhd_productinfo[size];
				for(int i=0;i<size;i++)
				{
					a[i]=new Yhd_productinfo();
				}
				//System.out.println(products[1]);
				//System.out.println(products[2]);
				for(int i=1;i<length;i++)
				{ 
					String productId=products[i].split("\\\\\"")[0];
					String cartItemName=products[i].split("\\\\\"")[2];
					//System.out.println(productId);
					//System.out.println(cartItemName);
					a[i-1].productId=productId;
					a[i-1].cartItemName=cartItemName;
				}
				//System.out.println(length2-1);
				for(int i=1;i<((length2-1)/2+1);i++)
				{
					//System.out.println(cart2Checkboxs[i].split("value\\\\\"")[1]);
					String cart2Checkbox_value=cart2Checkboxs[i].split("value=\\\\\"")[1].split("\\\\\"")[0];
					//System.out.println(cart2Checkbox_value);
					a[i-1].cart2Checkbox_value=cart2Checkbox_value;
				}
				for(int i=1;i<length1;i++)
				{
					String cartItemVoID=cartItemVoIDs[i].split("\\\\\"")[0];
					//System.out.println(cartItemVoID);
					a[i-1].cartItemVoID=cartItemVoID;
				}
				//assert(1!=1);
				int kk = 0;
				map.clear();
				for(int i=0;i<size;i++)
				{
					map.put(a[i].productId, a[i]);
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
		}
		System.out.println("get cart fail");
		return result;
	}
	
	public Result addCart(String curl){
		System.out.println("yhd begin to add cart");
		Result result=new Result();
		String pid=null;
		HttpGet hg1=new HttpGet(curl);
		Response resp1=mnet.execGet(hg1);
		if (resp1.getState()==0 &&resp1.getResp().getStatusLine().getStatusCode()==200){
			String content1=resp1.getContent();
			//System.out.println(content1);
			pid=content1.split("productId")[1].split("value=\"")[1].split("\"")[0];
			//System.out.println(pid);	
		}
		
		String url = "http://cart.yhd.com/cart/opt/add.do?callback=jsonp1442211853446&productId="+pid+"&merchantId=1&num=1&pmId=&ybPmIds=&showPrice=39.9&needTip=&pageRef=&linkPosition=addToCart";		
		result.vpn=getVpn();//1
		result.extype="addcart";//2
		result.httptype="get";//3
		result.shop="yhd";//4
		result.url=url;//5
		result.filetype="html";//6
		HttpGet hg = new HttpGet(url);
		try {
			result.ctime=new Date().getTime();//7
			Response rep=mnet.execGet(hg);
			if (rep.getState()==0&&rep.getResp().getStatusLine().getStatusCode() == 200) {
				String content=rep.getContent();
				//System.out.println(content);
				if(content.contains("操作成功"))
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
	
	public Result delCart(String curl){
		System.out.println("yhd begin to del cart");
		String pid=null;
		HttpGet hg=new HttpGet(curl);
		Response resp=mnet.execGet(hg);
		if (resp.getState()==0 &&resp.getResp().getStatusLine().getStatusCode()==200){
			String content1=resp.getContent();
			//System.out.println(content1);
			pid=content1.split("productId")[1].split("value=\"")[1].split("\"")[0];
			//System.out.println(pid);	
		}
		Yhd_productinfo pi=map.get(pid);
		String url="http://cart.yhd.com/cart/opt/delete.do?callback=jsonp1441955522952&deleteId="+pi.cartItemVoID+"&deletePromo=&cart2Checkbox="+pi.cart2Checkbox_value+"%3D1&logged=1&view=1";
		HttpGet hg1=new HttpGet(url);
		Response resp1=mnet.execGet(hg1);
		if(resp1.getState()==0 && resp1.getResp().getStatusLine().getStatusCode()==200){
			String content1=resp1.getContent();
			//System.out.println(content1);
		}
		
		String del_url="http://cart.yhd.com/cart/mod/newRedeem.do?callback=jsonp1441955522952&refreshFlag=1";
		HttpPost hp=new HttpPost(del_url);
		Result result=new Result();
		//List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		//nvps.add(new BasicNameValuePair("productIds",productid));
		//nvps.add(new BasicNameValuePair("callback","jsonp1441955522952"));
		result.vpn=getVpn();//1
		result.extype="delcart";//2
		result.httptype="post";//3
		result.url=del_url;//4
		result.shop="yhd";//5
		result.filetype="html";//6
		try{
			//hp.setEntity(new UrlEncodedFormEntity(nvps));
			result.ctime=new Date().getTime();//7
			Response resp2=mnet.execPost(hp);
			//String content=resp.getContent();
			//System.out.println(content);
			if(resp2.getState()==0&&resp2.getResp().getStatusLine().getStatusCode()==200)
			{
				String content=resp2.getContent();
				//System.out.println(content);
			    if(content.contains("\"msg\":\"操作成功"))
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
		}
		System.out.println("del cart fail");
		return result;
	}
	
	
	
	public static void main(String []args){
		String uname="*****";
		String pwd="*****";
		Yhd_simulator yhd=new Yhd_simulator();
		yhd.setVpn(Result.vpn);
		//System.out.println(Result.vpn);
		String ip=Result.vpn.split("\\+")[1];
		//redord begin time
		Log.getDateTime("Begin_time:");
		Log.recordLogFile("\t\t"+Result.vpc+"\t"+new Ping().doPing(ip,3)+"\n");
		yhd.Yhd_login(uname,pwd);
		Result result=yhd.getCart();
		Log.recordLogFile("\t\t"+result.toString()+"\n");
		System.out.println(result);
		result=yhd.addCart("http://item.yhd.com/item/54624167");
		Log.recordLogFile("\t\t"+result.toString()+"\n");
		System.out.println(result);
		result=yhd.getCart();
		Log.recordLogFile("\t\t"+result.toString()+"\n");
		System.out.println(result);
		result=yhd.delCart("http://item.yhd.com/item/54624167");
		Log.recordLogFile("\t\t"+result.toString()+"\n");
		//record end time
		Log.getDateTime("End_time:");
		System.out.println(result);
		yhd.closeAll();	
	}
}
