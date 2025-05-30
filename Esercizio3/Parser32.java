import java.io.*;
public class Parser32 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser32(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        if(look.tag == ';')
            System.out.println(look+ " ");
        else
            System.out.print(look+ " ");
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ":"+ look.tag+": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }

    void prog(){
        statlist();
        match(Tag.EOF);
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

    void stat(){
        switch (look.tag) {
        case Tag.ASSIGN: //assign 5 to a,b,c
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
        case Tag.READ: //read[a,b,c]
            match(Tag.READ);
            match('[');
            idlist();
            match(']');
            break;
        case Tag.WHILE:
            match(Tag.WHILE);
            match('(');
            bexpr();
            match(')');
            stat();
            break;
        case Tag.COND:
            match(Tag.COND);
            match('[');
            optlist();
            match(']');
            switch (look.tag) {
            case Tag.ELSE:
                match(Tag.ELSE);
                stat();
            case Tag.END:
                match(Tag.END);
                break;
            default:
                error("not ended conditional");

            }
            break;
        case '{':
            match('{');
            statlist();
            match('}');
            break;
        default:
            error("not valid stat");
        }
    }
    void idlist(){
        match(Tag.ID);
        idlistp();
    }
    void idlistp(){
        if(look.tag == ','){
            match(',');
            match(Tag.ID);
            idlistp();
        }
    }

    void optlist(){
        optitem();
        optlistp();
    }
    void optlistp(){
        if(look.tag == Tag.OPTION){
            optitem();
            optlistp();
        }
    }

    void optitem(){
        match(Tag.OPTION);
        match('(');
        bexpr();
        match(')');
        match(Tag.DO);
        stat();
    }

    void bexpr(){
        switch (look.tag) {
        case Tag.RELOP: // ==,>=,<=,>,<
            match(Tag.RELOP);
            expr();
            expr();
            break;
        case '!':  // !true = false | !false = true
            match('!');
            bexpr();
            break;
        case Tag.AND:
        case Tag.OR:
            match(look.tag);
            bexpr();
            bexpr();
            break;
        default:
            error("unexpected boolean expression");
        }

    }

    void expr(){
        switch (look.tag) {
        case '+':
        case '*':
            match(look.tag);
            match('(');
            exprlist();
            match(')');
            break;
        case '-':
        case '/':
            match(look.tag);
            expr();
            expr();
            break;
        case Tag.NUM:
        case Tag.ID:
            match(look.tag);
            break;
        default:
            error("Invalid Expresion");
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

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "test.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser32 parser = new Parser32(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}

    }
}
