package LoginTest;
//作为基础的配置类
//用户名以及配置的url都在这里设置好了
//管理配置的地方就在这里好了

public class Config {
	
	private String vpn;
	
    public Config()
    {
    	
    }
    
    public void setVpn(String vpn)
    {
    	this.vpn=vpn;
    }
    
    public String getVpn()
    {
    	return vpn;
    }
}
