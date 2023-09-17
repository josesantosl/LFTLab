import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
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
        expr();
        match(Tag.EOF);
    }

    private void expr() {//E
        term();
        exprp();
    }

    private void exprp() {//E'
        switch (look.tag) {
        case '+':
        case '-':
            match(look.tag);
            term();
            exprp();
        }
    }

    private void term() {//T
        fact();
        termp();
    }

    private void termp() {//T'
        switch(look.tag){
        case '*':
        case '/':
            match(look.tag);
            fact();
            termp();
            break;
        }
    }

    private void fact() {//F
        switch(look.tag){
        case '(':
            match('(');
            expr();
            match(')');
            break;
        case Tag.NUM:
            match(Tag.NUM);
            break;
        default:
            error("expected number or new completed expression");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "test.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
