package LoginTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Entity;
//tmall算是初步完成
//针对tm的商品需哟啊保存一些必要的消息
class Tm_product {
	public String shopId;
	public String comboId;
	public String shopActId; 
	public int quantity;
	public String cartId;
	public String skuId;
	public String itemId;
	public String operate;
	public String in_type;

	public Tm_product() {
		shopId = "";
		comboId = "0";
		shopActId = "0";
		//默认商品数目是1
		quantity = 1;
		cartId = "";
		skuId = "";
		itemId = "";
		operate = "";
		in_type = "delete";
	}

	public String toString() {
		return "shopId:" + shopId + "\n" + "comboId:" + comboId + "\n"
				+ "shopActId:" + shopActId + "\n" + "quantity:" + quantity
				+ "\n" + "cartId:" + cartId + "\n" + "skuId:" + skuId + "\n"
				+ "itemId:" + itemId + "\n" + "operate:" + operate + "\n"
				+ "in_type:" + in_type;
	}
}

public class Tm_simulator extends Config{
    //封装进行网络的请求
	private NetWork mnet;
	//_tb_token_登陆时候就应该获得
	private String _tb_token_ ;
	//通过getCart获得商品的信息
	private Map<String, Tm_product> map;

	public Tm_simulator() {
        mnet=new NetWork();
        _tb_token_="";
		map = new HashMap<String, Tm_product>();
	}

	public void closeAll() {
		mnet.closeAll();
	}
	
	private HttpPost getLoginPost()
	{
		String url="https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Fwww.tmall.com%2F";
	    HttpPost hp=new HttpPost(url);
	    hp.setHeader("origin", "https://login.taobao.com");
		hp.setHeader("referer","https://login.taobao.com/member/login.jhtml?redirectURL=https%3A%2F%2Fwww.tmall.com%2F");
		hp.setHeader("upgrade-insecure-requests", "1");
		hp.setHeader("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36");
	    return hp;
	}
	
	private List<NameValuePair> getLoginParam(String authcode)
	{
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("ua","102UW5TcyMNYQwiAiwZTXFIdUh1SHJOe0BuOG4=|Um5OcktyTnVIfUN7RnxIfSs=|U2xMHDJ7G2AHYg8hAS8WLgAgDlIzVTleIFp0InQ=|VGhXd1llXGVZYl9qVGxRa19qXWBCeUV6QnhFeUdzRnpFe0Z+RWs9|VWldfS0TMw8wCzAQLBc3GWxRbVBsVnNaYVt+SDJMLVB+KH4=|VmNDbUMV|V2NDbUMV|WGRYeCgGZhtmH2VScVI2UT5fORtmD2gCawwuRSJHZAFsCWMOdVYyVTpbPR99HWAFYVMuVzNnHHEdfAdqAXwoVC9LextmG2IYMQ42CENqVW1SHjcIMA9DPUM9H2IHbgpuTCdAJQwzCzR4UihVJUEsHHkYfh92EnZcMVcrUmIfZBh1XzFNKU0ZYg9jAnkUfwIyTzRIJQ9hGn0YKFUwWT1Zcxp+GmNTOkA6VypQBG8Vb0Z5QX8xDiwVNwgrTyhHJkAUbQYkTzVPZllhXmUIbhJrP1QuVH1CekQKNRcuDDMQfRtnHkozWHoRaxE4Bz8AO1IoUj9COGwOdF1iWmQqFTcOLBMwVDNcPVsPdhtgDmkSdx54E35cPkRuF3IIOAQ9BDgDPgs1Cj8RMR8xZzE=|WWdHFyoKNggzCCgWKRQ0Cj8BIR0kHSAANAk0FCgRKBU1ATwBVwE=|WmZYeCgGWjtdMVYoUn5EakpkOHs/Ey8aOgcnGjoFOwQqfCo=|W2FBET9gO30pUDlDOUcgWzdjX3FRbU15RhBG|XGBdfVMDOwE9Bj4eSWdbZVFpXWhQaldoU2tRJBk7DjUBPAQ7BzwCOgI3Cj8CN2BOblIEKnw=|XWVFFTsVNWVeYkJ+RnstDTAQPhAwCjEOMWcx|XmREFDplPngsVTxGPEIlXjJmWnRUaUlzSHZNG00=|X2VFFTt7L2YBbQBDJVwgXQloRmZaekB6RXwqfA==|QHtbCyVlMXgfcx5dO0I+Qxd2WHhDeFhlRXBKdEgeSA==|QXpaCiRkMHkech9cOkM/QhZ3WXlDfV1gQHROdEwaTA==|QnlZCSdnM3odcRxfOUA8QRV0WnpPelpnR3tGe0N5L3k=|Q3hYCCZmMnsccB1eOEE9QBR1W3tOclJvT3NOd0t3IXc=|RHxcDCJiNm4SeB18AVkkTTBROhQ0ZFhhWHhHekUTMw4uAC4OMg0zCDFnMQ==|RX9fDyFhNXwbdxpZP0Y6RxNyXHxBYV1iXGlQBlA=|RnxcDCJiNm4SeB18AVkkTTBROhQ0CCgUKxIvEUcR|R35Dfl5jQ3xcYFllRXtDeVlhVXVPd1drV25OclJuVWhIdkJiV2NDf0FhX2REekJiXWFBfkNjX2tLd0JiWXlGclJtWA4="));
	    nvps.add(new BasicNameValuePair("TPL_username", "1224694533@qq.com"));
		nvps.add(new BasicNameValuePair("TPL_password", ""));
		nvps.add(new BasicNameValuePair("TPL_checkcode", authcode));
		nvps.add(new BasicNameValuePair("loginsite", "0"));
		nvps.add(new BasicNameValuePair("newlogin", "0"));
		nvps.add(new BasicNameValuePair("TPL_redirect_url","https://www.tmall.com"));
		nvps.add(new BasicNameValuePair("from", "tmall"));
		nvps.add(new BasicNameValuePair("fc", "default"));
		nvps.add(new BasicNameValuePair("style", "miniall"));
		nvps.add(new BasicNameValuePair("css_style", ""));
		nvps.add(new BasicNameValuePair("tid", ""));
		nvps.add(new BasicNameValuePair("support", "000001"));
		nvps.add(new BasicNameValuePair("CtrlVersion", "1,0,0,7"));
		nvps.add(new BasicNameValuePair("loginType", "3"));
		nvps.add(new BasicNameValuePair("minititle", ""));
		nvps.add(new BasicNameValuePair("minipara", ""));
		nvps.add(new BasicNameValuePair("umto", "NaN"));
		nvps.add(new BasicNameValuePair("pstrong", ""));
		nvps.add(new BasicNameValuePair("llnick", ""));
		nvps.add(new BasicNameValuePair("sign", ""));
		nvps.add(new BasicNameValuePair("need_sign", ""));
		nvps.add(new BasicNameValuePair("isIgnore", ""));
		nvps.add(new BasicNameValuePair("full_redirect", "true"));
		nvps.add(new BasicNameValuePair("popid", ""));
		nvps.add(new BasicNameValuePair("callback", ""));
		nvps.add(new BasicNameValuePair("guf", ""));
		nvps.add(new BasicNameValuePair("not_duplite_str", ""));
		nvps.add(new BasicNameValuePair("need_user_id", ""));
		nvps.add(new BasicNameValuePair("poy", ""));
		nvps.add(new BasicNameValuePair("gvfdcname", "10"));
		nvps.add(new BasicNameValuePair("gvfdcre", ""));
		nvps.add(new BasicNameValuePair("from_encoding", ""));
		nvps.add(new BasicNameValuePair("sub", ""));
		nvps.add(new BasicNameValuePair("TPL_password_2","66c13c6da4a7b9124586f615c5183a2bffc7a2b551508cb0fa714f0645afddb09981e852ad45fdce9e0cdea16d991ee9bd5231a14764c36ac77fa8ab58209fb12734f0214140477e126f692f2185b47b3180f10f5c9e61438a77f1031defa97838eb622441c42d36383b2a7f2e31754b98a54df710604e3674fa06a1ee91eca8"));
		nvps.add(new BasicNameValuePair("loginASR", "1"));
		nvps.add(new BasicNameValuePair("loginASRSuc", "1"));
		nvps.add(new BasicNameValuePair("allp","assets_css=2.4.2/login_pc.css&enup_css=2.4.2/enup_pc.css&assets_js=2.4.2/login_performance.js"));
		nvps.add(new BasicNameValuePair("oslanguage", "zh-CN"));
		nvps.add(new BasicNameValuePair("sr", "1440*900"));
		nvps.add(new BasicNameValuePair("osVer", "windows|6.1"));
		nvps.add(new BasicNameValuePair("naviVer", "chrome|45.0245485"));
		return nvps;
	}

	//过程需要重新考证
    public boolean Tm_login(String uname, String passwd)
    {
    	try{
    	  //首先是定义一下post上去的参数
    	  System.out.println("begin the tmall login");
    	  List<NameValuePair> nvps=getLoginParam("");
    	  //其次是post本身
    	  HttpPost hp=getLoginPost();
    	  hp.setEntity(new UrlEncodedFormEntity(nvps));
    	  Response resp=mnet.execPost(hp);
    	  if(resp.getState()==0&&resp.getResp().getStatusLine().getStatusCode()==200)
    	  {
    		  //得到了第一次返回的结果
    		  String content = resp.getContent();
    		  //分析结果看是否需要验证码等问题
    		  if (content.contains("请输入验证码")) 
    		  {
				 System.out.println("需要输入验证码");
				 Parser ps = new Parser(content);
				 NodeFilter nf1 = new TagNameFilter("img");
				 NodeFilter nf2 = new HasAttributeFilter("id","J_StandardCode_m");
				 NodeFilter nf3 = new AndFilter(nf1, nf2);
				 NodeList nl1 = ps.extractAllNodesThatMatch(nf3);
				 ImageTag it = (ImageTag) nl1.elementAt(0);
				 // System.out.println(it.getAttribute("data-src"));
				 String url=it.getAttribute("data-src");
				 //重新修改了验证码的情况
				 VFrame vf1=new VFrame(mnet,"tm",url,"验证码");
				 String chkcode = vf1.getText();
				 //重新初始化post请求的情况
				 nvps=getLoginParam(chkcode);
				 hp=getLoginPost();
		    	 hp.setEntity(new UrlEncodedFormEntity(nvps));
		    	 resp=mnet.execPost(hp);
				 if (resp.getState() == 0&&resp.getResp().getStatusLine().getStatusCode()==200) 
				 {	
					 content=resp.getContent();
					 System.out.println(content);
					if(!content.contains("验证码错误"))
					{
						_tb_token_=Tool.getValByPattern(content,"(?<=_tb_token_=)\\w+");
						System.out.println("tmall login success!");
						return true;
					}
					else{
						while(content.contains("验证码错误"))
						{
							System.out.println("验证码输入错误");
							//System.out.println("请输入验证码:");
							VFrame vf2=new VFrame(mnet,"tm",url,"验证码错误");
							chkcode = vf2.getText();
							//chkcode = Tool.getScan().next();
							nvps=getLoginParam(chkcode);
							hp=getLoginPost();
					    	hp.setEntity(new UrlEncodedFormEntity(nvps));
					    	resp=mnet.execPost(hp);
					    	if(resp.getState()==0&&resp.getResp().getStatusLine().getStatusCode()==200)
					    	{
					    		content=resp.getContent();
					    		if(!content.contains("验证码错误"))
					    		{
					    			System.out.println("tmall login success!");
					    			_tb_token_=Tool.getValByPattern(content,"(?<=_tb_token_=)\\w+");
									return true;
					    		}
					    	}
					    	else{
					    		break;
					    	}
						}
					}
				  } 
				} else {
					//System.out.println(content);
					System.out.println("tmall login success!");
					_tb_token_=Tool.getValByPattern(content,"(?<=_tb_token_=)\\w+");
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
    	System.out.println("tmall login fail!");
    	return false;
    }

	// 在删除购物车中的物品前需要先访问一次购物车，这样可以一些必要的信息
	public Result getCart() {
		System.out.println("tmall begin to get cart");
		Result result=new Result();
		result.vpn=getVpn();//1
		result.extype="getcart";//2
		result.httptype="get";//3
		result.filetype="html";//4
		result.shop="tm";//5
		String url = "https://cart.taobao.com/cart.htm";
		result.url=url;//6
		result.ctime=new Date().getTime();//7
		Response resp = mnet.execGet(new HttpGet(url));
		try {
			if (resp.getState() == 0) {
				if (resp.getResp().getStatusLine().getStatusCode() == 200) {
					String content = resp.getContent();
					// 获得相应的token的取值
					Parser token_ps = new Parser(content);
					NodeFilter token_ft1 = new TagNameFilter("input");
					NodeFilter token_ft2 = new HasAttributeFilter("id","_tb_token_");
					NodeFilter token_ft3 = new AndFilter(token_ft1, token_ft2);
					NodeList token_nl = token_ps.extractAllNodesThatMatch(token_ft3);
					InputTag token_it = (InputTag) token_nl.elementAt(0);
					_tb_token_ = token_it.getAttribute("value");
					// 获得firstData的相应数据
					// 需要生成很多相应的数据
					String firstData = "";
					Pattern pn = Pattern.compile("(?<=firstData =).*(?=catch)");
					Matcher mc = pn.matcher(content);
					if (mc.find())
						firstData = content.substring(mc.start(), mc.end());
					//把网页中方放回来的json数据进行分析
					JSONObject jo = new JSONObject(firstData);
					String list = jo.getString("list");
					JSONArray ja = new JSONArray(list);
					// 购物车中的店铺的个数
					int shopSize = ja.length();
					for (int i = 0; i < shopSize; i++) {
						// 接下来看每个shop中的item的情况
						// 似乎并用不上这个工具哦
						JSONObject jjt = ja.getJSONObject(i);
						String shopId = jjt.getString("id");
						String listi = ja.getString(i);
						// 然后获得items所在的string，这样方便分析
						String itemss = new JSONObject(listi).getString("bundles");
						JSONArray bundles_array = new JSONArray(itemss);
						String bundles_value = bundles_array.getString(0);
						String items_str = new JSONObject(bundles_value).getString("orders");
						//System.out.println(items_str);

						JSONArray jas = new JSONArray(items_str);
						int itemsize = jas.length();
						// 这个是针对每个店铺的商品统计
						for (int j = 0; j < itemsize; j++) {
							JSONObject jjjt = jas.getJSONObject(j);
							String cartId = jjjt.getString("cartId");
							String skuId = jjjt.getString("skuId");
							String itemId = jjjt.getString("itemId");
							String operate = cartId;
							Tm_product pro = new Tm_product();
							pro.shopId = shopId;
							pro.cartId = cartId;
							pro.skuId = skuId;
							pro.itemId = itemId;
							pro.operate = operate;
							map.put(pro.itemId, pro);
						}

					}
					System.out.println("there are "+map.size()+" products!");
					if(map.size()>0)
					{
						System.out.println("get cart success!");
						result.status=0;//8
						result.filesize=resp.getFileSize();//9
						result.ip=mnet.getIp();//10
						result.ptime=mnet.getPing(result.ip);//11
						result.duration=resp.getDuration();//12;
						return result;
					}
				
					  
				}

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {

		}
		System.out.println("get cart fail!");
		return result;
	}
    
	
	// 假设根据商品的url来添加,这样实在不行还能获得页面
	public Result addCart(String url) {
		//getPage("https://gm.mmstat.com/tmalljy.1.1?shopid=58613162&itemid=520504424524&pos=detailclickadd&rn=f924083f6270ac1b67c60360a610f150&catid=1512&itemId=520504424524&pagetype=item&sellerId=268451883&_tm_cache=1440337422382");
		//getPage("https://gm.mmstat.com/tmalljy.3.1?action=detail_cartclick&userType=4&rn=f924083f6270ac1b67c60360a610f150&catid=1512&itemId=520504424524&pagetype=item&sellerId=268451883&_tm_cache=1440337422384");
		//getPage("https://gm.mmstat.com/tmalldetail.50.3?rn=f924083f6270ac1b67c60360a610f150&catid=1512&itemId=520504424524&pagetype=item&sellerId=268451883&_tm_cache=1440337422385");
		//getPage("https://buy.tmall.com/login/buy.do?from=itemDetail&var=login_indicator&id=undefined&shop_id=undefined&cart_ids=&t=1440337422407");
		//getPage("https://mdskip.taobao.com/extension/get_tb_ck_ps.htm?varName=__BTrackID&t=1440337423758");
		System.out.println("tmall begin to add cart");
		Result result=new Result();
		result.vpn=getVpn();//1
		result.extype="addcart";//2
		result.httptype="get";//3
		result.filetype="json";//4
		result.shop="tm";//5
		String itemId = Tool.getValByPattern(url, "(?<=id\\=)\\d+");
		String skuId = Tool.getValByPattern(url, "(?<=skuId\\=)\\d+");
		Response resp = mnet.execGet(new HttpGet(url));
		if (resp.getState() == 0) {
			if (resp.getResp().getStatusLine().getStatusCode() == 200) {
				try {
					//程序只有运行到这里才算运行成功
					String content = resp.getContent();
					Pattern pa = null;
					//还是先用来获得一些其他方面的信息比较好
					String _ksTS = String.valueOf(new Date().getTime())+ "_3254";
					String callback = "jsonp3255";
					// 接下来是tsid也就是cookie里面的t
					String tsid = mnet.getCookie("t");
					// 还是先把js找出来然后变换后在匹配
					String xmlc = Tool.getValByPattern(content,"\\{\"api\":.*");
					// 还是先获得add里面的数据好了
					/*
					 * add:{"deliveryCityCode":340100,"campaignId":0,"from_etao":
					 * "","umpkey":"","items":[{"itemId":"520495134793","skuId":
					 * 3102333455912
					 * ,"iChannel":"","quantity":1,"serviceInfo":"",
					 * "extraAttribute":{}}]}
					 */
					String deliveryCityCode = "340100";
					int campaignId = 0;
					// 如果是来之etao其值为1
					String from_etao = "";
					String umpkey = "";
					String items = "";
					if (itemId.equals("")) {
						itemId = Tool.getValue(xmlc, "itemDO.itemId");
					}
					if (skuId.equals("")) {
						skuId = Tool.getValue(xmlc,
								"valItemInfo.skuList[0].skuId");
					}
					String add = "{\"deliveryCityCode\":340100,\"campaignId\":0,\"from_etao\":\"\",\"umpkey\":\"\",\"items\":[{\"itemId\":\""
							+ itemId
							+ "\",\"skuId\":"
							+ skuId
							+ ",\"iChannel\":\"\",\"quantity\":1,\"serviceInfo\":\"\",\"extraAttribute\":{}}]}";
					String sellerId = "";
					sellerId = Tool.getValue(xmlc, "itemDO.userId");
					//System.out.println("sellerId:");
					//System.out.println(sellerId);
					String categoryId = "";
					categoryId = Tool.getValue(xmlc, "itemDO.categoryId");
					//System.out.println("categoryId");
					//System.out.println(categoryId);
					String item_url_refer = "https%3A%2F%2Flist.tmall.com%2Fsearch_product.htm%3Fq%3D%25CA%25D6%25BB%25FA%26type%3Dp%26spm%3Da220m.1000858.a2227oh.d100%26from%3D.list.pc_1_searchbutton";
					String ggurl = "https://fbuy.tmall.com/cart/addCartItems.do?";
					String params = "";
					// 组装gurl
					params += "_tb_token_=" + encode(_tb_token_) + "&";
					params += "add=" + encode(add) + "&";
					params += "tsid=" + encode(tsid) + "&";
					params += "itemId=" + encode(itemId) + "&";
					params += "sellerId=" + encode(sellerId) + "&";
					params += "categoryId=" + encode(categoryId) + "&";
					params += "root_refer=" + "&";
					params += "item_url_refer=" + item_url_refer + "&";
					params += "_ksTS=" + encode(_ksTS) + "&";
					params += "callback=" + encode(callback);
					HttpGet hg = new HttpGet(ggurl + params);
					result.url=ggurl+params;//6
					hg.setHeader(":host", "fbuy.tmall.com");
					hg.setHeader(":method", "GET");
					hg.setHeader(":path", "/cart/addCartItems.do?" +params);
					hg.setHeader(":scheme", "https");
					hg.setHeader(":version", "HTTP/1.1");
					hg.setHeader("accept", "*/*");
					hg.setHeader("accept-encoding", "gzip, deflate, sdch");
					hg.setHeader("accept-language", "zh-CN,zh;q=0.8,en;q=0.6");
					hg.setHeader("referer","https://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.33.gdIVZa&id=520504424524&skuId=3102374914361&areaId=340100&cat_id=50024400&rn=f924083f6270ac1b67c60360a610f150&standard=1&user_id=268451883&is_b=1");
					hg.setHeader("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36");
					result.ctime=new Date().getTime();//7
					
					Response resp1 = mnet.execGet(hg);
					if (resp1.getState() == 0) {
						if (resp1.getResp().getStatusLine().getStatusCode() == 200) {
							System.out.println(resp1.getContent());
							if(resp1.getContent().contains("\"success\":true"))
							{
								System.out.println("add cart success");
								result.status=0;//8
								result.ip=mnet.getIp();//9
								result.duration=resp1.getDuration();//10
								result.ptime=mnet.getPing(result.ip);//11
								result.filesize=resp1.getFileSize();//12
								return result;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("add cart fail");
		return result;
	}

	public String encode(String temp) {

		try {
			return java.net.URLEncoder.encode(temp, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// 先通过itemId看看到底能不能删除呢
	// 看来还是cookie的问题呢？
	public Result delCart(String url) {
		// 再删除购物车前先访问两个页面
		//String furl1 = "https://go.mmstat.com/tbapp.1002.1.3?t=1440222762557&url=https://cart.taobao.com/cart.htm?spm=a220o.1000855.a2226mz.14.G0qUff&from=btop";
		//Response fres1 = getPage(furl1);
		//String furl2 = "https://gm.mmstat.com/tbcart.8.8?cache=1440222762630";
		//Response fres2 = getPage(furl2);
		System.out.println("tmall begin to del cart");
		Result result=new Result();
		result.vpn=getVpn();//1
		result.extype="delcart";//2
		result.httptype="post";//3
		result.filetype="json";//4
		result.shop="tm";//5
		String mid = Tool.getValByPattern(url, "(?<=id=)\\d+");
        //通过id获得商品的相应信息
		Tm_product tp = map.get(mid);
		// 构建整个post的数据
		String _input_charset = "utf-8";
		String tk = _tb_token_;
		String data = "";
		String shopId = tp.shopId;
		String comboId = "0";
		String shopActId = "0";
		String quantity = tp.quantity + "";
		String cartId="";
		//String cartId = tp.cartId;
		String skuId = tp.skuId;
		String itemId = tp.itemId;
		String operate = cartId;
		String in_type = "delete";
		data = "[{\"shopId\":\""
				+ shopId
				+ "\",\"comboId\":0,\"shopActId\":0,\"cart\":[{\"quantity\":1,\"cartId\":\""
				+ cartId + "\",\"skuId\":\"" + skuId + "\",\"itemId\":\""
				+ itemId + "\"}],\"operate\":[\"" + operate
				+ "\"],\"type\":\"delete\"}]";
		//System.out.println("data:" + data);
		String shop_id = "0";
		// 这个就相当于1970的时间
		String t = String.valueOf(new Date().getTime());
		String type = "delete";
		String page = "1";
		String _thwlang = "zh_CN";
		//通过cookie获得
		String ct = mnet.getCookie("t");
		//开始组装整个post请求
		String post_url = "https://cart.taobao.com/json/AsyncUpdateCart.do";
		HttpPost hp = new HttpPost(post_url);
		result.url=post_url;//6
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("_input_charset", _input_charset));
		nvps.add(new BasicNameValuePair("tk", tk));
		nvps.add(new BasicNameValuePair("data", data));
		nvps.add(new BasicNameValuePair("shop_id", shop_id));
		nvps.add(new BasicNameValuePair("t", t));
		nvps.add(new BasicNameValuePair("type", type));
		nvps.add(new BasicNameValuePair("ct", ct));
		nvps.add(new BasicNameValuePair("page", page));
		nvps.add(new BasicNameValuePair("_thwlang", _thwlang));
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(nvps, "utf-8");
			//System.out.println("entity:");
			//System.out.println(entity.getContentLength());
			hp.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		hp.setHeader(":host", "cart.taobao.com");
		hp.setHeader(":method", "POST");
		hp.setHeader(":path", "/json/AsyncUpdateCart.do");
		hp.setHeader(":scheme", "https");
		hp.setHeader(":version", "HTTP/1.1");
		hp.setHeader("accept", "application/json, text/javascript, */*; q=0.01");
		hp.setHeader("accept-encoding", "gzip, deflate");
		hp.setHeader("accept-language", "zh-CN,zh;q=0.8,en;q=0.6");
		hp.setHeader("content-type","application/x-www-form-urlencoded; charset=UTF-8");
		hp.setHeader("origin", "https://cart.taobao.com");
		hp.setHeader("referer","https://cart.taobao.com/cart.htm?spm=a220o.1000855.a2226mz.14.4o2tk9&from=btop");
		hp.setHeader("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
		hp.setHeader("x-requested-with", "XMLHttpRequest");
		// 顺便查看一下httpclient的header机制
		result.ctime=new Date().getTime();//7
		Response resp = mnet.execPost(hp);
		if (resp.getState() == 0) {
			if (resp.getResp().getStatusLine().getStatusCode() == 200) {
				System.out.println(resp.getContent());
				//result.status=0;//8
				result.ip=mnet.getIp();//9
				result.ptime=mnet.getPing(result.ip);//10
				result.duration=resp.getDuration();//11
				result.filesize=resp.getFileSize();//12
				if(resp.getContent().contains("\"success\":true"))
				{
					System.out.println(resp.getContent());
					System.out.println("del cart success");
					result.status=0;//8
					result.ip=mnet.getIp();//9
					result.ptime=mnet.getPing(result.ip);//10
					result.duration=resp.getDuration();//11
					result.filesize=resp.getFileSize();//12
					return result;
				}
			}
		}
		System.out.println("del cart fail");
		return result;
	}
	
	public void loadCookie()
	{
		mnet.readCookie("tm");
	}
	
	public void saveCookie()
	{
		mnet.storeCookie("tm");
	}
    
	public static void main(String[] args) {
		String uname = "ge-jq@qq.com";
		String upass = "123456..abc";
		Tm_simulator ts = new Tm_simulator();
		ts.setVpn(Result.vpn);
		String ip=Result.vpn.split("\\+")[1];
		Log.getDateTime("Begin_time:");
		Log.recordLogFile("\t\t"+Result.vpc+"\t"+new Ping().doPing(ip, 3)+"\n");
		boolean isLogin = ts.Tm_login(uname, upass);
		Result result = null;
		if(isLogin){
			result = ts.getCart();
			Log.recordLogFile("\t\t"+result.toString()+"\n");
			System.out.println(result);
			result = ts
					.addCart("https://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.1.1AFmXO&id=37580131307&skuId=3106570272869&areaId=340100&cat_id=50024406&rn=c6d1e36949ec61e768c1d7936d0234a1&standard=1&user_id=1645053348&is_b=1");
			Log.recordLogFile("\t\t"+result.toString()+"\n");
			System.out.println(result);
			result = ts.getCart();
			Log.recordLogFile("\t\t"+result.toString()+"\n");
			System.out.println(result);
			result = ts
					.delCart("https://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.1.1AFmXO&id=37580131307&skuId=3106570272869&areaId=340100&cat_id=50024406&rn=c6d1e36949ec61e768c1d7936d0234a1&standard=1&user_id=1645053348&is_b=1");
			Log.recordLogFile("\t\t"+result.toString()+"\n");
			Log.getDateTime("End_time:");
			System.out.println(result);
		}
		ts.closeAll();
	}
}
