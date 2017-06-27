import java.util.*; 
public class hello {
	public static void main(String args[]){
		long e=5151;
		ArrayList<Integer> A=new ArrayList<Integer>();
		A.add(0);
		A.set(0, 9);
		System.out.println(A.get(0));
		char c=(char)254;
		System.out.println((int)c);
		for(int i=0;i<256;i++){
			System.out.println(((char)i)==-1);
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
