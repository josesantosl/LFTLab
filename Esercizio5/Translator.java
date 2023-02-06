import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() { 
	// come in Esercizio 3.1
	look = lex.lexical_scan(pbr);
        System.out.print(look+ " ");

    }

    void error(String s) { 
	// come in Esercizio 3.1
	throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
	// come in Esercizio 3.1
	if (look.tag == t) {
	    if (look.tag != Tag.EOF) move();
	} else error("syntax error");
    }

    public void prog() {        
	// ... completare ...
        int lnext_prog = code.newLabel();
        //statlist(lnext_prog); //ancora non capisco perche c'e' lnext_prog
        code.emitLabel(lnext_prog);
        match(Tag.EOF);
        try {
        	code.toJasmin();
        }
        catch(java.io.IOException e) {
        	System.out.println("IO error\n");
        };
	// ... completare ...
    }

    public void stat( /* completare */ ) {
        switch(look.tag) {
	case Tag.ASSIGN:
	    match(Tag.ASSIGN);
	    expr();
	    match(Tag.TO);
	    idlist();
	    break;
	case Tag.PRINT:
	    match(Tag.PRINT);
	    match('[');
	    exprlist();
	    match(']');
	    break;
	case Tag.READ:
	    match(Tag.READ);
	    match('[');
	    idlist(/* completare */);
	    match(']');
	    break;
	case Tag.WHILE:
	    match(Tag.WHILE);
	    
	case Tag.COND:
	case '{':
	default:
	    error("not valid stat");
        }
     }
    void statlist(){
	stat();
	statlistp();
    }

    void statlistp(){
	if(look.tag == ';'){
	    match(';');
	    stat();
	    statlistp();
	}
    }

    private void idlist(int value) {
        switch(look.tag) {
	    case Tag.ID:
        	int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1) {
                    id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                match(Tag.ID);
		if(look.tag == ','){
		    match(',');
		    idlist(value);
		}
		break;
	    default:
		error("expected another var");
		
    	}
    }

    private void expr() {
        switch(look.tag) {
	case '+':
	    match('+');
	    match('(');
	    exprlist();
	    match(')');
	    code.emit(OpCode.iadd);
	    break;
	case '-':
	    match('-');
	    expr();
	    expr();
	    code.emit(OpCode.isub);
	    break;
	case '*':
	    match('*');
	    match('(');
	    exprlist();
	    match(')');
	    code.emit(OpCode.imul);
	    break;
	case '/':
	    match('/');
	    expr();
	    expr();
	    code.emit(OpCode.idiv);
	    break;
	case Tag.NUM:
	    code.emit(OpCode.ldc,((NumberTok)look).num);
	    match(Tag.NUM);
	    break;
	case Tag.ID:
	    code.emit(OpCode.iload,((Word)look).lexeme);
	    match(Tag.ID);
	    break;
	default:
	    error("Invalid Expression");
        }
    }

    void exprlist(){
	expr();
	exprlistp();
    }
    void exprlistp(){
	if(look.tag == ','){
	    match(',');
	    expr();
	    exprlistp();
	}
    }

// ... completare ...
    public static void main(String[] args) {
	Lexer lex = new Lexer();
	String path = "Test.lft";
	try {
	    BufferedReader br = new BufferedReader(new FileReader(path));
	    Translator translator = new Translator(lex,br);
	    translator.prog();
	    br.close();
	}
	catch (IOException e) {e.printStackTrace();}

    }
}
