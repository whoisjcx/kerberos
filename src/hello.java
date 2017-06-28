import java.util.*; 
public class hello {
	public static void main(String args[]){
		String S="123\\2345\\56";
		String[] A=S.split("\\\\");
		for(String s:A){
			System.out.println(s);
		}
		/***
		System.out.println(Long.toString(e).substring(1));
		desdes tem=new desdes();
		String Key="11111111";
		String str="strstdsag1rsr";
		String en=tem.Encryptions(str, Key);
		System.out.println(en.length());
		System.out.println(tem.Dectyptions(en, Key));
		***/
	}
}
