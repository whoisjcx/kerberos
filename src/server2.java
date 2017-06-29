
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class server2 {
    // TODO hello 
}

class vtoc00100000{
	private ArrayList<String> deco = new ArrayList<String>();
	private ArrayList<String> totalfile = new ArrayList<String>();
	
	private String willsend = ""; 
	private ArrayList<String> Key = new ArrayList<String>();
	private ArrayList<String> newS = new ArrayList<String>();
	
	public String getwillsend()
	{
		return willsend;
	}
	
	vtoc00100000(ArrayList<String> decode,ArrayList<String> filename,ArrayList<String> key)
	{
		this.deco=decode;
		this.totalfile=filename;
		this.Key=key;
		
		char a = 2<<4;
		String tema = "";
		tema+=a;
		//newS = new ArrayList<String>();
		newS.add(tema);
		newS.addAll(filename);

		//newS = totalfile; 
		data d = new data();
		willsend = d.encode(newS, Key);
		System.out.println(willsend);
		
		deco.clear();
		totalfile.clear();
	}
}

class vtoc00110000{
	
	
	private String willsend = ""; 
	@SuppressWarnings("unused")
	private ArrayList<String> S = new ArrayList<String>();
	private ArrayList<String> Key = new ArrayList<String>();
	private data d = new data();
	
	String filename = "";
	
	public String getfilename()
	{
		return filename;
	}
	
	public String getwillsend()
	{
		return willsend;
	}
	
	public vtoc00110000(ArrayList<String> Key, ArrayList<String> S)
	{
		this.Key = Key;
		this.S = S;
		filename = S.get(1);
	}
	public String vtoc(String str, int length)
	{
		ArrayList<String> newS = new ArrayList<String>();
		
		char a = 3<<4;
		String tema = "";
		tema+=a;
		newS.add(tema);
		newS.add(filename);
		newS.add(str);
		newS.add(Integer.toString(length));
		
		willsend = d.encode(newS, Key);
		
		return willsend;
	}

}

class ctov00110000{
	
	@SuppressWarnings("unused")
	private ArrayList<String> S = new ArrayList<String>();
	
	String Path = "";
	
	public ctov00110000(ArrayList<String> S, String path) throws IOException{
		this.S = S;
		Path = path + "\\" + S.get(1);
		
		FileOutputStream fout = null;
        fout = new FileOutputStream(new File(Path),true);
        
        byte[] sendByte = null;
        sendByte = new byte[1024*10];
       
		sendByte =  S.get(2).getBytes("ISO8859-1");
		
		fout.write(sendByte,0,Integer.parseInt(S.get(3)));

    	fout.close();
	}
}