import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TGS {
	
	private String ID;	//ID号
	private String IP;	//IP地址
	private int port=2345;	//监听端口
	private String IDtgs="IDtgs123";	//tgs的ID
	private String lifetime="00005000";
	private String Kastgs="00000000";
	private String[] pack2={"IDv:","Kc-tgs:","IDc:","IPc:","IDtgs:","time:","lifetime:","IDc:","Addrc:","time:"};
	
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
		mysql sql=null;
		
		public SendThread(Socket socket){
			this.socket=socket;
		}
		
		@Override
		public void run(){
			//String ip=socket.getInetAddress().getHostAddress();
			System.out.println("Connected");
			
			//t1.setText(t1.getText()+"connected!\n\n");
			try {
				sql=new mysql();
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
				String str="";
				String tmp="";
				int tmp2;
				int flag=0;
				System.out.println("Connected");
				while((tmp2=reader.read())!=-1){
					if(tmp2=='完') break;
					str+=(char)tmp2;
				}	
				System.out.println("str size:"+str.length());
				System.out.println("str----:"+str);	
				
				tmp2=str.charAt(0);
				t2.setText(t2.getText()+"收到"+tmp2+"号数据包,明文：\n");  
				Date date=new Date(System.currentTimeMillis()); 
				  DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				  String time=format.format(date); 
				t1.setText(t1.getText()+time+"\n");  
				if(tmp2!=2){
					ws.clear();
					tmp2=1<<7;
					tmp+=(char)tmp2;
					ws.add(tmp);
					willsend=d.encode(ws, null);
					t1.append("未知数据包，错误");
				}
				else{
					key.add(Kastgs);
					s=d.decode(str, key);
					for(int i=1;i<s.size();++i){
						System.out.println("c->tgs----i:"+s.get(i));
						t2.append(pack2[i-1]+"\n\t"+s.get(i));
						t2.append("\n");
					}
					t1.setText(t1.getText()+s.get(8)+"请求访问"+s.get(1)+"\n");
					long time2=System.currentTimeMillis();
					long time1=Long.parseLong(s.get(6));
					time2%=100000000;
					if(time2-time1>5000){
						ws.clear();
						tmp2=1<<7;
						tmp+=(char)tmp2;
						ws.add(tmp);
						willsend=d.encode(ws, null);
						t1.append("验证超时，错误");
					}
					else{

						Ktgsv=sql.select(s.get(1));
						if(Ktgsv==null){
							ws.clear();
							tmp2=1<<7;
							tmp+=(char)tmp2;
							ws.add(tmp);
							willsend=d.encode(ws, null);
							t1.append("无此服务器，拒绝数据包");
						}
						else{
							ws.clear();
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
							key.add(Ktgsv);
							key.add(s.get(2));
							willsend=d.encode(ws, key);
							t1.append("认证票据包");
						}
						for(int i=0;i<ws.size();++i){
							System.out.println(i+":"+ws.get(i));
						}
						
					}
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(willsend);
			writer.print(willsend);
			t1.setText(t1.getText()+"已发送！\n\n");
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
		
		/***
		try {
			mysql sql=new mysql();
			System.out.println(sql.select("IDV12345"));
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		***/
		
	}
	
	static TextArea t1= new TextArea(19,39);
	static TextArea t2= new TextArea(19,39);

	static class MyFramePanel2 extends JFrame{								
				JPanel p1 = new JPanel();
				JPanel p2 = new JPanel();		
				JLabel l1 = new JLabel("事件");
				JLabel l2 = new JLabel("包from->to");
				JButton b1 = new JButton("清屏");
				JButton b2 = new JButton("清屏");
				MyFramePanel2(){
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
					
					this.setTitle("TGS服务器");
					this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					this.setVisible(true);
				} 								
		}
	
	static class mysql{
		// 数据库名称，管理员账号、密码
		 //建立本地数据库连接，编码规则转换为utf-8(正常录入中文)
		String url = "jdbc:mysql://localhost:3306/mytgs?useUnicode=true&characterEncoding=utf8";
		String user = "root";
		String pwd = "root";
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
				pStmt=con.prepareStatement("select vkey from ktgsv where servers = '" + name + "'");
				ResultSet rs=pStmt.executeQuery();
				if(rs.next()){
					String res=rs.getString(1);
					return res;
				}
				else{
					System.out.println("no such server");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
}
}

