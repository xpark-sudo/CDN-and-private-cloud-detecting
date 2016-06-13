package LoginTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

class Sys{
	public HashMap<String,ArrayList<String>> vpc_vp;
	public HashMap<String,ArrayList<String>> vp_ip;
	
	public Sys(){
		vpc_vp=new HashMap<String, ArrayList<String>>();
		vp_ip=new HashMap<String,ArrayList<String>>();
	}
	
	public int readSysConfigure(){
		System.out.println("System Initalizing...");
		try{
			BufferedReader reader=new BufferedReader(new FileReader("./files/VP.csv"));
			reader.readLine();//first line ignore
			String line=null;
			ArrayList<String> recordVPC=new ArrayList<String> ();
			ArrayList<String> recordVP=new ArrayList<String> ();
			ArrayList<String> recordIP=new ArrayList<String> ();
			while((line=reader.readLine())!=null){
					String []item=line.split(",");
					String vpc=item[0];
					String vp=item[1];
					String ip=item[item.length-1];
					//System.out.println(item[0]);
					if (!vpc.equals("")){
						String keyVPC=vpc;		
						recordVPC.add(keyVPC);
						vpc_vp.put(keyVPC,new ArrayList<String> ());
					}
					if (!vp.equals("")){
						String keyVP=vp;
						recordVP.add(keyVP);
						//System.out.println(keyVP);
						vpc_vp.get(recordVPC.get(recordVPC.size()-1)).add(keyVP);
						vp_ip.put(keyVP,new ArrayList<String> ());
					}
					if(!ip.equals("")){
						String keyIP=ip;
						recordIP.add(keyIP);
						vp_ip.get(recordVP.get(recordVP.size()-1)).add(keyIP);
					}
					
					//System.out.println(ip);
			}
			System.out.println(recordVPC.size());
			System.out.println(recordVP.size());
			//System.out.println(recordIP.size());
			reader.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public String chooseTOuser(String vpc){
		ArrayList<String> vps=this.vpc_vp.get(vpc);
		Random rand=new Random();
		int index=rand.nextInt(vps.size());
		//System.out.println(vps);
		//System.out.println(index);
		String vp=vps.get(index);
		ArrayList<String> info=this.vp_ip.get(vp);
		index=rand.nextInt(info.size());
		String ip=info.get(index);
		return vp+":"+ip;
	}
	
	public int loopDeleteBadVpc(String vpc,User user,int choice,Scanner sc){
		int bad_vpc=0;
		int flag;
		Result.vpc=vpc;
		while (true){
			String vpn=this.chooseTOuser(vpc);
			String vp=vpn.split(":")[0];
			String ip=vpn.split(":")[1];
			bad_vpc++;
			System.out.println(vp+"\t"+ip);
			System.out.println("Ready?");
			String ready=sc.nextLine();
			//System.out.println(vpc_vp.get(vpc).size());
			if (vpc_vp.get(vpc).size()==1 && !(ready.equals("yes"))){
				flag=1;
				//System.out.println("1");
				break;
			}
			if (ready.toLowerCase().equals("yes")){
				flag=0;
				try{
					user.userMethod(vp+"+"+ip,choice);
				}
				catch(Exception e){
					e.printStackTrace();
					return this.loopDeleteBadVpc(vpc, user, choice,sc);
				}
				//System.out.println("2");
				break;
			}
			if (ready.equals("all no")){
				flag=-1;
				//System.out.println("3");
				break;
			}
			if (bad_vpc>=8){
				flag=-1;
				//System.out.println("4");
				break;
			}
		}//end of while
		
		return flag;
}

	
	public void doUserConfig(int choice,Scanner sc){
		User user=new User();
		BufferedReader reader=null;
		try{
			reader=new BufferedReader(new FileReader(user.file_config));
			String line=reader.readLine();
			while(line !=null){
				System.out.println(line);
				int tag;
				tag=this.loopDeleteBadVpc(line, user, choice,sc);
				if (tag==1 || tag==-1){
					line=reader.readLine();
					continue;
				}
				line=reader.readLine();
			}
			reader.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
		}

	}
	
}

class User{
	//public String file_url;
	public String file_config;
	public int n=3;
	
	public User(){
		file_config="./files/configure.txt";
	}
	
	public void userMethod(String vpn,int ch) throws Exception{
		System.out.println("user_method");
		Result.vpn=vpn;
		//面侧     先jd 后yhd
		//Jd_simulator.main(null);
		//Yhd_simulator.main(null);
		//点测
		while(true){
		//Yhd_simulator.main(null);
		Jd_simulator.main(null);
		//Sn_simulator.main(null);
		if (0==ch)
			System.out.println("here 0 break");
		if (1==ch){
			int time_record=0;
			System.out.println("here 1 break");
			while(time_record<1){
				//15 minute
				Thread.sleep(300000);
				//10 minute
				//Thread.sleep(60000);
				//Thread.sleep(30000);
				time_record++;
			}
		}
		
	}
}
	
}


public class Main{
	
	public Main(){}
	
	public static void main(String []args)
	{
		Sys sys=new Sys();
		Scanner sc=new Scanner(System.in);
		sys.readSysConfigure();
		sys.doUserConfig(1,sc);
		System.out.println("System End!!!");
		sc.close();
		System.exit(0);
		return;
	}
}