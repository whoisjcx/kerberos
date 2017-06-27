import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class TGS {
	
	private String ID;	//ID号
	private String IP;	//IP地址
	private int port=2345;	//监听端口
	private String IDtgs="IDtgs123";	//tgs的ID
	private String lifetime="00005000";
	private String Kastgs="00000000";
	
	class SendThread extends Thread{
		private Socket socket=null;  
		private BufferedReader reader;
		private PrintWriter writer;
		private ArrayList<String> s=new ArrayList<String>();
		private ArrayList<String> key=new ArrayList<String>();	 
		private ArrayList<String> ws=new ArrayList<String>();
		private String IDv="";
		private String Kcv="00000000";	//TODO 从数据库读
		String willsend;
		data d=new data();
		
		public SendThread(Socket socket){
			this.socket=socket;
		}
		
		@Override
		public void run(){
			//String ip=socket.getInetAddress().getHostAddress();
			System.out.println("Connected");
			try {
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
				String str="";
				String tmp="";
				int tmp2;
				int flag=0;
				while((tmp2=reader.read())!=-1){
					str+=(char)tmp2;
					if(tmp2=='0'){
						flag++;
						if(flag==4) break;
					}
					else flag=0;
				}	//str为从client接收的数据
				str=str.substring(0, str.length()-4);
				key.add(Kastgs);
				s=d.decode(str, key);
				tmp+=(char)3;
				ws.add(tmp);
				ws.add(randomkey());
				IDv=s.get(1);
				ws.add(IDv);
				ws.add(getTS());
				ws.add(ws.get(1));
				ws.add(s.get(8));
				ws.add(s.get(9));
				ws.add(s.get(1));
				ws.add(ws.get(3));
				ws.add(lifetime);
				key.clear();
				key.add(Kcv);
				key.add(s.get(2));
				willsend=d.encode(ws, key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
			System.out.println("Listening");
			
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
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				if(socket!=null)
				{
					new SendThread(socket).start();
				}
				try {
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
	
	public void TGSstart()
	{
		new ListenThread().start();
	}
	
	public static void main(String args[]){
		TGS tgs=new TGS();
		tgs.TGSstart();
	}
}

