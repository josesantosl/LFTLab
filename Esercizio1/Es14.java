public class Es14 {
    /*
     INPUT:
       matricola e cognome separato per uno spazio.

     CONDIZIONI:
     - dalla A alla K e matricola pari
     - dalla L alla Z e matricola dispari

     STATI:
     -  0: stato iniziale
     -  1: stato dispari
     -  2: stato pari
     -  3: stato di spazio dopo dispari
     -  4: stato di spazio dopp pari
     -  5: stato aprovato
     - -1: stato negato o errore

     |STATO|pari|dispari|A-K|L-Z|spazio|
     |---------------------------------|
     |->0  |  2 |   1   |-1 |-1 |   0  |
     |  1  |  2 |   1   |-1 |-1 |   3  |
     |  2  |  2 |   1   |-1 |-1 |   4  |
     |  3  | -1 |  -1   |-1 | 5 |   3  |
     |  4  | -1 |  -1   | 5 |-1 |   4  |
     | *5  | -1 |  -1   | 5 | 5 |   5  |
     |---------------------------------|
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
			if( ch == ' ')
				state = 0;
			else if(Character.isLetter(ch))
				state = -1;
			else if( numchar%2 != 0)
				state = 1;
			else if( numchar%2 == 0)
				state = 2;
			break;


	    case 1:
			//stato dispari
			if( ch == ' ')
				state = 3;
		else if(Character.isDigit(ch)){
		    if( numchar%2 != 0)
				state = 1;
		    else
				state = 2;
		}else
		    state = -1;
			break;


	    case 2:
			//stato pari
			if(ch == ' ')
				state = 4;
			else if(Character.isDigit(ch)){
				if( numchar%2 != 0){
					state = 1;
				}else{
					state = 2;
				}
			}else
				state = -1;
			break;


	    case 3:
			//stato spazio dispari
			if( ch == ' ')
				state = 3;
			else if( ch >= 'L' && ch <= 'Z')
				state = 5;
			else
				state = -1;
			break;

	    case 4:
			//stato spazio pari
			if( ch == ' ')
				state = 4;
			else if( ch >= 'A' && ch <= 'K')
				state = 5;
			else
				state = -1;
			break;
	    case 5:
			//stato aprovato
			if( Character.isLetter(ch) || ch==' ' )
				state = 5;
			else
				state = -1;
	    }

		}
		return state==5;
    }
    public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK":"NOPE");
    }
}
