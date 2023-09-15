import java.io.*; 

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
        lex = l;
        pbr = br;
        move();
    }

    void move() { 
        look = lex.lexical_scan(pbr);
        System.out.print(look+ " ");
    }

    void error(String s) { 
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }

    public void start() { 
        int expr_val;

	if(look.tag == Tag.EOF){
	    error("null file");
	}
    
    	expr_val = expr();
        match(Tag.EOF);

        System.out.println(" = "+expr_val);

    }

    private int expr() { 
        int term_val,exprp_val;
        term_val   = term();
        exprp_val = exprp(term_val);
	
        return exprp_val;
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val;

	switch (look.tag) {
	case '+':
        match('+');
        term_val = term();
	    exprp_i = exprp_i + term_val;
	    exprp_val = exprp(exprp_i); 
        break;

	case '-':
	    match('-');
	    term_val = term();
	    exprp_i = exprp_i - term_val;
        exprp_val = exprp(exprp_i);
        break;
	default:
	    exprp_val = exprp_i;
	}

	return exprp_val;

    }

    private int term() { 

        int fact_val = fact();
        int term_val = termp(fact_val);
        return term_val;

    }

    private int termp(int termp_i) {
        int termp_val,fact_val;

        switch (look.tag) {
        case '*':
            match('*');
            fact_val   = fact();
            termp_i  = termp_i * fact_val;
            termp_val= termp(termp_i);
            break;
        case '/':
            match('/');
            fact_val   = fact();
            termp_i  = termp_i / fact_val;
            termp_val= termp(termp_i);
            break;
        default:
            termp_val= termp_i;
        }
        return termp_val;

    }

    private int fact() { 
        int fact_val;
        switch(look.tag){
        case '(':
            match('(');
            fact_val=expr();
            match(')');
            break;
        case Tag.NUM:
            NumberTok numerico = (NumberTok) look;
            fact_val = numerico.num;
            match(Tag.NUM);
            break;
        default:
            error("invalid fact");
            fact_val=0;
        }
        return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "test.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
