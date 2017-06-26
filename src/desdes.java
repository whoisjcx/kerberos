

/*
 * 		desdes tem=new desdes();
		String Key="11111111";
		String str="strstdsag1rsr";
		String en=tem.Encryptions(str, Key);
		System.out.println(en);
		System.out.println(tem.Dectyptions(en, Key));
		
		.?z???5?3??s?
		strstdsag1rsr
 * */

public class desdes {
	
	/*
	 * ½âÃÜ 
	 * */
	public String Dectyptions(String str, String Key){
			ToBinary t = new ToBinary();
			Feistel f = new Feistel();
			String result = "";
			int m = str.length()/8;
			for(int i = 0; i< m; i++){
				String temstr = t.StringToBinary(str.substring(i*8, (i+1)*8));
				temstr = t.Replacement(temstr);
				temstr = f.Decryption(temstr, Key);
				temstr = t.InverseReplacement(temstr);
				temstr = t.BinaryToString(temstr);
				result += temstr;
			}
			
			String ans="";
			for(int i=0;i<result.length();++i)
			{
				char tech=result.charAt(i);
				if(tech!=0)
				{
					ans+=tech;
				}
				else
				{
					break;
				}
			}
			return ans;
		}
	
	/*
	 * ½âÃÜ*/
	public String Encryptions(String str, String Key) {

		ToBinary t = new ToBinary();
		Feistel f = new Feistel();
		String result = "";
		int Size = str.length();
		int m = Size % 8;
		String Zero = "00000000";
		int TSize = (int) Math.ceil(Size / 8.0);
		for (int i = 0; i < TSize; i++) {
			if (i < TSize - 1) {
				String temstr = t.StringToBinary(str.substring(i * 8, (i + 1) * 8));
				temstr = t.Replacement(temstr);
				temstr = f.Encryption(temstr, Key);
				temstr = t.InverseReplacement(temstr);
				temstr = t.BinaryToString(temstr);
				result += temstr;
			} else {
				String tempa = t.StringToBinary(str.substring(i * 8));
				if (m != 0) {
					for (int n = 0; n < 8 - m; n++) {
						tempa += Zero;
					}
				}
				String temp1 = t.Replacement(tempa);
				String temp2 = f.Encryption(temp1, Key);
				String temp3 = t.InverseReplacement(temp2);
				String temp4 = t.BinaryToString(temp3);
				result += temp4;
			}
		}

		return result;
	}
	
	
	class Feistel {

		 private int [][][] S ={
				 	{{14, 4, 13, 1,  2, 15, 11,  8,  3, 10,  6, 12,  5,  9,  0,  7},  
			        {0, 15,  7,  4, 14,  2, 13,  1, 10,  6, 12, 11,  9,  5,  3,  8},  
			        {4,  1, 14,  8, 13,  6,  2, 11, 15, 12,  9,  7,  3, 10,  5,  0},  
			        {15, 12,  8,  2,  4,  9,  1,  7,  5, 11,  3, 14, 10,  0,  6, 13}},
			        {{15,  1,  8, 14,  6, 11,  3,  4,  9,  7,  2, 13, 12,  0,  5, 10},  
			        {3, 13,  4,  7, 15,  2,  8, 14, 12,  0,  1, 10,  6,  9, 11,  5},  
			        {0, 14,  7, 11, 10,  4, 13,  1,  5,  8, 12,  6,  9,  3,  2, 15},  
			        {13,  8, 10,  1,  3, 15,  4,  2, 11,  6,  7, 12,  0,  5, 14,  9}},     
			        {{10,  0,  9, 14,  6,  3, 15,  5,  1, 13, 12,  7, 11,  4,  2,  8},  
			        {13,  7,  0,  9,  3,  4,  6, 10,  2,  8,  5, 14, 12, 11, 15,  1},  
			        {13,  6,  4,  9,  8, 15,  3,  0, 11,  1,  2, 12,  5, 10, 14,  7},  
			        {1, 10, 13,  0,  6,  9,  8,  7,  4, 15, 14,  3, 11,  5,  2, 12}},   
			        {{7, 13, 14,  3,  0,  6,  9, 10,  1,  2,  8,  5, 11, 12,  4, 15},  
			        {13,  8, 11,  5,  6, 15,  0,  3,  4,  7,  2, 12,  1, 10, 14,  9},  
			        {10,  6,  9,  0, 12, 11,  7, 13, 15,  1,  3, 14,  5,  2,  8,  4},  
			        {3, 15,  0,  6, 10,  1, 13,  8,  9,  4,  5, 11, 12,  7,  2, 14}},   
			        {{2, 12,  4,  1,  7, 10, 11,  6,  8,  5,  3, 15, 13,  0, 14,  9},  
			        {14, 11,  2, 12,  4,  7, 13,  1,  5,  0, 15, 10,  3,  9,  8,  6},  
			        {4,  2,  1, 11, 10, 13,  7,  8, 15,  9, 12,  5,  6,  3,  0, 14},  
			        {11,  8, 12,  7,  1, 14,  2, 13,  6, 15,  0,  9, 10,  4,  5,  3}},  
			        {{12,  1, 10, 15,  9,  2,  6,  8,  0, 13,  3,  4, 14,  7,  5, 11},  
			        {10, 15,  4,  2,  7, 12,  9,  5,  6,  1, 13, 14,  0, 11,  3,  8},  
			        {9, 14, 15,  5,  2,  8, 12,  3,  7,  0,  4, 10,  1, 13, 11,  6},  
			        {4,  3,  2, 12,  9,  5, 15, 10, 11, 14,  1,  7,  6,  0,  8, 13}},    
			        {{4, 11,  2, 14, 15,  0,  8, 13,  3, 12,  9,  7,  5, 10,  6,  1},  
			        {13,  0, 11,  7,  4,  9,  1, 10, 14,  3,  5, 12,  2, 15,  8,  6},  
			        {1,  4, 11, 13, 12,  3,  7, 14, 10, 15,  6,  8,  0,  5,  9,  2},  
			        {6, 11, 13,  8,  1,  4, 10,  7,  9,  5,  0, 15, 14,  2,  3, 12}},  
			        {{13,  2,  8,  4,  6, 15, 11,  1, 10,  9,  3, 14,  5,  0, 12,  7},  
			        {1, 15, 13,  8, 10,  3,  7,  4, 12,  5,  6, 11,  0, 14,  9,  2},  
			        {7, 11,  4,  1,  9, 12, 14,  2,  0,  6, 10, 13, 15,  3,  5,  8},  
			        {2,  1, 14,  7,  4, 10,  8, 13, 15, 12,  9,  0,  3,  5,  6, 11}}
		 };
		
		private int[] Extends = {31, 0, 1, 2, 3, 4,   
	            3,  4, 5, 6, 7, 8,   
	            7,  8,9,10,11,12,   
	            11,12,13,14,15,16,   
	            15,16,17,18,19,20,   
	            19,20,21,22,23,24,   
	            23,24,25,26,27,28,   
	            27,28,29,30,31, 0};
		
		private int[] P = {
				16, 7, 20, 21, 29, 12, 28, 17, 1,  15, 23, 26, 5,  18, 31, 10,  
		        2,  8, 24, 14, 32, 27, 3,  9,  19, 13, 30, 6,  22, 11, 4,  25
		};
		
		/**
		 * Ê¹ÓÃkeys½âÃÜ×Ö·û´®b
		 */
		public String Decryption(String b, String keys){
			int Length = b.length()/2;
			String Left = b.substring(0, Length);
			String Right = b.substring(Length);
			KEY key1 = new KEY();
			String[] s = key1.CreateKey(keys);
			
			for(int i = 15; i >= 0; i--){
				String tempL = ExtendsRight(Left);
				String tempL1 = Xor(tempL,s[i]);
				String tempL2 = S_Box(tempL1);
				String newTempL = P_Box(tempL2);
				String newTempL2 = Xor(newTempL,Right);
				Right = Left;
				Left = newTempL2;
				//System.out.println(Left+Right);
			}
			String temp = Left+Right;
			return temp;
		}
		
		/**
		 * Ê¹ÓÃ×Ö·û´®keys¼ÓÃÜ×Ö·û´®b
		 */
		public String Encryption(String b,String keys){
			int Length = b.length()/2;
			String Left = "";
			String Right = "";
			KEY key1 = new KEY();
			String[] s = key1.CreateKey(keys);
			
			Left = b.substring(0, Length);
			Right = b.substring(Length);
			
			for(int i = 0; i < 16; i++){
				String tempR = ExtendsRight(Right);
				//System.out.println(s[i])
				String tempR1 = Xor(tempR,s[i]);
				String tempR2 = S_Box(tempR1);
				String newTempR = P_Box(tempR2);
				String newTempR2 = Xor(newTempR,Left);
				Left = Right;
				Right = newTempR2;
				//System.out.println(Left+Right);
			}
			String temp = "";
			temp = Left+Right;
			return temp;
		}
		
		/**
		 * ½«32Î»¾­À©Õ¹±íÀ©Õ¹Îª48Î»¶þ½øÖÆ
		 */
		public String ExtendsRight(String Right){
			String b = "";
			for(int i = 0; i< Extends.length; i++){
				b += Right.charAt(Extends[i]);
			}
			return b;
		}
		
		/**
		 * Óëkey½øÐÐÒÖ»ò²Ù×÷
		 */
		public String Xor(String tempR, String Key){
			String s = "";
			for(int i = 0; i < tempR.length(); i++){
				if(tempR.charAt(i) == Key.charAt(i)) s+="0";
				else s+="1";
			}
			return s;
		}
		
		public String Int_B(int a){
			String s1 = Integer.toBinaryString(a);
			String s = "";
			for(int i = 0; i<4-s1.length(); i++){
				s+="0";
			}
			s+=s1;
			return s;
		}
		
		/**
		 * ½«6Î»¶þ½øÖÆÍ¨¹ýSºÐ×ª»»Îª4Î»¶þ½øÖÆ
		 */
		public String Compression(String temp,int Snum){
			int t = 0;
			int m = 0;
			if(temp.charAt(0) == '1') t+=2;
			if(temp.charAt(5) == '1')	t+=1;
			for(int i = 1; i<5; i++){
				m*=2;
				if(temp.charAt(i) == '1') m+=1;  
			}
			String temp4 = Int_B(S[Snum][t][m]);
			return temp4;
		}
		
		/**
		 * SºÐÊµÏÖ
		 */
		public String S_Box(String tempS){
			String[] temp = new String[8];
			String MyR = "";
			int t = 0;
			for(int i = 0; i<8;i++){
				for(int j = 0; j < 6; j++){
					temp[i] += tempS.charAt(t);
					t++;
				}
			}
			t=0;
			for(int i =0; i< 8; i++){
				String temp4 = Compression(temp[i],i);
				MyR += temp4;
			}
			return MyR;
		}
		
		/**
		 * PºÐÊµÏÖ£¬²úÉúÑ©±ÀÐ§Ó¦
		 */
		public String P_Box(String temp){		//PºÐÊµÏÖ
			String MyR = "";
			for(int i = 0; i< 32; i++)
				MyR += temp.charAt(P[i]-1);
			return MyR;
		}
		
	}
	
	class KEY {
		
		private int[] RingLeft = {57, 49, 41, 33, 25, 17, 9,
				1, 58, 50, 42, 34, 26, 18,
				10, 2, 59, 51, 43, 35, 27,
				19, 11, 3, 60, 52, 44, 36}; 
		private int[] RingRight = {63, 55, 47, 39, 31, 33, 15,
				7, 62, 54, 46, 38, 30, 22,
			 	14, 6, 61, 53, 45, 37, 29,
			 	21, 13, 5, 28, 20, 12, 4}; 
		private int[] KEY = {14, 17, 11, 24, 1, 5,
				3, 28, 15, 6, 21, 10,
				23, 19, 12, 4, 26, 8,
				16, 7, 27, 20, 13, 2,
				41, 52, 31, 37, 47, 55,
				30, 40, 51, 45, 33, 48,
				44, 49, 39, 56, 34, 53,
				46, 42, 50, 36, 29, 32};
		private int[] Move = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
		
		/**
		 * ½«ÊäÈëµÄ8Î»×Ö·û×°»»Éú³É16¸ökey
		 */
		public String[] CreateKey(String str){
			String [] s = new String[16];
			String s1 = "";
			int n = str.length()*8;
			boolean[] b = new boolean[n];
			for(int i = 0; i < n; i++){
				b[i] = false;
			}
			char[] v = str.toCharArray();
			for(int i = 0; i<v.length;i++){
				String s2 = Integer.toBinaryString(v[i]);
				for(int t = 0; t < 8-s2.length();t++){
					s1+="0";
				}
				s1+=s2;
			}
			String Left = "";
			String Right = "";
			for(int i = 0; i < RingLeft.length;i++){
				Left += s1.charAt(RingLeft[i]-1);
			}
			for(int i = 0; i < RingRight.length; i++){
				Right += s1.charAt(RingRight[i]-1);
			}
			
			for(int i = 0; i < 16; i++){
				String temp1 = Left.substring(Move[i]);
				String temp2 = Left.substring(0, Move[i]);
				Left = temp1+temp2;
				temp1 = Right.substring(Move[i]);
				temp2 = Right.substring(0,Move[i]);
				Right = temp1+temp2;
				String temp = Left+Right;
				String temps="";
				for(int t = 0; t < KEY.length; t++){
					temps+=temp.charAt(KEY[t]-1);
				}
				s[i] = temps;
			}
			return s;
		}

	}
	class ToBinary {
		
		private int[] IP = {58,50,42,34,26,18,10,2,60,52,44,36,28,20,12,4,
				62,54,46,38,30,22,14,6,64,56,48,40,32,24,16,8,
				57,49,41,33,25,17, 9,1,59,51,43,35,27,19,11,3,
				61,53,45,37,29,21,13,5,63,55,47,39,31,23,15,7}; 
		private int[] IP_1 = {40,8,48,16,56,24,64,32,39,7,47,15,55,23,63,31,
				38,6,46,14,54,22,62,30,37,5,45,13,53,21,61,29,
				36,4,44,12,52,20,60,28,35,3,43,11,51,19,59,27,
				34,2,42,10,50,18,58,26,33,1,41, 9,49,17,57,25};
		
		/**
		 * ³õÊ¼ÖÃ»»º¯Êý
		 */
		public String Replacement(String b){	
			String b1 = "";
			
			for(int i = 0; i<b.length(); i++){
				b1 += b.charAt(IP[i]-1);
			}
			
			return b1;
		}
		
		/**
		 *ÄæÖÃ»»º¯Êý 
		 */
		public String InverseReplacement(String b){	
			String b1 = "";
			
			for(int i = 0; i<b.length(); i++){
				b1 += b.charAt(IP_1[i]-1);
			}
			
			return b1;
		}
		
		/**
		 * ×Ö·û´®×ª¶þ½øÖÆº¯Êý
		 */
		public String StringToBinary(String str){
			String s = "";
			for(int i = 0; i<str.length();i++){
				char e = str.charAt(i);
				int m = e;
				String s1 = Integer.toBinaryString(m);
				for(int j = 0; j < 8-s1.length(); j++)
				{
					s+="0";
				}
				s+=s1;
			}
			return s;
		}
		
		/**
		 * 64¶þ½øÖÆ×ª8Î»×Ö·û
		 */
		public String BinaryToString(String b){
			String s = "";
			char e;
			for(int i = 0; i<8;i++){
				int a = 0;
				for(int j = 0; j < 8; j++){
					a*=2;
					if(b.charAt(i*8+j) == '1') a+=1;
				}
				e = (char) a;
				s+=e;
			}
			return s;
		}
	}
}
