import java.util.*;

public class SymbolTable {

    Map <String, Integer> OffsetMap = new HashMap <String,Integer>();

    /*
     *abbiamo modificato queste funzione per passare un token invece di una stringa.
     *Pensiamo che sarebbe meglio evitare la ripettizione del cast nel Translator,
     *affinche il Transaltor sia più facile da leggere.
     */
    public String legge(Token t){
        return ((Word)t).lexeme;
    }

    public void insert( Token t, int address ) {
        if( !OffsetMap.containsValue(address) )
            OffsetMap.put(legge(t),address);
        else
            throw new IllegalArgumentException("Reference to a memory location already occupied by another variable");
    }

    public int lookupAddress ( Token t ) {
        try{
            if( OffsetMap.containsKey(legge(t)))
                return OffsetMap.get(legge(t));
            else
                return -1;
        }catch(ClassCastException e){
            System.err.println("error en lookup en el token "+t);
            return 0;
        }
    }
}
