import java.io.*;

public class Translator { // Un Parser32 adattato.
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st     = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count          = 0;//counter di registri del symboltable

    public Translator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
	move();
    }

    void move() {
	look = lex.lexical_scan(pbr);

	if(look.tag == ';' || look.tag == -1)
	    System.out.println(look);
	else
	    System.out.print(look+ " ");
    }

    private void error(String s) {
	throw new Error("near line " + lex.line + ":"+ look +": " + s);
    }

    private void match(int t) {
	if (look.tag == t) {
	    if (look.tag != Tag.EOF) move();
	} else error("syntax error");
    }

    private void prog(){//prog() -> statlist(lnext) EOF

	if (look.tag == Tag.EOF) {
	    error("file vuoto");
	}

        int proglabel = code.newLabel();
	statlist(proglabel);
	match(Tag.EOF);

	code.emit(OpCode.GOto,proglabel);
        code.emitLabel(proglabel);


        try {
	    code.toJasmin();
        }catch(java.io.IOException e) {
	    System.err.println("Error writting the Output.j");
	}
	
    }

    private void statlist(int labelAttuale){
	stat(labelAttuale);
	int lnext = code.newLabel();
	code.emit(OpCode.GOto,lnext);
	code.emitLabel(lnext);
	statlistp(lnext);
    }

    private void statlistp(int labelAttuale){
	if(look.tag == ';'){
	    match(';');
	    stat(labelAttuale);
	    int lnext = code.newLabel();
	    code.emit(OpCode.GOto,lnext);
	    code.emitLabel(lnext);
	    statlistp(lnext);
	}
    }

    private void stat(int labelattuale){
	switch (look.tag) {
	case Tag.ASSIGN:
	    match(Tag.ASSIGN);
	    expr();
	    match(Tag.TO);
	    idlist(Tag.ASSIGN);
	    break;
	case Tag.PRINT:
	    match(Tag.PRINT);
	    match('[');
	    exprlist(Tag.PRINT);
	    match(']');
	    break;
	case Tag.READ:
	    match(Tag.READ);
	    match('[');
	    idlist(Tag.READ);
	    match(']');
	    break;
	case Tag.WHILE:
	    match(Tag.WHILE);
	    int endwhile   = code.newLabel();
	    match('(');
	    bexpr(endwhile);
	    match(')');
	    stat(labelattuale);
	    code.emit(OpCode.GOto,labelattuale);
	    code.emitLabel(endwhile);
	    break;
	case Tag.COND:
	    match(Tag.COND);
	    match('[');
	    optlist();
	    match(']');
	    switch (look.tag) {
	    case Tag.ELSE:
		match(Tag.ELSE);
		stat(labelattuale);
	    case Tag.END:
		match(Tag.END);
		break;
	    default:
		error("not valid command");

	    }
	    break;
	case '{':
	    match('{');
	    statlist(labelattuale);
	    match('}');
	    break;
	default:
	    error("not valid stat");
	}
    }
    private void  idlist(int op){
	if(look.tag == Tag.ID){
	    int address = st.lookupAddress(look);
	    if (address == -1) {
		st.insert(((Word)look).lexeme,count);
		address = count;
	    }

	    if (op == Tag.READ) {
		code.emit(OpCode.invokestatic,0); // invokestatic 0 = read
	    }
	    code.emit(OpCode.istore,address);
	    match(Tag.ID);
	    count++;
	    idlistp(op);
	}else{
	    error("no identifier was found.");
	}
    }
    private void idlistp(int op){
	if(look.tag == ','){
	    match(',');
	    if(look.tag == Tag.ID){
		int address = st.lookupAddress(look);
		if (address == -1) {
		    st.insert(((Word)look).lexeme,count);
		    address = count;
		}

		if (op == Tag.READ) {
		    code.emit(OpCode.invokestatic,0); // invokestatic 0 = read
		}
		code.emit(OpCode.istore,address);
		match(Tag.ID);
		idlistp(op);
	    }else{
		error("no identifier was found.");
	    }

	}
    }

    private void optlist(){
	optitem();
	optlistp();
    }

    private void optlistp(){
	if(look.tag == Tag.OPTION){
	    optitem();
	    optlistp();
	}
    }

    private void optitem(){
	match(Tag.OPTION);
	match('(');
	//bexpr();
	match(')');
	match(Tag.DO);
	//stat(lnex); //da riparare come pasare tutti i label
    }

    private void bexpr(int truelabel){
	switch (look.tag) {
	case Tag.RELOP:
	    String relatinaloperator = ((Word)look).lexeme;
	    match(Tag.RELOP);
	    expr();
	    expr();
	    switch (relatinaloperator) {
	    case ">":
		code.emit(OpCode.if_icmpgt,truelabel);
		break;
	    case "<":
		code.emit(OpCode.if_icmplt,truelabel);
		break;
		
	    case "<=":
		code.emit(OpCode.if_icmple,truelabel);
		break;
	    case ">=":
		code.emit(OpCode.if_icmpge,truelabel);
		break;
	    case "==":
		code.emit(OpCode.if_icmpeq,truelabel);
		break;
	    case "<>":
		code.emit(OpCode.if_icmpne,truelabel);
		break;
	    }

	    break;
	case '!':
	    match('!');
	    //bexpr();
	    code.emit(OpCode.ineg, truelabel);
	    break;
	case Tag.AND:
	    match(Tag.AND);
	    expr();
	    expr();
	    code.emit(OpCode.iand, truelabel);
	    break;
	case Tag.OR:
	    match(Tag.OR);
	    expr();
	    expr();
	    code.emit(OpCode.ior, truelabel);
	    break;
	default:
	    error("is not a relational operator");
	}
    }

    private void expr(){
	switch (look.tag) {
	case '+':
	    match('+');
	    match('(');
	    exprlist('+');
	    match(')');
	    break;

	case '*':
	    match('*');
	    match('(');
	    exprlist('*');
	    match(')');
	    break;
	case '-':
	    match('-');
	    expr();
	    expr();
	    code.emit(OpCode.isub);
	    break;
	case '/':
	    match('/');
	    expr();
	    expr();
	    code.emit(OpCode.idiv);
	    break;
	case Tag.NUM:
	    /*devo fare il cast a NumberTok perche java non sa che il look e'
	     *un NumberTok e non un semplice Token.
	    */
	    int num = ((NumberTok)look).num;
	    code.emit(OpCode.ldc,num);
	    match(Tag.NUM);
	    break;
	case Tag.ID:
	    /*Questo e' l'unico momento di lettura dei SymbolTable*/
	    int address = st.lookupAddress(look);

	    if (address != -1) {
		code.emit(OpCode.iload, address);
	    }else{
		error("identifier not found.");
	    }
	    match(Tag.ID);
	    break;
	default:
	    error("Invalid Expresion");
	}

    }

    private void exprlist(int op){
	expr();
	if(op == Tag.PRINT){
	    /*invokestatic 1 significa print, altrimente sarebbe READ(non e'
	    specificato nel progetto ma possiamo dire che sarebbe 0 in quel caso)*/
	    code.emit(OpCode.invokestatic,1);//nel caso del print dobbiamo usarlo per ogni espressione perche e' un'operazione unitaria e non binaria 
	}
	exprlistp(op);
    }

    private void exprlistp(int op){
	if(look.tag == ','){
	    match(',');
	    expr();
	    switch (op) {
	    case '+':
		code.emit(OpCode.iadd);
		break;
	    case '*':
		code.emit(OpCode.imul);
		break;
	    case Tag.PRINT:
		code.emit(OpCode.invokestatic,1); // invokestatic 1 = print
		break;
	    default:
		error("invalid Operator");
	    }
	    exprlistp(op);
	}
    }

    public static void main(String[] args) {

	Lexer lex = new Lexer();
	String path = "test.lft";
	try {
	    BufferedReader br = new BufferedReader(new FileReader(path));
	    Translator parser = new Translator(lex, br);
	    parser.prog();
	    //System.out.println(path+" tradotto correttamente.");
	    br.close();
	} catch (IOException e) {e.printStackTrace();}
    }
}
