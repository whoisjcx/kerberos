import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SERVER {
	
	MyList demo = new MyList();
	
	private int port=3456;	//监听端口
	
	class SendThread extends Thread{
		private Socket socket=null;  
		private BufferedReader reader;
		private PrintWriter writer;
		private String Ktgsv="12345678";	
		
		private ArrayList<String> key=new ArrayList<String>();	 
		data d=new data();
		
		public SendThread(Socket socket){
			this.socket=socket;
		}
		protected void finalize(){
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@SuppressWarnings({ "resource", "unused" })
		@Override
		public void run(){
			//String ip=socket.getInetAddress().getHostAddress();
			VtoC vc=new VtoC();
			System.out.println("Connected");
			try {
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
				String str="";
				int tmp2;
				while((tmp2=reader.read())!=-1){
					if(tmp2=='完') break;
					str+=(char)tmp2;
				}
				while(reader.ready()){
					tmp2=reader.read();
				}
				key.add(Ktgsv);
				vc.setS(d.decode(str, key));
				
				String text3="收到" + (int)vc.getS().get(0).charAt(0)+"号包," + vc.getS().get(2) + "登录\n";

				demo.beginadd(vc.getS().get(2), text3);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

			vc.vtoc();
			String willsend=d.encode(vc.getnewS(),vc.getnewKey());
			writer.print(willsend);
			writer.flush();
			while("dsf".equals("dsf")){
				String str="";
				int tmp2;

				ArrayList<String> temAL = new ArrayList<String>();
				try {
					str="";
					while((tmp2=reader.read())!=-100){
						if(tmp2=='完') break;
						str+=(char)tmp2;
					}

					temAL=d.decode(str, vc.getnewKey());
					
					demo.addmessage(vc.getS().get(2), "收到" + (int)temAL.get(0).charAt(0) + "号包,内容为:");
					
					System.out.println(vc.getS().get(2));
					
					//System.out.println(temAL);
					switch(temAL.get(0).charAt(0)){
						case ((1)<<4):{
							demo.addmessage(vc.getS().get(2), "请求文件列表\n");
							vtoc00100000 v = new vtoc00100000(temAL,demo.getfilename(),vc.getnewKey());
							System.out.println("SEND"+v.getwillsend());
							writer.print(v.getwillsend());
							writer.flush();
						
						};break;
						case ((5)<<4):{
							demo.addmessage(vc.getS().get(2), "请求下载文件"+ temAL.get(1) +"\n");
							vtoc00110000 v = new vtoc00110000(vc.getnewKey(), temAL);
							String filename = demo.getPath() + "\\" + v.getfilename();
							
							FileInputStream fin=null;
							fin = new FileInputStream(new File(filename));
					        byte[] sendByte = null;
					        sendByte = new byte[1024*5];
					        int length = 0;
					        String sendstr = "";
					        while((length = fin.read(sendByte, 0, sendByte.length))>0){
					        	sendstr=new String(sendByte,"ISO8859-1");
					        	writer.print(v.vtoc(sendstr,length));
					        	writer.flush();
					        }
					        
					        
					        ArrayList<String> ALtem = new ArrayList<String>();
							char a = 4<<4;
							String tema = "";
							tema += a;
							ALtem.add(tema);
							
					        writer.print(d.encode(ALtem, vc.getnewKey()));
					        writer.flush();
							
						};break;
						case ((3)<<4):{
							demo.addmessage(vc.getS().get(2), "收到上传文件"+ temAL.get(1) +"\n");
							System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
							ctov00110000 c = new ctov00110000(temAL, demo.getPath());
							
							demo.refresh();
						};break;
					}
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					
					e1.printStackTrace();
					demo.enddelete(vc.getS().get(2));
					return;
				}
			}
			

			
			
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
			System.out.println("Server Listening");
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
	
	public void Serverstart()
	{
        demo.setPath("C:\\Users\\75608_000\\Desktop\\test");
        demo.refresh();
		new ListenThread().start();
	}
	
	public static void main(String args[]){
		SERVER se=new SERVER();
		se.Serverstart();
	}
}

class VtoC{
	
	public void setS(ArrayList<String> S)
	{
		this.S=S;
	}
	public ArrayList<String> getS()
	{
		return S;
	}
	private ArrayList<String> S=new ArrayList<String>();
	
	private ArrayList<String> newkey=new ArrayList<String>();
	private ArrayList<String> newS=new ArrayList<String>();
	
	public void vtoc()
	{
		for(int i=0;i<S.size();++i)
		{
			System.out.println(S.get(i));
		}
		
		newkey.add(S.get(1));
		char ch=5;
		String tem="";
		tem+=ch;
		newS.add(tem);
		long ll=Long.parseLong(S.get(9));
		ll+=1;
		tem=new String(Long.toString(ll));
		newS.add(tem);
	}
	
	public ArrayList<String> getnewKey(){
		return newkey;
	}
	
	public ArrayList<String> getnewS(){
		return newS;
	}
	
}

class MyList {
	private String Path;
	void setPath(String str)
	{
		this.Path=str;
	}
	
	public String getPath()
	{
		return Path;
	}
	
    private JFrame frame = new JFrame("Server");
    private Container container = frame.getContentPane();
    @SuppressWarnings("rawtypes")
	private JList list1 = new JList();// 定义列表框
    
    TextArea text2=new TextArea(19,25);
    TextArea text3=new TextArea(19,25);
    
    void settext2(String str)
    {
    	text2.setText(str);
    }
    void settext3(String str)
    {
    	text3.setText(str);
    }
    
	JLabel L2 = new JLabel("文件列表");
	JLabel L3 = new JLabel("事件信息");
	
	JPanel jp2 = new JPanel();
	JPanel jp3 = new JPanel();	
    
	@SuppressWarnings("rawtypes")
	DefaultListModel userlist=new DefaultListModel();
	//DefaultListModel message=new DefaultListModel();
	//Vector<String> userlist = new Vector<String>();
	Vector<String> filelist = new Vector<String>();
	//Vector<String> message = new Vector<String>();
	
	@SuppressWarnings("unchecked")
	public void beginadd(String user,String messa){
		userlist.addElement(user);
		//message.add(messa);
		text3.append(messa+"\n");
	}
	@SuppressWarnings("rawtypes")
	public DefaultListModel getuserlist()
	{
		return userlist;
	}
	public String getmessage()
	{
		return text3.getText();
	}
	
	public void enddelete(String str)
	{
		list1.clearSelection();
		for(int i=0;i<userlist.size();++i)
		{
			if(userlist.get(i).equals(str))
			{
				//message.set(i, message.get(i)+"\n结束！！");
				//list1.remove(i);
				//message.remove(i);
				text3.append(str + "离开！\n");
				userlist.remove(i);
				list1.repaint();
			}
		}
	}
	
	public void addmessage(String str1, String str2)
	{
		for(int i=0;i<userlist.size();++i)
		{
			if(userlist.get(i).equals(str1))
			{
				//message.set(i, message.get(i)+"\n"+str2);
				text3.append(str2);
			}
		}
	}
	
	FileName fn=new FileName();
	
	public ArrayList<String> getfilename(){
		return fn.getfilename(Path);
	}
	
	public void totext2(String path)
	{
		String te2="";
		ArrayList<String> al = fn.getfilename(path);
		for(int i=0;i<al.size();++i)
		{
			te2+=al.get(i);
			te2+="\n";
		}
		text2.setText(te2);
	}
	
	public void refresh()
	{
		totext2(Path);
	}
 
    @SuppressWarnings("unchecked")
	public MyList() {
    	
        this.frame.setLayout(new GridLayout(1, 2));   
        this.list1.setModel(userlist);
        //this.list1 = new JList(userlist);
        list1.addListSelectionListener(new ListSelectionListener(){
        	public void valueChanged(ListSelectionEvent e){
        		do_user_valueChanged(e);
        	}
        });
        
		GridLayout g = new GridLayout(1,3,10,10);
		container.setLayout(g);
        
        list1.setBorder(BorderFactory.createTitledBorder("用户名"));
        list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        jp2.add(L2);
        jp2.add(text2);
        jp3.add(L3);
        jp3.add(text3);
        
        container.add(this.list1);
        container.add(this.jp2);
        container.add(this.jp3);
        this.frame.setSize(630, 380);
        this.frame.setVisible(true);
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
                System.exit(1);
            }
        });
    }
    
    protected void do_user_valueChanged(ListSelectionEvent e){
    	System.out.println(list1.getSelectedIndex());
    }
}

class FileName{
	
	public ArrayList<String> getfilename(String path){
		ArrayList<String> al=new ArrayList<String>();
		  File f = new File(path);
	        if (!f.exists()) {
	            System.out.println(path + " not exists");
	        }

	        File fa[] = f.listFiles();
	        for (int i = 0; i < fa.length; i++) {
	            File fs = fa[i];
	            al.add(fs.getName());
	        }
	        return al;
	}
}


