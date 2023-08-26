import java.util.*;

public class SymbolTable {

    Map <String, Integer> OffsetMap = new HashMap <String,Integer>();

    public void insert( String s, int address ) {
	if( !OffsetMap.containsValue(address) ) 
	    OffsetMap.put(s,address);
	else 
	    throw new IllegalArgumentException("Reference to a memory location already occupied by another variable");
    }

    /*Voglio modificare questa funzione per passare un token invece di una
      stringa. So non c'e bisogno ma penso che sarebbe meglio evitare la
      repetizione nel translator, anche questo lo fa molto piu' facile da leggere
      il Translator. Penso che non è un cambio grande nel codice e spero non sia
      un problmea al momento dell'interrogazione.
    */
    public int lookupAddress ( Token t ) {
	if( OffsetMap.containsKey(((Word)t).lexeme))
	    return OffsetMap.get(((Word)t).lexeme);
	else
	    return -1;
    }
}
