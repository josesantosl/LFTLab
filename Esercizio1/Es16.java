public class Es16 {
    /*
      |stato| a | b |
      |  0  | 1 | 0 |
      |  1  | 1 | 2 |
      |  2  | 1 | 3 |
      |  3  | 1 | 4 |
      |  4  | 1 | 4 |
     */
    public static boolean scanner(String s){
	int state = 0;
	int i = 0;
	while (state >=0 && i < s.length()) {
	    final char ch = s.charAt(i++);
	    final int numchar = Character.getNumericValue(ch); //numericValue Char
	    switch (state) {

	    case 0:
		if(ch == 'a')
		    state=1;
		else if( ch=='b')
		    state=0;
		else
		    state=-1;
		break;
	    case 1:
		if(ch == 'a')
		    state=1;
		else if( ch=='b')
		    state=2;
		else
		    state=-1;
		break;

	    case 2:
		if(ch == 'a')
		    state=1;
		else if( ch=='b')
		    state=3;
		else
		    state=-1;
		break;

	    case 3:
		if(ch == 'a')
		    state=1;
		else if( ch=='b')
		    state=4;
		else
		    state=-1;
		break;

	    case 4:
		if(ch == 'a')
		    state=1;
		else if( ch=='b')
		    state=4;
		else
		    state=-1;
		break;

	    }
	}
	return state>0 && state<4;
	
    }
    public static void main(String[] args) {
	System.out.println(scanner(args[0]) ? "OK":"NOPE");
    }
}
