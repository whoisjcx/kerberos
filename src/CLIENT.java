import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class CLIENT {

	int port;
	String ipAS;
	String ipTGS;
	String ipSERVER;
	
	public CLIENT(int port,String ipAS,String ipTGS,String ipSERVER){
		this.port=port;
		this.ipAS=ipAS;
		this.ipTGS=ipTGS;
		this.ipSERVER=ipSERVER;
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
		
		socket=new Socket(ipAS,port);
		if(socket!=null)
		{
			reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer=new PrintWriter(socket.getOutputStream());
			writer.println(CtoAS0000());
			writer.flush();
			
			String str1="";
			String temstr="";
			while ((temstr = reader.readLine()) != null) {
		        str1+=temstr;
		      }
			
			//收到信息保存在str1中
			writer.flush();
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
}
