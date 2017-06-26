import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class AS {
	
	private String ID;	//ID号
	private String IP;	//IP地址
	private int port=1234;	//监听端口
	private String IDtgs="IDtgs123";	//tgs的ID
	private String lifetime="00005000";
	
	class SendThread extends Thread{
		private Socket socket=null;  
		private BufferedReader reader;
		private PrintWriter writer;
		private ArrayList<String> s=new ArrayList<String>();
		private ArrayList<String> key=new ArrayList<String>();
		private ArrayList<String> ws=new ArrayList<String>();
		data d=new data();
		public SendThread(Socket socket){
			this.socket=socket;
		}
		
		@Override
		public void run(){
			//String ip=socket.getInetAddress().getHostAddress();
			try {
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer=new PrintWriter(socket.getOutputStream());
				String str="";
				//while((str+=reader.readLine())!=null){}	//str为从client接收加密后的数据
				//s=d.decode(str,key);						//解密为list
				
				/***
				 *test
				 */
				String tmp="";
				tmp+=(char)0;
				s.add(tmp);
				s.add("idc12345");
				s.add("idtgs123");
				s.add(getTS());
				
				tmp="";
				tmp+=(char)1;
				ws.add(tmp);
				tmp=randomkey();
				ws.add(tmp);
				ws.add(IDtgs);
				ws.add(getTS());
				ws.add(lifetime);
				ws.add(ws.get(1));
				ws.add(s.get(1));
				ws.add(getip(socket));
				ws.add(ws.get(2));
				ws.add(ws.get(4));
				for(int i=0;i<ws.size();++i){
					System.out.println(i+":"+ws.get(i));
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//!!!!!!!!!!!!!!!!!!!!!
			
			String willsend="";
			
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
		
		ServerSocket server=null;
		Socket socket=null;
		@Override
		public void run(){
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
	
	public void ASstart()
	{
		new ListenThread().start();
	}
	
	public String randomkey(){
		String k="";
		Random r=new Random();
		for(int i=0;i<8;++i){
			k+=(char)r.nextInt(256);
		}
		return k;
	}
	
	public String getTS(){
		long t=(long) System.currentTimeMillis();
		String k=new String(Long.toString(t));
		k=k.substring(k.length()-8,k.length());
		return k;
	}
	
	public String getip(Socket socket){
		String s=socket.getInetAddress().getHostAddress();
		String res="0000";
		String[] tmp=s.split("\\.");
		int tmp2;
		for(int i=0;i<tmp.length;++i){
			tmp2=Integer.parseInt(tmp[i]);
			res+=(char)tmp2;
		}
		return res;
	}
	
	
	public static void main(String args[]){
		String s="";
		AS as=new AS();
		as.ASstart();
		try {
			Socket socket=new Socket("127.0.0.1",1234);
			s=as.getip(socket);
			System.out.println(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis());
		
	}
}
