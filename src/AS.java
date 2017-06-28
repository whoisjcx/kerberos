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
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
		private mysql sql;
		data d=new data();
		String willsend="";
		public SendThread(Socket socket){
			this.socket=socket;
		}
		
		@Override
		public void run(){
			//String ip=socket.getInetAddress().getHostAddress();
			System.out.println("connected!");
			//t1.setText(t1.getText()+"connected!\n\n");
			try {
				sql=new mysql();
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
				tmp2=str.charAt(0);
				t2.setText(t2.getText()+"收到"+tmp2+"号数据包\n");
				if(tmp2!=0){
					ws.clear();
					tmp2=1<<7;
					tmp+=tmp2;
					ws.add(tmp);
					willsend=d.encode(ws, null);
					t1.append("未知数据包，错误");
				}
				else{
					Date date=new Date(System.currentTimeMillis()); 
					  DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					  String time=format.format(date); 
					t1.setText(t1.getText()+time+"\n");  
					s=d.decode(str, key);
					t1.setText(t1.getText()+s.get(1)+"请求认证！\n");
					for(int i=0;i<s.size();++i){
						System.out.println(i+":"+s.get(i));	//名文内容
						t2.setText(t2.getText()+s.get(i)+"\n");  
					}
					Kc=sql.select(s.get(1));
					t2.setText(t2.getText()+"\n");
					if(Kc==null){
						ws.clear();
						tmp2=1<<7;
						tmp+=tmp2;
						ws.add(tmp);
						t1.append("查无此人，认证失败包");
						willsend=d.encode(ws, null);
					}
					else{
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
						willsend=d.encode(ws, key);
						t1.append("认证票据包");
						
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
					}
					for(int i=0;i<ws.size();++i){
						System.out.println(i+":"+ws.get(i));
					}

					
				}
			} catch (IOException | ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			} 
			System.out.println("willsend----:"+willsend);
			writer.println(willsend);
			t1.setText(t1.getText()+"已发送！\n\n");
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
	
	static TextArea t1= new TextArea(19,39);
	static TextArea t2= new TextArea(19,39);

	static class MyFramePanel extends JFrame{								
				JPanel p1 = new JPanel();
				JPanel p2 = new JPanel();		
				JLabel l1 = new JLabel("事件");
				JLabel l2 = new JLabel("包from->to");
				JButton b1 = new JButton("清屏");
				JButton b2 = new JButton("清屏");
				MyFramePanel(){
					this.setSize(600,400);
					setResizable(false);  					
					Container container = this.getContentPane();
					GridLayout g = new GridLayout(1,2,10,10);
					container.setLayout(g);
										
					p1.add(l1);
					p2.add(l2);
					p1.add(t1);
					p2.add(t2);
					p1.add(b1);
					p2.add(b2);
				
					t1.setText("Listening......\n\n");	
					container.add(p1);
					container.add(p2);
					
					this.setTitle("AS服务器");
					this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					this.setVisible(true);
				} 								
		}	
	
	static class mysql{
		// 数据库名称，管理员账号、密码
		 //建立本地数据库连接，编码规则转换为utf-8(正常录入中文)
		String url = "jdbc:mysql://localhost:3306/myas?useUnicode=true&characterEncoding=utf8";
		String user = "root";
		String pwd = "123456";
		Connection con = null;
		Statement stat=null;
		PreparedStatement pStmt=null;
		mysql() throws ClassNotFoundException, SQLException{
			   Class.forName("com.mysql.jdbc.Driver");
			   con = DriverManager.getConnection(url, user, pwd);
			   stat=con.createStatement();
		}
		public String select(String name){
			try {
				pStmt=con.prepareStatement("select ckey from kasc where clients = '" + name + "'");
				ResultSet rs=pStmt.executeQuery();
				if(rs.next()){
					String res=rs.getString(1);
					return res;
				}
				else{
					System.out.println("no such client");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}

