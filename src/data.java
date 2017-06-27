import java.math.*;
import java.util.*;
import java.util.Map.Entry;

public class data {
	desdes des=new desdes();
	String encode(ArrayList<String> S,ArrayList<String> Key){
		String ret="";
		char t=S.get(0).charAt(0);
		t<<=4;t>>=4;
		switch (t){
			case 0:{
				for(String it:S) ret+=it;
			};break;
			case 1:{
				ret+=S.get(0);
				String tmp1="",tmp2="";
				for(int i=1;i<5;i++)		tmp1+=S.get(i);
				for(int i=5;i<S.size();i++)	tmp2+=S.get(i);
				String en=des.Encryptions(tmp2, Key.get(0));
				tmp1+=en;
				ret+=des.Encryptions(tmp1, Key.get(1));
			};break;
			case 2:{
				ret+=S.get(0);
				ret+=S.get(1);
				ret+=S.get(2);
				String tmp1="";
				tmp1+=S.get(3);
				tmp1+=S.get(4);
				tmp1+=S.get(5);
				tmp1=des.Encryptions(tmp1, Key.get(0));
				ret+=tmp1;
			};break;
			case 3:{
				ret+=S.get(0);
				String tmp1="",tmp2="";
				for(int i=1;i<4;i++)		tmp1+=S.get(i);
				for(int i=4;i<S.size();i++)	tmp2+=S.get(i);
				String en=des.Encryptions(tmp2, Key.get(0));
				tmp1+=en;
				ret+=des.Encryptions(tmp1, Key.get(1));
			};break;
			case 4:{
				
			};break;
			case 5:{
				
			};break;
			case 6:{
				
			};break;
			case 7:{
				
			};break;
		}
		return ret+"0000";
	}
	
	ArrayList<String> decode(String S,ArrayList<String> Key){
		ArrayList<String> ret=new ArrayList<String>();
		char B=S.charAt(0);
		switch (B){
			case 0:{
				ret=new ArrayList<String>();
				String t="";
				t+=S.charAt(0);
				ret.add(t);
				ret.add(S.substring(1,9));
				ret.add(S.substring(9,17));
				ret.add(S.substring(17,25));
			};break;
			case 1:{
				ret=new ArrayList<String>();
				String t="",tmp1="";
				t+=S.charAt(0);
				ret.add(t);
				tmp1=S.substring(1);
				tmp1=des.Dectyptions(tmp1, Key.get(0));
				ret.add(tmp1.substring(0,8));
				ret.add(tmp1.substring(8,16));
				ret.add(tmp1.substring(16,24));
				ret.add(tmp1.substring(24,32));
				ret.add(tmp1.substring(32));
			};break;
			case 2:{
				ret=new ArrayList<String>();
				String t="",tmp1="";
				t+=S.charAt(0);
				ret.add(t);
				tmp1=S.substring(1,9);
				ret.add(tmp1);
				tmp1=S.substring(9,57);
				tmp1=des.Dectyptions(tmp1, Key.get(0));
				ret.add(tmp1.substring(0,8));
				ret.add(tmp1.substring(8,16));
				ret.add(tmp1.substring(16,24));
				ret.add(tmp1.substring(24,32));
				ret.add(tmp1.substring(32,40));
				ret.add(tmp1.substring(40,48));
				
				tmp1=S.substring(57);
				tmp1=des.Dectyptions(tmp1,ret.get(2));
				
				ret.add(tmp1.substring(0,8));
				ret.add(tmp1.substring(8,16));
				ret.add(tmp1.substring(16,24));
				
			};break;
			case 3:{
				ret=new ArrayList<String>();
				String t="",tmp1="";
				t+=S.charAt(0);
				ret.add(t);
				tmp1=S.substring(1);
				tmp1=des.Dectyptions(tmp1, Key.get(0));
				ret.add(tmp1.substring(0,8));
				ret.add(tmp1.substring(8,16));
				ret.add(tmp1.substring(16,24));
				ret.add(tmp1.substring(24));
			};break;
			case 4:{ 
				
			};break;
			case 5:{
				
			};break;
			case 6:{
				
			};break;
			case 7:{
				
			};break;
		}
		return ret;
	}
	public static void main(String[] args){
		int x=19;
		String s=Integer.toString(x);
		s="0000"+s;
		System.out.println(new Integer(s));
    }
	
}
