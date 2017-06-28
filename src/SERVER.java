import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
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
		private String Ktgsv="00000000";	
		
		private ArrayList<String> key=new ArrayList<String>();	// 要从数据库读  
		data d=new data();
		
		public SendThread(Socket socket){
			this.socket=socket;
		}
		
		@Override
		public void run(){
			//String ip=socket.getInetAddress().getHostAddress();
			VtoC vc=new VtoC();
			System.out.println("Connected");
			try {
				reader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				writer=new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"),true);
				String str="";
				String tmp="";
				int tmp2;
				int flag=0;
				while((tmp2=reader.read())!=-1){
					if(tmp2=='完') break;
					str+=(char)tmp2;
				}
				key.add(Ktgsv);
				
				String text3="";
				for(int i=0;i<vc.getnewS().size();++i)
				{
					text3+=vc.getnewS().get(i);
					text3+="\n";
				}
				
				demo.beginadd(vc.getnewS().get(2), text3);
				
				vc.setS(d.decode(str, key));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			vc.vtoc();
			String willsend=d.encode(vc.getnewS(),vc.getnewKey());
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
        //demo.setPath("C:\\Users\\chenlvhao\\Desktop\\send");
        //demo.refresh();
		new ListenThread().start();
	}
	
	public static void main(String args[]){
		String s="";
		SERVER se=new SERVER();
		se.Serverstart();
	}
}

class VtoC{
	
	public void setS(ArrayList<String> S)
	{
		this.S=S;
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
	
    private JFrame frame = new JFrame("Server");
    private Container container = frame.getContentPane();
    private JList list1 = null;// 定义列表框
    
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
    
	Vector<String> userlist = new Vector<String>();
	Vector<String> filelist = new Vector<String>();
	Vector<String> message = new Vector<String>();
	
	public void beginadd(String user,String messa){
		userlist.add(user);
		message.add(messa);
	}
	
	public void enddelete(String str)
	{
		for(int i=0;i<userlist.size();++i)
		{
			if(userlist.get(i).equals(str))
			{
				userlist.remove(i);
				message.remove(i);
			}
		}
	}
	
	FileName fn=new FileName();
	
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
 
    public MyList() {
    	
        this.frame.setLayout(new GridLayout(1, 2));   
        this.list1 = new JList(userlist);
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
    	text3.setText(message.get(list1.getSelectedIndex()));
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


