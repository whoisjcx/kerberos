import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TGS {
	
	private String ID="IDtgs123";	//ID��
	private String IP;	//IP��ַ
	private int port=2345;	//�����˿�
	private String lifetime="00005000";
	private String Kastgs="12345678";
	private String[] pack2={"IDv:","Kc-tgs:","IDc:","IPc:","IDtgs:","time:","lifetime:","IDc:","Addrc:","time:"};
	
	class SendThread extends Thread{
		private Socket socket=null;  
		private BufferedReader reader;
		private PrintWriter writer;
		private ArrayList<String> s=new ArrayList<String>();
		private ArrayList<String> key=new ArrayList<String>();	 
		private ArrayList<String> ws=new ArrayList<String>();
		private String IDv="";
		private String Ktgsv="00000000";	//TODO �����ݿ��
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
				System.out.println("Connected");
				while((tmp2=reader.read())!=-1){
					if(tmp2=='��') break;
					str+=(char)tmp2;
				}	
				System.out.println("str size:"+str.length());
				System.out.println("str----:"+str);	
				
				tmp2=str.charAt(0);
				t2.setText(t2.getText()+"�յ�"+tmp2+"�����ݰ�,���ģ�\n");  
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
					t1.append("δ֪���ݰ�������");
				}
				else{
					key.add(Kastgs);
					s=d.decode(str, key);
					for(int i=1;i<s.size();++i){
						System.out.println("c->tgs----:"+i+s.get(i));
						t2.append(pack2[i-1]+"\n\t"+s.get(i));
						t2.append("\n");
					}
					t1.setText(t1.getText()+s.get(8)+"�������"+s.get(1)+"\n");
					if(!s.get(8).equals(s.get(3))){
						ws.clear();
						tmp2=1<<7;
						tmp+=(char)tmp2;
						ws.add(tmp);
						willsend=d.encode(ws, null);
						t1.append("��ð�û�������");
					}
					else{
						long time2=System.currentTimeMillis();
						long time1=Long.parseLong(s.get(6));
						time2%=100000000;
						if(time2-time1>5000){
							ws.clear();
							tmp2=1<<7;
							tmp+=(char)tmp2;
							ws.add(tmp);
							willsend=d.encode(ws, null);
							t1.append("��֤��ʱ������");
						}
						else{

							Ktgsv=sql.select(s.get(1));
							if(Ktgsv==null){
								ws.clear();
								tmp2=1<<7;
								tmp+=(char)tmp2;
								ws.add(tmp);
								willsend=d.encode(ws, null);
								t1.append("�޴˷��������ܾ����ݰ�");
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
								t1.append("��֤Ʊ�ݰ�");
							}
							for(int i=0;i<ws.size();++i){
								System.out.println(i+":"+ws.get(i));
							}
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
			t1.setText(t1.getText()+"�ѷ��ͣ�\n\n");
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
	
	static TextArea t1= new TextArea(19,39);
	static TextArea t2= new TextArea(19,39);

	static class MyFramePanel2 extends JFrame{								
				JPanel p1 = new JPanel();
				JPanel p2 = new JPanel();		
				JLabel l1 = new JLabel("�¼�");
				JLabel l2 = new JLabel("�������");
				JButton b1 = new JButton("����");
				JButton b2 = new JButton("����");
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
					
					b1.setFont(new Font("����",Font.PLAIN,15));
					b1.setForeground(Color.BLUE);
					b1.setBackground(Color.PINK);
					b1.setBorderPainted(false);
					b2.setFont(new Font("����",Font.PLAIN,15));
					b2.setForeground(Color.BLUE);
					b2.setBackground(Color.PINK);
					b2.setBorderPainted(false);
					l1.setFont(new Font("����",Font.PLAIN,15));
					l2.setFont(new Font("����",Font.PLAIN,15));
					
					b1.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							t1.setText("Listening......");
						}
					});
					b2.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							t2.setText("");
						}
					});
				
					t1.setText("Listening......\n\n");	
					container.add(p1);
					container.add(p2);
					
					this.setTitle("TGS������");
					this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					this.setVisible(true);
					
					setLocation(200, 50);  
			        //����ͼƬ��·���������·�����߾���·��������ͼƬ����"java��Ŀ��"���ļ��£�  
			        String path = "background.jpg";  			   
			        ImageIcon background = new ImageIcon(path);  			        
			        JLabel label = new JLabel(background);  			       
			        label.setBounds(0, 0, this.getWidth(), this.getHeight());  			        
			        JPanel imagePanel = (JPanel) this.getContentPane();  
			        imagePanel.setOpaque(false);  
			        			      			       
			        this.getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));  
			      
			        p1.setOpaque(false);
					p2.setOpaque(false);
					
			        setVisible(true); 
				} 								
		}
	
	static class mysql{
		// ���ݿ����ƣ�����Ա�˺š�����
		 //�����������ݿ����ӣ��������ת��Ϊutf-8(����¼������)
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

