import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
		private String Ktgsv="00000000";	//TODO 从数据库读
		String willsend;
		data d=new data();
		
		public SendThread(Socket socket){
			this.socket=socket;
		}
		
		@Override
		public void run(){
			//String ip=socket.getInetAddress().getHostAddress();
			System.out.println("Connected");
			t1.setText(t1.getText()+"connected!\n\n");
			try {
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
				String str="";
				String tmp="";
				int tmp2;
				int flag=0;
				System.out.println("Connected");
				while((tmp2=reader.read())!=-1){
					str+=(char)tmp2;
					if(tmp2=='0'){
						flag++;
						if(flag==4) break;
					}
					else flag=0;
				}	
				str=str.substring(0, str.length()-4);
				System.out.println("str size:"+str.length());
				System.out.println("str----:"+str);
				t1.setText(t1.getText()+"收到请求！\n\n");
				key.add(Kastgs);
				s=d.decode(str, key);
				
				for(int i=0;i<s.size();++i){
					System.out.println("c->tgs----i:"+s.get(i));
				}
				
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
				
				for(int i=0;i<ws.size();++i){
					System.out.println(i+":"+ws.get(i));
				}
				
				key.clear();
				key.add(Ktgsv);
				key.add(s.get(2));
				System.out.println("key----:"+key.get(1));
				willsend=d.encode(ws, key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(willsend);
			writer.println(willsend);
			t1.setText(t1.getText()+"已发送认证！\n\n");
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
		MyFramePanel2 frame = new MyFramePanel2();
	}
	
	static TextArea t1= new TextArea(23,78);

	static class MyFramePanel2 extends JFrame{
				
		JPanel p1 = new JPanel();
		 
		
	MyFramePanel2(){
		this.setSize(600,400);
		setResizable(false);
		Container container = this.getContentPane();
		container.setLayout(new FlowLayout());
				
		p1.add(t1);
		t1.setText("Listening......\n\n");	
		container.add(p1);
		
		this.setTitle("TGS服务器");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	}
}

