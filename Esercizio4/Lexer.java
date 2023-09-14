import java.io.*; 
import java.util.*;

public class Lexer {

    static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') { //'t'=TAP, 'n'=salto di riga, 'r'=carrige return
            if (peek == '\n'){//salto di linea
                line++;
            }
            readch(br);
        }
        
        switch (peek) {
        case '!':
            peek = ' ';
            return Token.not;
        case '(':
            peek = ' ';
            return Token.lpt;
        case ')':
            peek = ' ';
            return Token.rpt;
        case '[':
            peek = ' ';
            return Token.lpq;
        case ']':
            peek = ' ';
            return Token.rpq;
        case '{':
            peek = ' ';
            return Token.lpg;
        case '}':
            peek = ' ';
            return Token.rpg;
        case '+':
            peek = ' ';
            return Token.plus;
        case '-':
            peek = ' ';
            return Token.minus;
        case '_':
            peek = ' ';
            return Token.underscore;
        case '*':
            peek = ' ';
            return Token.mult;
        case '/':
            readch(br);
            if(peek=='*'){//start the comment.
                boolean comment=true;
                while(comment){
                    readch(br);
                    while(peek == '*') {//salto tutti i * fino a trovare il /
                        readch(br);
                        if (peek == '/') {
                            comment=false;
                            peek=' ';
                            return lexical_scan(br);
                        }
                    }
                    if (peek == -1) {
                        System.err.println("comment not closed");
                    }
                }
            }else if(peek=='/'){//start commentline
                while(peek!='\n'){
                    readch(br);
                }
                return lexical_scan(br);
            }else{
                return Token.div;
            }
        case ';':
            peek = ' ';
            return Token.semicolon;
        case ',':
            peek = ' ';
            return Token.comma;


        case '&':
            readch(br);
            if (peek == '&') {
                peek = ' ';
                return Word.and;
            } else {
                System.err.println("Erroneous character"
                                   + " after & : "  + peek );
                return null;
            }

            // ... gestire i casi di || < > <= >= == <> ... //
        case '|':
            readch(br);
            if (peek == '|') {
                peek = ' ';
                return Word.or;
            } else {
                System.err.println("Erroneous character after | : "  + peek );
                return null;
            }

        case '<':
            readch(br);
            if (peek == '=') {
                peek = ' ';
                return Word.le;
            } else if(peek == '>'){
                peek = ' ';
                return Word.ne;
            } else {
                //System.err.println("Erroneous character after < : "  + peek );
                return Word.lt;
            }

        case '>':
            readch(br);
            if (peek == '=') {
                peek = ' ';
                return Word.ge;
            } else {
                //System.err.println("Erroneous character after > : "  + peek );
                return Word.gt;
            }

        case '=':
            readch(br);
            if (peek == '=') {
                peek = ' ';
                return Word.eq;
            } else {
                System.err.println("Erroneous character"
                                   + " after = : "  + peek );
                return null;
            }


        case (char)-1:
            return new Token(Tag.EOF);

        default:
            String word = "";
            if (Character.isLetter(peek)) {
                // ... gestire il caso degli identificatori e delle parole chiave //
                do { //complete the word with the other characters
                    word+=peek;
                    readch(br);
                }while(Character.isLetter(peek) || Character.isDigit(peek) || peek=='_');
                switch(word){
                case "assign":
                    return Word.assign;
                case "to":
                    return Word.to;
                case "conditional":
                    return Word.conditional;
                case "option":
                    return Word.option;
                case "do":
                    return Word.dotok;
                case "else":
                    return Word.elsetok;
                case "while":
                    return Word.whiletok;
                case "begin":
                    return Word.begin;
                case "end":
                    return Word.end;
                case "print":
                    return Word.print;
                case "read":
                    return Word.read;
                default:
                    return new Word(Tag.ID,word);
                }

            }else if(Character.isDigit(peek)) {

                // ... gestire il caso dei numeri ... //
                do { //Complete the number with the next digits.
                    word+=peek;
                    readch(br);
                } while (Character.isDigit(peek));
                return new NumberTok(Integer.parseInt(word));
            } else {
                System.err.println("Erroneous character: "
                                   + peek );
                return null;
            }
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "test.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.print(tok+" ");
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}
