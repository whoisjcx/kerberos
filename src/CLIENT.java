import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CLIENT {

	int port;
	String ipAS;
	String ipTGS;
	String ipSERVER;
	String IDc="IDc12345";
	String IDtgs="IDtgs123";
	String IDv="IDv12345";
	ArrayList<String> key=new ArrayList<String>();
	
	public CLIENT(int port,String ipAS,String ipTGS,String ipSERVER){
		this.port=port;
		this.ipAS=ipAS;
		this.ipTGS=ipTGS;
		this.ipSERVER=ipSERVER;
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
		CLIENT cl=new CLIENT(1234,"127.0.0.1","127.0.0.1","127.0.0.1");
		try {
			cl.SendAndReceive();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static class ClientUI extends JFrame{

		JButton bt1=new JButton("请求服务");	
		JButton bt2=new JButton("取消");
		
		JTextField t1=new JTextField(15);
		JTextField t2=new JTextField(15);
		JLabel l1=new JLabel("用户名");
		JLabel l2=new JLabel("要访问服务器名");
		JPanel p1=new JPanel();
		JPanel p2=new JPanel();
		JPanel p3=new JPanel();
		//JPanel p4=new JPanel();
		
		ClientUI(){
			
			JFrame jf = new JFrame("Client");
			jf.setSize(300,150);
			Container container=jf.getContentPane();
			FlowLayout fleft=new FlowLayout(FlowLayout.CENTER,10,10);
			FlowLayout fright=new FlowLayout(FlowLayout.CENTER,10,10);
			
			BorderLayout border=new BorderLayout(10,10);
			container.setLayout(border);
			p1.setLayout(fleft);
			p1.add(l1);
			p1.add(t1);
			
			p2.setLayout(fright);
			p2.add(bt1);
			p2.add(bt2);
			p3.add(l2);
			p3.add(t2);
			
			container.add(p1, BorderLayout.NORTH);
			container.add(p3, BorderLayout.CENTER);
			container.add(p2, BorderLayout.SOUTH);
			
			bt1.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){	
					IP=t1.getText();
					jf.setVisible(false);
					method1();
		
				}
			});
			
			bt2.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					method2();
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
			IP=t1.getText();
			JOptionPane.showMessageDialog(this, "选择服务","服务",JOptionPane.INFORMATION_MESSAGE);
			
		}	
		
		void method2()
		{
			t1.setText("");
		}	
		
	}
}
