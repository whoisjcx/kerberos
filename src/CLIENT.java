import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



public class CLIENT {

	static int port=1234;
	static int OK=0;
	static JList list1 = new JList();// 定义列表框
	static String ipAS="127.0.0.1";
	static int upfile=0;
	static int download=0;
	static String ipTGS="127.0.0.1";
	static String ipSERVER="127.0.0.1";
	static String IDc="IDc12345";
	static String IDtgs="IDtgs123";
	static String IDv="IDv12345";
	static String upname="";
	static String downname="";
	static String downfile="";
	static String Kcv="00000000";
	static String Kas="00000000";
	static JFrame jf = new JFrame("Client");
	ArrayList<String> key=new ArrayList<String>();
	String[] pack1={"Kc-tgs:","IDtgs:","Time:","Lifetime:","Ticket:"};
	String[] pack3={"Kc-v:","IDv:","Time:","Ticket:"};
	String[] pack5={"Time:"};
	//JList list1 = new JList();// 定义列表框
	//Vector<String> filelist = new Vector<String>();
	static DefaultListModel filelist=new DefaultListModel();
	public CLIENT(int cport,String cipAS,String cipTGS,String cipSERVER){
		port=cport;
		ipAS=cipAS;
		ipTGS=cipTGS;
		ipSERVER=cipSERVER;
	}
	void Cstar(){
		new SendAndReceive ().start();
	}
	public static synchronized int UP() {
	    return upfile;
	 }
	public static synchronized int DO() {
	    return download;
	}
	public static synchronized int UP1() {
	    return upfile=1;
	 }
	public static synchronized int DO1() {
	    return download=1;
	}
	public static synchronized int UP0() {
	    return upfile=0;
	 }
	public static synchronized int DO0() {
	    return download=0;
	}
	class SendAndReceive extends Thread{
		
		public void run(){
			try {
				Socket socket=null;
				BufferedReader reader = null;
				PrintWriter writer = null;
				String willsend="";
				String tmp="";
				socket=new Socket(ipAS,port);
				
				data d=new data();
				ArrayList<String> res=new ArrayList<String>();
				if(socket!=null)
				{
					reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
					writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
					tmp+=(char)0;
					res.add(tmp);
					res.add(IDc);
					res.add(IDtgs);
					res.add(getTS());
					key.clear();
					key.add(Kas);
					willsend=d.encode(res, key);
					System.out.println(willsend);
					writer.print(willsend);
					writer.flush();
					String str1="";
					String temstr="";
					int tmp2;
					int flag=0;
					while((tmp2=reader.read())!=-1){
						if(tmp2=='完') break;
						str1+=(char)tmp2;
					}	
					tmp2=str1.charAt(0);
					if(tmp2!=1){
						OK=0;
						socket.close();
						return;
					}
					else OK=1;
					t4.append("收到"+tmp2+"号数据包,明文如下\n");
					res=d.decode(str1, key);
					if(res==null){
						OK=0;
						socket.close();
						return;
					}
					else{
						
						System.out.println("size----:"+str1.length());
						System.out.println("str1----:"+str1);
						for(int i=0;i<str1.length();++i){
							System.out.print((int)str1.charAt(i)+"-");
						}
						System.out.println("");
						for(int i=0;i<key.size();++i){
							System.out.println("key----:"+key.get(i));
						}

						for(int i=1;i<res.size();++i){
							System.out.println(i+":"+res.get(i));
							t4.append(pack1[i-1]+"\n\t"+res.get(i));
							t4.append("\n");
						}
						t4.append("\n");
						System.out.println("ticket size:"+res.get(5).length());
					}
				}
				writer.close();
				reader.close();
				socket.close();
				
				socket=new Socket(ipTGS,2345);
				if(socket!=null)
				{
					ArrayList<String> a=new ArrayList<String>();
					tmp="";
					tmp+=(char)2;
					a.add(tmp);
					a.add(IDv);
					a.add(res.get(5));
					a.add(IDc);
					a.add(getIP());
					a.add(getTS());
					key.clear();
					key.add(res.get(1));
					willsend=d.encode(a,key);

					reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
					writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
					
					System.out.println("c->tgs----:"+willsend);
					for(int i=0;i<willsend.length();++i){
						System.out.print((int)willsend.charAt(i)+"-");
					}
					writer.print(willsend);
					writer.flush();
					String str2="";
					int tmp2;
					while((tmp2=reader.read())!=-1){
						if(tmp2=='完') break;
						str2+=(char)tmp2;
					}	
					tmp2=str2.charAt(0);
					if(tmp2!=3){
						OK=0;
						socket.close();
						return;
					}
					else OK=1;
					t4.append("收到"+tmp2+"号数据包,明文如下\n");
					res=d.decode(str2, key);
					
					if(res==null){
						OK=0;
						socket.close();
						return;
					}
					Kcv=res.get(1);
					System.out.println(str2);
					res=d.decode(str2,key);
					System.out.println("key----:"+key.get(0));
					for(int i=1;i<res.size();++i){
						System.out.println(i+":"+res.get(i));
						t4.append(pack3[i-1]+"\n\t"+res.get(i));
						t4.append("\n");
					}
					t4.append("\n");
					//收到信息保存在str2中
				}
				
				writer.close();
				reader.close();
				socket.close();
				
				socket=new Socket(ipSERVER,3456);
				if(socket!=null)
				{
					CtoV cv=new CtoV();
					cv.setS(res);
					cv.setIDc(IDc);
					cv.ctov();
					
					reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
					writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
					writer.print(d.encode(cv.getnewS(), cv.getnewKey()));
					System.out.println("BBBBBBBBBBBBBBBBBBBBBBBB"+d.encode(cv.getnewS(), cv.getnewKey()).length());
					writer.flush();
					String str="";
					//String tmp="";
					int tmp2;
					int flag=0;
					while((tmp2=reader.read())!=-1){
						if(tmp2=='完') break;
						str+=(char)tmp2;
					}
					System.out.println(str);
					tmp2=str.charAt(0);
					if(tmp2!=5){
						OK=0;
						socket.close();
						return;
					}
					else OK=1;
					t4.append("收到"+tmp2+"号数据包,明文如下\n");
					ArrayList<String> al= d.decode(str, cv.getnewKey());
					for(int i=1;i<al.size();++i)
					{
						System.out.println(al.get(i));
						t4.append(pack5[i-1]+"\n\t"+al.get(i)); 
						
					}
					t4.append("\n");
					
					//收到信息保存在str2中
					//writer.flush();
					
					tmp="";
					tmp+=(char)((1)<<4);
					System.out.println("tmp!!!  "+(int)tmp.charAt(0));
					ArrayList<String> Zsen=new ArrayList<String>();
					ArrayList<String> Zkey=new ArrayList<String>();
					Zkey.add(Kcv);
					Zsen.add(tmp);
					System.out.println("tmp!!!  "+d.encode(Zsen, Zkey));
					writer.print(d.encode(Zsen, Zkey));
					//writer.print(s);
					writer.flush();
					
					if(OK==1) JOptionPane.showMessageDialog( jf, "认证成功","服务", JOptionPane.INFORMATION_MESSAGE);
					else{
						JOptionPane.showMessageDialog( jf,"认证失败","服务",JOptionPane.INFORMATION_MESSAGE);
					}
					//String tmp="";
					flag=0;
					str="";
					while((tmp2=reader.read())>=0){
						if(tmp2=='完') break;
						str+=(char)tmp2;
					}
					//System.out.println(str);
					tt2.append("收到"+(int)str.charAt(0)+"号包,内容为文件列表\n");
					al= d.decode(str, Zkey);
					System.out.println("ALLLLL   "+(int)str.charAt(0));
					for(String s:al){
						if (s.length()<4) continue;
						filelist.addElement(s);
						//System.out.println("ADD   "+s+"  size"+s.length());
					}
				
					while("dsf".equals("dsf")){

						if(UP()!=0){
							System.out.println("这是一次UP操作");
							System.out.println(upfile);
							System.out.println("FXX");
							String[] TMP=upname.split("\\\\");
							String NAME=TMP[TMP.length-1];
							System.out.println("NAME   "+NAME);
							
							FileInputStream fin=null;
							fin = new FileInputStream(new File(upname));
					        byte[] sendByte = null;
					        sendByte = new byte[1024];
					        int length=0;
					        String sendstr="";
					        ArrayList<String> ALFILE = new ArrayList<String>();
					        
					        char a = 3<<4;
							String tema = "";
							tema+=a;
							ALFILE.add(tema);
							ALFILE.add(NAME);
							ArrayList<String> temALFILE = new ArrayList<String>();
							
					        while((length = fin.read(sendByte, 0, sendByte.length))>0){
					        	sendstr = new String(sendByte,"ISO8859-1");
					        	System.out.println(sendstr);
					        	temALFILE = ALFILE;
					        	temALFILE.add(sendstr);
					        	temALFILE.add(Integer.toString(length));
					        	writer.print(d.encode(temALFILE, Zkey));
								//writer.print(s);
								writer.flush();
					        }
					        JOptionPane.showMessageDialog(null, "上传完成!","完成",JOptionPane.INFORMATION_MESSAGE);
					        fin.close();
					        
							tmp="";
							tmp+=(char)((1)<<4);
							System.out.println("tmp!!!  "+(int)tmp.charAt(0));
							Zsen=new ArrayList<String>();
							Zkey=new ArrayList<String>();
							Zkey.add(Kcv);
							Zsen.add(tmp);
							System.out.println("tmp!!!  "+d.encode(Zsen, Zkey));
							writer.print(d.encode(Zsen, Zkey));
							//writer.print(s);
							writer.flush();
					        str="";
							while((tmp2=reader.read())!=-100){
								if(tmp2=='完') break;
								str+=(char)tmp2;
							}
							tt2.append("收到"+(int)str.charAt(0)+"号包,内容为文件列表");
							System.out.println(str);
							//al=new ArrayList<String>();
							al= d.decode(str, Zkey);
							System.out.println("ALLLLL   "+al);
							//list1.clearSelection();
							//filelist.removeAllElements();
							
							int flagsj=0;
							for(String s:al){
								flagsj=1;
								if (s.length()<4) continue;
								//for(String tt:list1)
								{
									for(int j=0; j <filelist.size();++j)
									{
										if(s.equals(filelist.getElementAt(j)))
										{
											flagsj=0;
										}
									}
								}
								if(flagsj == 1)
									filelist.addElement(s);
								System.out.println("ADD   "+s+"  size"+s.length());
							}
							
							UP0();
						}
						if(DO()!=0){
							
							System.out.println("NAME"+downname);
							System.out.println("FILE"+downfile);
							
							al = new ArrayList<String>();
							char a = 5<<4;
							String tema = "";
							tema+=a;
							al.add(tema);
							al.add(downname);
							writer.print(d.encode(al, Zkey));
							writer.flush();
							
							
							char b = 4<<4;
							while(true)
							{
								str="";
								while((tmp2=reader.read())>=0){
									if(tmp2=='完') break;
										str+=(char)tmp2;
								}
								
								
								al = d.decode(str, Zkey);
								tt2.append("收到"+(int)str.charAt(0)+"号包,正在下载"+al.get(1)+"文件");
								
								if(al.get(0).charAt(0)==b){
									JOptionPane.showMessageDialog(null, "下载完成！","完成",JOptionPane.INFORMATION_MESSAGE);
									break;
								}
							
								ctov00110000 cv3 = new ctov00110000(al, downfile);
							}
							System.out.println("FFFFFFFFFFFFFFFF");
							
							DO0();
						}
						
					}
					
					
				}
				
				
				
				writer.close();
				reader.close();
				socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					OK=0;
					e.printStackTrace();
				}
		}
	}
	
	public String getTS(){
		long t=(long) System.currentTimeMillis();
		String k=new String(Long.toString(t));
		k=k.substring(k.length()-8,k.length());
		return k;
	}
	
	public String getIP(){
		String s="";
		String ip="0000";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			s=addr.getHostAddress().toString();//获得本机IP
			String[] tmp=s.split("\\.");
			int tmp2;
			for(int i=0;i<tmp.length;++i){
				tmp2=Integer.parseInt(tmp[i]);
				ip+=(char)tmp2;
			}
			return ip;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	class CtoV{
		public ArrayList<String> getnewKey(){
			return newkey;
		}
		
		public ArrayList<String> getnewS(){
			return newS;
		}
		
		public void setS(ArrayList<String> S)
		{
			this.S=S;
		}
		public void setIDc(String str)
		{
			this.IDc=str;
		}
		
		private String IDc;
		private ArrayList<String> S=new ArrayList<String>();
		
		private ArrayList<String> newkey=new ArrayList<String>();
		private ArrayList<String> newS=new ArrayList<String>();
		
		public ArrayList<String> Key(){
			return newkey;
		}
		
		public ArrayList<String> S(){
			return newS;
		}
		
		public void ctov(){
			
			newkey.add(S.get(1));
			char ch=4;
			String tem="";
			tem+=ch;
			newS.add(tem);  // 0
			newS.add(S.get(4));  //1
			newS.add(IDc);
			newS.add(getIP());
			newS.add(getTS());
		}
	}
	
	public static void main(String args[]){
		ClientUI ui=new ClientUI();
		/***
		CLIENT cl=new CLIENT(1234,ipAS,ipTGS,ipSERVER);
		try {
			cl.SendAndReceive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		****/
	}
	static TextArea t4= new TextArea(20,53);
	static TextArea tt2= new TextArea(19,27);
	static class ClientUI extends JFrame{

		JButton bt1=new JButton("请求认证");	
		JButton bt2=new JButton("进入网盘");
		
		JTextField t1=new JTextField(30);
		JTextField t2=new JTextField(30);
		JTextField t3=new JTextField(30);
		JTextField t5=new JTextField(30);
		JLabel l1=new JLabel("用户名    ");
		JLabel l5=new JLabel("用户KEY  ");
		JLabel l6=new JLabel("                                       ");
		JLabel l2=new JLabel("服务器名");
		JLabel l3=new JLabel("服务器IP ");
		JPanel p1=new JPanel();
		JPanel p2=new JPanel();
		JPanel p3=new JPanel();
		JPanel p4=new JPanel();


		class MyFramePanel extends JFrame{
				
				//TextArea tt1= new TextArea(19,39);
				
				JPanel p1 = new JPanel();
				JPanel p2 = new JPanel();		
				JLabel l1 = new JLabel("上传文件路径");
				
				//Vector<String> filelist = new Vector<String>();
				//DefaultListModel filelist=new DefaultListModel();
				JLabel no1 = new JLabel("                                                                           ");
				JLabel no2 = new JLabel("                                                                           ");
				JLabel no3 = new JLabel("                                                                           ");
				JLabel no4 = new JLabel("                                                                           ");
				JLabel no5 = new JLabel("--------------------------------------------");
				JLabel no6 = new JLabel("--------------------------------------------");
				JLabel no7 = new JLabel("                                                                           ");
				JLabel no8 = new JLabel("                                                                           ");
				JLabel l3 = new JLabel("   下载保存文件路径      ");
				JTextField t1=new JTextField(17);
				JTextField t2=new JTextField(17);
				JLabel l2 = new JLabel("数据包情况");
				JButton b1 = new JButton("上传");
				JButton b2 = new JButton("下载");
				
				MyFramePanel(){
					this.setSize(720,370);
					setResizable(false);  					
					Container container = this.getContentPane();
					GridLayout g = new GridLayout(1,2,10,10);
					container.setLayout(g);
					//this.list1 = new JList(filelist);
					list1.setModel(filelist);
					list1.addListSelectionListener(new ListSelectionListener(){
			        	public void valueChanged(ListSelectionEvent e){
			        		do_user_valueChanged(e);
			        	}
			        });
					
					b1.setBounds(0, 0, 90, 50);
			         ImageIcon icon1 = new ImageIcon("3.png"); 
			         Image temp1 = icon1.getImage().getScaledInstance(b1.getWidth(),  
			                 b1.getHeight(), icon1.getImage().SCALE_DEFAULT);  
			         icon1 = new ImageIcon(temp1);
			        b1.setIcon(icon1);
			        b1.setContentAreaFilled(false);//不绘制按钮区域
					b1.setBorderPainted(false);//不绘制边框
			        
			        b2.setBounds(0, 0, 90, 50);
			         ImageIcon icon2 = new ImageIcon("4.png"); 
			         Image temp2 = icon2.getImage().getScaledInstance(b2.getWidth(),  
			                 b2.getHeight(), icon2.getImage().SCALE_DEFAULT);  
			         icon2 = new ImageIcon(temp2);
			        b2.setIcon(icon2);
			        b2.setContentAreaFilled(false);//不绘制按钮区域
					b2.setBorderPainted(false);//不绘制边框
					
					
					b1.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							
							JFileChooser chooser = new JFileChooser();             //设置选择器  
							 chooser.setMultiSelectionEnabled(true);             //设为多选  
							int returnVal = chooser.showOpenDialog(b1);        //是否打开文件选择框  
							System.out.println("returnVal="+returnVal);  
							  
							if (returnVal == JFileChooser.APPROVE_OPTION) {          //如果符合文件类型  
							  
							String filepath = chooser.getSelectedFile().getAbsolutePath();      //获取绝对路径  
							System.out.println(filepath);  
							  
							  
							System.out.println("You chose to open this file: "+ chooser.getSelectedFile().getName());  //输出相对路径  
							  
							upname=filepath;
							System.out.println("??????????????????????????");
							//upfile=1;
							UP1();
							System.out.println(upfile);
							
							
							}  
							
							
							
						}
					});
					b2.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){	
							downfile=t2.getText();
							//download=1;
							DO1();
						}
					});
					list1.setBorder(BorderFactory.createTitledBorder("文件列表"));
			        list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			        //p1.add(no2);
			        p1.add(no6);
			        //p1.add(l1);
					//p1.add(t1);
					p2.add(l2);
					//p1.add(list1);
					//p1.add(tt1);
					p2.add(tt2);
					p1.add(b1);
					p1.add(no7);
					//p1.add(no1);
					
					//p1.add(no3);
					//p1.add(no4);
					p1.add(no5);
					p1.add(l3);
					p1.add(t2);
					p1.add(b2);
					p1.add(no8);
				
					//tt1.setText("Listening......\n\n");	
					container.add(list1);
					container.add(p1);
					container.add(p2);
					
					this.setTitle("服务页面");
					this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					this.setVisible(true);
				} 	
				protected void do_user_valueChanged(ListSelectionEvent e){
			    	System.out.println(list1.getSelectedIndex());
			    	System.out.println(list1.getSelectedValue());
			    	downname=list1.getSelectedValue().toString();
			    	System.out.println(downname);
			    	//text3.setText(list1.getSelectedValue().toString());
			    }
			}	
		
		ClientUI(){
			
			jf.getContentPane().setBackground(Color.getHSBColor(22, 22, 22));
			jf.setLocation(100, 100);
			jf.setSize(410,520);
			jf.setResizable(false);
			Container container=jf.getContentPane();
			//FlowLayout fleft=new FlowLayout(FlowLayout.CENTER,10,10);
			FlowLayout fright=new FlowLayout(FlowLayout.CENTER,10,10);
			
			BorderLayout border=new BorderLayout(10,10);
			container.setLayout(border);
			//p1.setLayout(fleft);
			t1.setText("idc12345");
			t2.setText("idv12345");
			t3.setText("127.0.0.1");
			p3.add(l1);
			p3.add(t1);
			p3.add(l5);
			p3.add(t5);
			p2.setLayout(fright);
			p2.add(bt1);
			p2.add(l6);
			p2.add(bt2);
			p3.add(l2);
			p3.add(t2);
			p3.add(l3);
			p3.add(t3);
			p3.add(t4);
			p1.setBackground(Color.getHSBColor(22, 22, 22));
			p2.setBackground(Color.getHSBColor(22, 22, 22));
			p3.setBackground(Color.getHSBColor(22, 22, 22));
			//container.add(p1, BorderLayout.NORTH);
			container.add(p3, BorderLayout.CENTER);
			container.add(p2, BorderLayout.SOUTH);
			bt1.setBackground(Color.pink);
			bt2.setBackground(Color.pink);
			//bt1.setContentAreaFilled(false);//不绘制按钮区域
			bt1.setBorderPainted(false);//不绘制边框
			//bt2.setContentAreaFilled(false);//不绘制按钮区域
			bt2.setBorderPainted(false);//不绘制边框
			bt1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){	
					IP=t1.getText();
					//jf.setVisible(false);
					method1();
					
				}
			});
			
			bt2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(method2()!=0)jf.setVisible(false);
				}
			});
			
			jf.setVisible(true);
		}
		
		private String IP;
		
		String getIP()
		{
			return IP;
		}
		
		void method1()
		{
			//IP=t1.getText();
			//ipAS="127.0.0.1";
			//ipTGS="127.0.0.1";
			ipSERVER=t3.getText();
			IDc=t1.getText();
			IDtgs="IDtgs123";
			Kas=t5.getText();
			IDv=t2.getText();
			if(Kas.length()!=8||IDc.length()!=8||IDv.length()!=8){
				JOptionPane.showMessageDialog(this, "不合法输入","警告",JOptionPane.INFORMATION_MESSAGE);
				return;
			} 

			CLIENT CI=new  CLIENT(port,ipAS,ipTGS,ipSERVER);
			CI.Cstar();
			//OK=1;
			return;
		}	
		
		int method2()
		{
			if(OK==0){
				JOptionPane.showMessageDialog(this, "你不是验证用户","警告",JOptionPane.INFORMATION_MESSAGE);
				return 0;
			}
			MyFramePanel frame = new MyFramePanel();
			t1.setText("");
			return 1;
		}	
		
	}
}
