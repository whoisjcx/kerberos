import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class CLIENT {

	int port;
	String ipAS;
	String ipTGS;
	String ipSERVER;
	String IDc="IDc12345";
	String IDtgs="IDtgs123";
	ArrayList<String> key=new ArrayList<String>();
	
	public CLIENT(int port,String ipAS,String ipTGS,String ipSERVER){
		this.port=port;
		this.ipAS=ipAS;
		this.ipTGS=ipTGS;
		this.ipSERVER=ipSERVER;
		key.add("00000000");
	}
	
	// 返回以下三个的包
	String CtoAS0000()
	{
		return "";
	}
	
	String CtoTGS0010()
	{
		return "";
	}
	
	String CtoS0100()
	{
		return "";
	}
	
	void SendAndReceive() throws UnknownHostException, IOException
	{
		Socket socket=null;
		BufferedReader reader = null;
		PrintWriter writer = null;
		String willsend="";
		String tmp="";
		socket=new Socket(ipAS,port);
		data d=new data();
		if(socket!=null)
		{
			reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer=new PrintWriter(socket.getOutputStream());
			tmp+=(char)1;
			willsend+=tmp;
			willsend+=IDc;
			willsend+=IDtgs;
			willsend+=getTS();
			writer.println(willsend);
			writer.flush();
			ArrayList<String> res=new ArrayList<String>();
			String str1="";
			String temstr="";
			while ((temstr = reader.readLine()) != null) {
				if(temstr.equals("end")) break;
		        str1+=temstr;
		        str1+="\n";
		      }
				str1=str1.substring(0, str1.length()-1);
				res=d.decode(str1, key);
			

		}
		
		writer.close();
		reader.close();
		socket.close();
		
		socket=new Socket(ipTGS,port);
		if(socket!=null)
		{
			reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer=new PrintWriter(socket.getOutputStream());
			writer.println(CtoTGS0010());
			writer.flush();
			String str2="";
			String temstr="";
			while ((temstr = reader.readLine()) != null) {
		        str2+=temstr;
		      }
			
			//收到信息保存在str2中
			writer.flush();
		}
		
		writer.close();
		reader.close();
		socket.close();
		
		socket=new Socket(ipSERVER,port);
		if(socket!=null)
		{
			reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer=new PrintWriter(socket.getOutputStream());
			writer.println(CtoS0100());
			writer.flush();
			String str3="";
			String temstr="";
			while ((temstr = reader.readLine()) != null) {
		        str3+=temstr;
		      }
			
			//收到信息保存在str2中
			writer.flush();
		}
		
		
		
		writer.close();
		reader.close();
		socket.close();
		
	}
	
	public String getTS(){
		long t=(long) System.currentTimeMillis();
		String k=new String(Long.toString(t));
		k=k.substring(k.length()-8,k.length());
		return k;
	}
}
