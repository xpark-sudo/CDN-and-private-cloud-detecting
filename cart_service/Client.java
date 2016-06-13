package LoginTest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
	private Socket server=null;
	private int length=0;
	private byte[] sendBytes=null;
	private DataOutputStream dos=null;
	private DataInputStream dis=null;
	private FileInputStream fis=null;
	private File file=null;
	
	public Client(String path){
		file=new File(path);
	}
	public String run(){
		String str=null;
		try{
			server=new Socket("172.16.1.60",3339);
			dos=new DataOutputStream(server.getOutputStream());
			dis=new DataInputStream(server.getInputStream());
			sendBytes=new byte[1024];
			fis=new FileInputStream(file);
			long len=file.length();
			//System.out.println(len);
			dos.writeLong(len);
			while((length=fis.read(sendBytes,0,sendBytes.length))>0){
				dos.write(sendBytes,0,length);
				dos.flush();
			}
			dos.writeInt(34);
			//System.out.println("hello");
			dos.flush();
			//获得验证码
			while(true){
				while(dis.available()>0){
					//System.out.println("...");
					str=dis.readUTF();
					//System.out.println("str="+str);	
					return str;
				}	
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(dos!=null)
				try {
					dos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(fis!=null)
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(server!=null)
				try {
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	public static void main(String []args){
		try {
			String str=null;
			str=new Client("./Pic/a.jpg").run();
			//System.out.println(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
