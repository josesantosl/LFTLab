public class Es15 {
    /*
     CONDIZIONI
     - matricola pari e dalla A alla K
     -matricola dispari e dalla L alla Z  

     STATI:
     -  0: stato iniziale
     -  1: stato iniziale A - K
     -  2: stato iniziale L - Z
     -  3: stato AK dispari
     -  4: stato AK pari
     -  5: stato LZ dispari
     -  6: stato LZ pari
     - -1: stato negato o errore

     |STATO|dispari|pari|A-K|L-Z|
     |--------------------------|
     |->0  |  -1   | -1 | 1 | 2 |
     |  1  |   3   |  4 | 1 | 1 |
     |  2  |   5   |  6 | 2 | 2 |
     |  3  |   3   |  4 |-1 |-1 |
     | *4  |   3   |  4 |-1 |-1 |
     | *5  |   5   |  6 |-1 |-1 |
     |  6  |   5   |  6 |-1 |-1 |
     |--------------------------|
     
     OK: state == 4 || state == 5 
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
				if( ch >= 'A' && ch <= 'K'){
					state = 1;
				}
				else if( ch >= 'L' && ch <= 'Z'){
					state = 2;
				}
				else{
					state = -1;
				}
				break;


			case 1:
				//stato iniziale A - K
				if(Character.isDigit(ch)){
					if( numchar%2 != 0)
						state = 3;
					else
						state = 4;
				}else if(Character.isLetter(ch))
					state = 1;
				else
					state = -1;
		break;


			case 2:
				//stato iniziale L - Z
				if(Character.isDigit(ch)){
					if( numchar%2 != 0)
						state = 5;
					else
						state = 6;
				}else if(Character.isLetter(ch))
					state = 2;
				else
					state = -1;
				break;


			case 3:
				//stato dispari AK
				if(Character.isLetter(ch))
					state=-1;
				else if( numchar%2 != 0)
					state = 3;
				else
					state = 4;
				break;

			case 4:
				//stato pari AK
				if(Character.isLetter(ch))
					state=-1;
				if( numchar%2 != 0)
					state = 3;
				else if(numchar%2 == 0)
					state = 4;
				break;

			case 5:
				//stato dispari LZ
				if(Character.isLetter(ch))
					state=-1;
				else if( numchar%2 != 0)
					state = 5;
				else
					state = 6;
				break;

			case 6:
				//stato pari LZ
				if(Character.isLetter(ch))
					state=-1;
				else if( numchar%2 != 0)
					state = 5;
				else
					state = 6;
		
			}
			//System.out.print(state);
			//System.out.println(ch);

		}
		return state==4 || state==5;
    }

    public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "OK":"NOPE");
    }
}
