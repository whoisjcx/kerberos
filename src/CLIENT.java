import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
	static String ipAS="127.0.0.1";
	static String ipTGS="127.0.0.1";
	static String ipSERVER="127.0.0.1";
	static String IDc="IDc12345";
	static String IDtgs="IDtgs123";
	static String IDv="IDv12345";
	ArrayList<String> key=new ArrayList<String>();
	
	public CLIENT(int cport,String cipAS,String cipTGS,String cipSERVER){
		port=cport;
		ipAS=cipAS;
		ipTGS=cipTGS;
		ipSERVER=cipSERVER;
		key.add("00000000");
	}
	
	
	void SendAndReceive() throws UnknownHostException, IOException
	{
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
			willsend=d.encode(res, key);
			System.out.println(willsend);
			writer.println(willsend);
			writer.flush();
			String str1="";
			String temstr="";
			int tmp2;
			int flag=0;
			while((tmp2=reader.read())!=-1){
				if(tmp2=='完') break;
				str1+=(char)tmp2;
			}	//str为从client接收的数据
			res=d.decode(str1, key);
			

				System.out.println("size----:"+str1.length());
				System.out.println("str1----:"+str1);
				for(int i=0;i<str1.length();++i){
					System.out.print((int)str1.charAt(i)+"-");
				}
				System.out.println("");
				for(int i=0;i<key.size();++i){
					System.out.println("key----:"+key.get(i));
				}

			for(int i=0;i<res.size();++i){
					System.out.println(i+":"+res.get(i));
				}
			System.out.println("ticket size:"+res.get(5).length());
		}
		writer.close();
		reader.close();
		socket.close();
		
		socket=new Socket(ipTGS,2345);
		if(socket!=null)
		{
			ArrayList<String> a=new ArrayList();
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
			writer.println(willsend);
			writer.flush();
			String str2="";
			int tmp2;
			int flag=0;
			while((tmp2=reader.read())!=-1){
				if(tmp2=='完') break;
				str2+=(char)tmp2;
			}	
			System.out.println(str2);
			res=d.decode(str2,key);
			System.out.println("key----:"+key.get(0));
			for(int i=0;i<res.size();++i){
				System.out.println(i+":"+res.get(i));
			}
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
			writer.println(d.encode(cv.getnewS(), cv.getnewKey()));
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
			ArrayList<String> al= d.decode(str, cv.getnewKey());
			for(int i=0;i<al.size();++i)
			{
				System.out.println(al.get(i));
			}
			
			//收到信息保存在str2中
			writer.flush();
		}
		
		
		
		writer.close();
		reader.close();
		socket.close();
		
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
	
	static class ClientUI extends JFrame{

		JButton bt1=new JButton("请求认证");	
		JButton bt2=new JButton("进入服务");
		
		JTextField t1=new JTextField(20);
		JTextField t2=new JTextField(20);
		JTextField t3=new JTextField(20);
		JLabel l1=new JLabel("用户名    ");
		JLabel l2=new JLabel("服务器名");
		JLabel l3=new JLabel("服务器IP ");
		JPanel p1=new JPanel();
		JPanel p2=new JPanel();
		JPanel p3=new JPanel();
		//JPanel p4=new JPanel();
		TextArea t4= new TextArea(19,39);
		;

		class MyFramePanel extends JFrame{
				JList list1 = null;// 定义列表框
				Vector<String> filelist = new Vector<String>();
				//TextArea tt1= new TextArea(19,39);
				TextArea tt2= new TextArea(19,39);
				JPanel p1 = new JPanel();
				JPanel p2 = new JPanel();		
				JLabel l1 = new JLabel("上传文件路径");
				
				JLabel no1 = new JLabel("                                                                           ");
				JLabel no2 = new JLabel("                                                                           ");
				JLabel no3 = new JLabel("                                                                           ");
				JLabel no4 = new JLabel("                                                                           ");
				JLabel no5 = new JLabel("--------------------------------------------");
				JLabel no6 = new JLabel("--------------------------------------------");
				JLabel no7 = new JLabel("                                                                           ");
				JLabel no8 = new JLabel("                                                                           ");
				JLabel l3 = new JLabel("   下载保存文件路径      ");
				JTextField t1=new JTextField(20);
				JTextField t2=new JTextField(20);
				JLabel l2 = new JLabel("数据包情况");
				JButton b1 = new JButton("上传");
				JButton b2 = new JButton("下载");
				MyFramePanel(){
					this.setSize(600,350);
					setResizable(false);  					
					Container container = this.getContentPane();
					GridLayout g = new GridLayout(1,2,10,10);
					container.setLayout(g);
					this.list1 = new JList(filelist);
					list1.addListSelectionListener(new ListSelectionListener(){
			        	public void valueChanged(ListSelectionEvent e){
			        		do_user_valueChanged(e);
			        	}
			        });
					list1.setBorder(BorderFactory.createTitledBorder("文件列表"));
			        list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			        p1.add(no2);
			        p1.add(no6);
			        p1.add(l1);
					p1.add(t1);
					p2.add(l2);
					//p1.add(list1);
					//p1.add(tt1);
					p2.add(tt2);
					p1.add(b1);
					p1.add(no7);
					//p1.add(no1);
					
					//p1.add(no3);
					p1.add(no4);
					p1.add(no5);
					p1.add(l3);
					p1.add(t2);
					p1.add(b2);
					p1.add(no8);
				
					//tt1.setText("Listening......\n\n");	
					container.add(this.list1);
					container.add(p1);
					container.add(p2);
					
					this.setTitle("服务页面");
					this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					this.setVisible(true);
				} 	
				protected void do_user_valueChanged(ListSelectionEvent e){
			    	System.out.println(list1.getSelectedIndex());
			    	//text3.setText(list1.getSelectedValue().toString());
			    }
			}	
		
		ClientUI(){
			
			JFrame jf = new JFrame("Client");
			jf.setSize(310,490);
			jf.setResizable(false);
			Container container=jf.getContentPane();
			//FlowLayout fleft=new FlowLayout(FlowLayout.CENTER,10,10);
			FlowLayout fright=new FlowLayout(FlowLayout.CENTER,10,10);
			
			BorderLayout border=new BorderLayout(10,10);
			container.setLayout(border);
			//p1.setLayout(fleft);
			p3.add(l1);
			p3.add(t1);
			
			p2.setLayout(fright);
			p2.add(bt1);
			p2.add(bt2);
			p3.add(l2);
			p3.add(t2);
			p3.add(l3);
			p3.add(t3);
			p3.add(t4);
			//container.add(p1, BorderLayout.NORTH);
			container.add(p3, BorderLayout.CENTER);
			container.add(p2, BorderLayout.SOUTH);
			
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
			ipAS="127.0.0.1";
			ipTGS="127.0.0.1";
			ipSERVER=t3.getText();
			IDc=t1.getText();
			IDtgs="IDtgs123";
			IDv=t2.getText();
			if(IDc.length()!=8||IDv.length()!=8){
				JOptionPane.showMessageDialog(this, "请输入合法ID","警告",JOptionPane.INFORMATION_MESSAGE);
				return;
			} 

			
			CLIENT cl=new CLIENT(1234,ipAS,ipTGS,ipSERVER);
			try {
				cl.SendAndReceive();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OK=1;
			JOptionPane.showMessageDialog(this, "认证成功","服务",JOptionPane.INFORMATION_MESSAGE);
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
