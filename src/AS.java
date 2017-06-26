import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AS {
	
	private String ID;	//ID��
	private String IP;	//IP��ַ
	private int port;	//�����˿�
	private String IDtgs;	//tgs��ID
	
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
				while((str+=reader.readLine())!=null){}	//strΪ��client���ռ��ܺ������
				s=d.decode(str);						//����Ϊlist
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
		
		private ServerSocket server=null;
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
				try {
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void ASstart()
	{
		new ListenThread().start();
	}
}
