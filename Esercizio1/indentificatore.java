public class indentificatore {
    /*
      CONDIZIONI IDENTIFICATORE:
      - non comincia con un numero 
      - bib puo essere composto solo di simboli _.
      
      |stato|num|let|und|
      +-----------------+
      |->0  |-1 | 2 | 1 |
      |  1  | 2 | 2 | 1 |
      |  2  | 2 | 2 | 2 | 
    */
    public static boolean identificador(String s){
	int state=0;//stato presente
	int i=0;
	while ( state>=0 && i<s.length() ){
	    final char ch = s.charAt(i++);
	    switch(state){
	    case 0:
		if(ch=='_'){
		    state=1;
		}else if(Character.isLetter(ch)){
		    state=2;
		}else{
		    state=-1;
		}
		break;
	    case 1:
		if( Character.isDigit(ch) || Character.isLetter(ch)){
		    state=2;
		}else if(ch=='_'){
		    state=1;
		}else{
		    state=-1;
		}
		break;
	    case 2:
		if( Character.isDigit(ch) || Character.isLetter(ch) || ch=='_'){
		    state=2;
		}else{
		    state=-1;
		}
		break;
	    }
	}
	return state==2;
	
    }

    public static void main(String[] args) {
	System.out.println(identificador(args[0]) ? "OK":"NOPE");
    }
}
