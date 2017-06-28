import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

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
		private String Kastgs="00000000";
		private String Kc="00000000";	//数据库
		data d=new data();
		String willsend="";
		public SendThread(Socket socket){
			this.socket=socket;
		}
		
		@Override
		public void run(){
			//String ip=socket.getInetAddress().getHostAddress();
			System.out.println("connected!");
			t1.setText(t1.getText()+"connected!\n\n");
			try {
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
				String str="";
				String tmp="";
				int tmp2;
				int flag=0;
				while((tmp2=reader.read())!=-1){
					if(tmp2=='完')break;
					str+=(char)tmp2;
				}	//str为从client接收的数据
				//str=str.substring(0, str.length()-4);
				System.out.println(str);//密文内容
				t1.setText(t1.getText()+"收到请求！\n\n");
				s=d.decode(str, key);
				for(int i=0;i<s.size();++i){
					System.out.println(i+":"+s.get(i));	//名文内容
				}
				tmp="";
				tmp+=(char)1;
				ws.add(tmp);
				tmp=randomkey();
				key.add(Kastgs);
				key.add(Kc);
				ws.add(tmp);
				ws.add(IDtgs);
				ws.add(getTS());
				ws.add(lifetime);
				ws.add(ws.get(1));
				ws.add(s.get(1));
				ws.add(getip(socket));
				ws.add(ws.get(2));
				ws.add(ws.get(3));
				ws.add(ws.get(4));
				tmp=d.encode(ws, key);
				willsend=tmp;
				

				for(int i=0;i<ws.size();++i){
					System.out.println(i+":"+ws.get(i));
				}

				key.remove(0);
				for(int i=0;i<key.size();++i){
					System.out.println("key----:"+key.get(i));
				}
				ws=d.decode(tmp, key);
				for(int i=0;i<ws.size();++i){
					System.out.println(i+":"+ws.get(i));
				}
				for(int i=0;i<ws.size();++i){
					for(int j=0;j<ws.get(i).length();++j){
						System.out.print((int)ws.get(i).charAt(j));
					}
					System.out.println("");
				}

				for(int i=0;i<willsend.length();++i){
					System.out.print((int)willsend.charAt(i)+"-");
				}
				System.out.println("");
				
				//System.out.println("size----:"+tmp.length());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			System.out.println("willsend----:"+willsend);
			writer.println(willsend);
			t1.setText(t1.getText()+"已发送认证！\n\n");
			writer.flush();
			writer.close();
			try {
				reader.close();
				//socket.close();
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
		MyFramePanel frame = new MyFramePanel();
		//t1.setText(t1.getText()+"\nsdfg\n\n");
	}
	static TextArea t1= new TextArea(23,78);

	static class MyFramePanel extends JFrame{
		
		
		//TextArea t1= new TextArea(23,78); //构造一个文本域
			
		JPanel p1 = new JPanel();
		 
		
	MyFramePanel(){
		this.setSize(600,400);
		setResizable(false);
		Container container = this.getContentPane();
		container.setLayout(new FlowLayout());
				
		p1.add(t1);
		t1.setText("Listening......\n\n");	
		container.add(p1);
		
		this.setTitle("AS服务器");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	}
	
}

