import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SERVER {
	
	private int port=3456;	//监听端口
	
	class SendThread extends Thread{
		private Socket socket=null;  
		private BufferedReader reader;
		private PrintWriter writer;
		private String Ktgsv="00000000";	
		
		private ArrayList<String> key=new ArrayList<String>();	// 要从数据库读  
		data d=new data();
		
		public SendThread(Socket socket){
			this.socket=socket;
		}
		
		@Override
		public void run(){
			//String ip=socket.getInetAddress().getHostAddress();
			VtoC vc=new VtoC();
			System.out.println("Connected");
			try {
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
				String str="";
				String tmp="";
				int tmp2;
				int flag=0;
				while((tmp2=reader.read())!=-1){
					if(tmp2=='完') break;
					str+=(char)tmp2;
				}
				System.out.println("server47行:"+str);
				key.add(Ktgsv);
				vc.setS(d.decode(str, key));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			vc.vtoc();
			String willsend=d.encode(vc.getnewS(),vc.getnewKey());
			writer.println(willsend);
			writer.flush();
			
			writer.close();
			try {
				reader.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class ListenThread extends Thread{
		
		private ServerSocket server=null;
		Socket socket=null;
		@Override
		public void run(){
			System.out.println("Server Listening");
			try {
					server=new ServerSocket(port);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}	
			while(true){
				try {
					socket=server.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(socket!=null)
				{
					new SendThread(socket).start();
				}
			}
		}
	}
	
	public void Serverstart()
	{
		new ListenThread().start();
	}
	
	public static void main(String args[]){
		String s="";
		SERVER se=new SERVER();
		se.Serverstart();
		
	}
}

class VtoC{
	
	public void setS(ArrayList<String> S)
	{
		this.S=S;
	}
	private ArrayList<String> S=new ArrayList<String>();
	
	private ArrayList<String> newkey=new ArrayList<String>();
	private ArrayList<String> newS=new ArrayList<String>();
	
	public void vtoc()
	{
		for(int i=0;i<S.size();++i)
		{
			System.out.println(S.get(i));
		}
		
		newkey.add(S.get(1));
		char ch=5;
		String tem="";
		tem+=ch;
		newS.add(tem);
		long ll=Long.parseLong(S.get(9));
		ll+=1;
		tem=new String(Long.toString(ll));
		newS.add(tem);
	}
	
	public ArrayList<String> getnewKey(){
		return newkey;
	}
	
	public ArrayList<String> getnewS(){
		return newS;
	}
}


