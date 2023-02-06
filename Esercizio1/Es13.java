public class Es13 {
    /*
     CONDIZIONI
     - dalla A alla K e matricola pari
     - dalla L alla Z e matricola dispari

     STATI:
     -  0: stato iniziale
     -  1: stato dispari
     -  2: stato pari
     -  3: stato aprovato
     - -1: stato negato o errore

     |STATO|pari|dispari|A-K|L-Z|
     |--------------------------|
     |  0  |  2 |   1   |-1 |-1 |
     |  1  |  2 |   1   |-1 | 3 |
     |  2  |  2 |   1   | 3 |-1 |
     |  3  | -1 |  -1   | 3 | 3 |
     |--------------------------|
     */
    public static boolean scan(String s){
	int state = 0;
	int i = 0;
	while (state >=0 && i < s.length()) {
	    final char ch = s.charAt(i++);
	    final int numchar = Character.getNumericValue(ch); //numericValue Char
	    switch (state) {


	    case 0:
		//stato iniziale
		if(Character.isLetter(ch))
		    state = -1;
		else if( numchar%2 != 0)
		    state = 1;
		else if( numchar%2 == 0)
		    state = 2;
		break;


	    case 1:
		//stato dispari
		if(Character.isDigit(ch)){
		    if( numchar%2 != 0)
			state = 1;
		    else
			state = 2;
		}else if( ch >= 'A' && ch <= 'K')
		    state = -1;
		else
		    state = 3;
		break;


	    case 2:
		//stato pari
		if(Character.isDigit(ch)){
		    if( numchar%2 != 0){
			state = 1;
		    }else{
			state = 2;
		    }
		}else if( ch >= 'A' && ch <= 'K')
		    state = 3;
		else
		    state = -1;
		break;

	       
	    case 3:
		//stato aprovato
		if(Character.isDigit(ch))
		    state=-1;
		    
		
	    }

	}
	return state==3;
    }
    public static void main(String[] args) {
	System.out.println(scan(args[0]) ? "OK":"NOPE");
    }
}
