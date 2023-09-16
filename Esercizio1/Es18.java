public class Es18 {
    /*
      STATI:
      0. stato iniziale
      1. numero
      2. segni +,-
      3. punto
      4. esponenziale
     -1. stringa invalidata
     
     TABELLA:
     |----------------------|
     |stato|numero|-,+| .| e|
     |----------------------|
     | ->0 |   1  | 2 | 3|-1|
     | *1  |   1  | 2 | 3| 4|
     |  2  |   1  |-1 | 3|-1|
     |  3  |   1  | 2 |-1|-1|
     |  4  |   1  | 2 |-1|-1|
     |----------------------|
     
     anche ci sono due booleani di controllo per capire 
     se non ci sono multipli esponenti o punti.
     */
	public static boolean scan(String s) {
		int state = 0;
		int i = 0;
		boolean punto= false;
		boolean e    = false;
		while (state >=0 && i < s.length()) {
			final char ch = s.charAt(i++);
			switch (state) {
			case 0://stato iniziale
				if(Character.isDigit(ch)){
					state=1;
				}else if(ch=='+' || ch=='-'){
					state=2;
				}else if(ch=='.'){
					state=3;
				}else{
					state=-1;
				}
				break;
			case 1://numero
				if(Character.isDigit(ch)){
					state=1;
		}else if(ch=='+' || ch=='-'){
					state=2;
				}else if(ch=='.'){
					state=3;
				}else if(ch=='e'){
					state=4;
				}else{
					state=-1;
				}
				break;
			case 2://segno
				if(Character.isDigit(ch)){
					state=1;
				}else if(ch=='+' || ch=='-'){
					state=-1;
				}else if(ch=='.'){
					state=3;
				}else if(ch=='e'){
					state=-1;
				}else{
					state=-1;
				}
		break;
			case 3://punto
				if(punto){
					state=-1;
				}else if(Character.isDigit(ch)){
					state=1;
				}else if(ch=='+' || ch=='-'){
					state=-1;
				}else if(ch=='.'){
					state=-1;
				}else if(ch=='e'){
					state=-1;
				}else{
					state=-1;
				}
				punto=true;
				break;
			case 4://esponenziale
				if(e){
					state=-1;
				}else if(Character.isDigit(ch)){
					state=1;
				}else if(ch=='+' || ch=='-'){
					state=2;
				}else if(ch=='.'){
					state=3;
				}else if(ch=='e'){
					state=-1;
				}else{
					state=-1;
				}
				e=true;
				break;
			

			}

		}
		return state==1;

	 
	}
    public static void main(String[] args) {
		System.out.println(scan(args[0])? "OK":"NOPE");
    }
}
