public class Es17 {
    public static boolean scan(String orig, String prov ) {
	int state=0;
	int i    =0;
	if( orig.length() != prov.length())
	    state=-1;

	while( state >= 0 && i < orig.length()){
	    final char chO = orig.charAt(i);
	    final char chP = prov.charAt(i);
	    if(chO != chP)
		state++;

	    state++;
	    i++;
	}
	return state <= orig.length()+1 && state>0;
    }

    public static void main(String[] args) {
	System.out.println(scan(args[0],args[1])? "OK":"NOPE");
    }
}
