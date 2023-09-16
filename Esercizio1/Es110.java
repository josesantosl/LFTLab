public class Es110 {
    /*
      STATI:

      |stato| / | * | a |
      |-----------------|
      |-> *0| 1 | 0 | 0 |
      |   *1| 0 | 2 | 0 |
      |    2| 2 | 3 | 2 |
      |    3| 0 | 3 | 2 |
      |-----------------|
     */
    public static boolean scan(String s){
		int state = 0;
		int i = 0;
		while (state >=0 && i < s.length()) {
			final char ch = s.charAt(i++);
			final int numchar = Character.getNumericValue(ch); //numericValue Char
			switch (state) {
			case 0:
				if(ch=='/'){
					state=1;
				}else if(ch=='*'){
					state=0;
				}else if(ch=='a'){
					state=0;
				}else{
					state=-1;
				}
				break;
			case 1:
				if(ch=='/'){
					state=0;
				}else if(ch=='*'){
					state=2;
				}else if(ch=='a'){
					state=0;
				}else{
					state=-1;
				}
				break;
			case 2:
				if(ch=='/'){
					state=2;
				}else if(ch=='*'){
					state=3;
				}else if(ch=='a'){
					state=2;
				}else{
					state=-1;
				}
				break;
			case 3:
				if(ch=='/'){
					state=0;
				}else if(ch=='*'){
					state=3;
				}else if(ch=='a'){
					state=2;
				}else{
					state=-1;
				}
				break;
			}

		}
		return state==0 || state==1;
    }
    public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK":"NOPE");
    }
}
