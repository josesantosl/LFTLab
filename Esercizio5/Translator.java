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
		throw new Error("\nnear line " + lex.line + ":"+ look +": " + s);
    }

    private void match(int t) {
		if (look.tag == t) {
			if (look.tag != Tag.EOF) move();
		} else error("syntax error");
    }

    private void prog(){
		// GUIDA(<prog> -> <statlist> EOF) = assign, print, read, while, conditional, {
		switch(look.tag){
		case Tag.ASSIGN:
		case Tag.PRINT:
		case Tag.READ:
		case Tag.WHILE:
		case Tag.COND:
		case '{':
			int proglabel = code.newLabel();
			statlist();
			match(Tag.EOF);

			code.emit(OpCode.GOto,proglabel);
			code.emitLabel(proglabel);

			try {
				code.toJasmin();
			}catch(java.io.IOException e) {
				System.err.println("Error writting the Output.j");
			}
			break;
		case Tag.EOF:
			break;
		default:
			error("Incorrect prog start");
		}

    }

    private void statlist(){
		//GUIDA(<statlist> -> <stat><statlistp>) = assign, print, read, while, conditional, {
		switch(look.tag){
		case Tag.ASSIGN:
		case Tag.PRINT:
		case Tag.READ:
		case Tag.WHILE:
		case Tag.COND:
		case '{':
			stat();
			int lnext = code.newLabel();
			code.emit(OpCode.GOto,lnext);
			code.emitLabel(lnext);
			statlistp();
			break;
		case Tag.EOF:
			break;
		default:
			error("unexpected stat inside the statlist.");
		}
    }

    private void statlistp(){
		//GUIDA(<statlistp> -> ;<stat><statlistp>) = ;
		//GUIDA(<statlistp> -> eps) = EOF, }
		switch(look.tag){
		case ';':
			match(';');
			statlist();
			break;
		case '}':
		case Tag.EOF:
			break;
		default:
			error("unexpected stat inside the statlist.");
		}
    }

    private void stat(){
		switch (look.tag) {
		case Tag.ASSIGN: // GUIDA(<stat> -> assign<expr>to<idlist>) = assign
			match(Tag.ASSIGN);
			expr();
			match(Tag.TO);
			idlist(Tag.ASSIGN);
			break;
		case Tag.PRINT: // GUIDA(<stat> -> print[<exprlist>]) = print
			match(Tag.PRINT);
			match('[');
			exprlist(Tag.PRINT);
			match(']');
			break;
		case Tag.READ: // GUIDA(<stat> -> read[<idlist>]) = read
			match(Tag.READ);
			match('[');
			idlist(Tag.READ);
			match(']');
			break;
		case Tag.WHILE:
			match(Tag.WHILE); // GUIDA(<stat> -> while(<bexpr>)<stat>) = while
			int startloop  = code.newLabel();
			int endwhile   = code.newLabel();
			code.emitLabel(startloop);
			match('(');
			negbexpr(endwhile); // ! bexpr
			match(')');
			stat();
			code.emit(OpCode.GOto,startloop);
			code.emitLabel(endwhile);
			break;
		case Tag.COND: // GUIDA(<stat> -> conditional[<optlist>]end) = conditional
			int lendCondition = code.newLabel();
			match(Tag.COND);
			match('[');
			optlist(lendCondition);
			match(']');

			switch (look.tag) {
			case Tag.ELSE: // GUIDA(<stat> -> conditional[<optlist>]else<stat>end) = conditional
				match(Tag.ELSE);
				stat();
			case Tag.END:
				match(Tag.END);
				code.emitLabel(lendCondition);
				break;
			default:
				error("unclosed OPTION");
			}
			break;

		case '{': // GUIDA(<stat> -> {<statlist>}) = {
			match('{');
			statlist();
			match('}');
			break;
		default:
			error("not valid stat");
		}
    }

    private void idlist(int op){
		// GUIDA(<idlist> -> ID<idlistp>) = ID
		//Registra le identificatori che ancora non sono registrati al SymbolTable.
		if(look.tag == Tag.ID){
			int address = st.lookupAddress(look);
			if (address == -1) {
				st.insert(look,count);
				address = count++;
			}

			match(Tag.ID);

			if (op == Tag.READ) {
				code.emit(OpCode.invokestatic,0);//invokestatic READ
			}

			code.emit(OpCode.istore,address); // Salva il valore

			if (look.tag == ',' && op == Tag.ASSIGN) {
				code.emit(OpCode.iload,address);
			}
		}else{
			error("is not a identifier.");
		}

		//FOLLOW(idlist) = {',ID',';',']','}','$'}
		idlistp(op);
    }

    private void idlistp(int op){
		// GUIDA(<idlistp> -> ,ID<idlistp>) = ,
		// GUIDA(<idlistp> -> eps) = ], ;, }, end, option, EOF
		//FOLLOW(idlist) = {',ID',';',']','}','$'}
		switch(look.tag){
		case ',':
			match(',');
			idlist(op);
			break;
		case ']':
		case '}':
		case ';':
		case Tag.EOF:
		case Tag.END:
			break;
		default:
			error("no identifier was found.");
		}
    }

    private void optlist(int lendCondition){
		// GUIDA(<optilist> -> <optitem><optlistp>) = option
		if(look.tag==Tag.OPTION){
			int lnext = code.newLabel();
			optitem(lnext,lendCondition);
			optlistp(lendCondition);
		}else{
			error("unexpected OPTION inside option list.");
		}
    }

	private void optlistp(int lendCondition){
		// GUIDA(<optlistp> -> <optitem><optlistp>) = option
		// GUIDA(<optlistp> -> eps) = ]
		// FOLLOW(optlistp) = {'option',']'}
		switch(look.tag){
		case Tag.OPTION:
			optlist(lendCondition);
			break;
		case ']':
			break;
		default:
			error("unexpected OPTION inside option list.");
		}
    }

    private void optitem(int lnext, int lendCondition){
		// GUIDA(<optitem> -> option(<bexpr>)do<stat>) = option
		int ltrue = code.newLabel();
		match(Tag.OPTION);
		match('(');
		bexpr(ltrue);
		match(')');
		match(Tag.DO);

		code.emit(OpCode.GOto,lnext);
		code.emitLabel(ltrue);
		stat();
		code.emit(OpCode.GOto,lendCondition);
		code.emitLabel(lnext);
    }

    private void bexpr(int truelabel){
		// GUIDA(<bexpr> -> RELOP<expr><expr>) = RELOP
		int lFalse; //per i casi dove dobbiamo controllare sia vero o falso (AND e OR)
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
			default:
				error("unrecognized relational operator");
			}

			break;

		//Facoltativo 5.2
		case '!': // ! true = false // ! false = true
			match('!');
			negbexpr(truelabel);
			break;
		case Tag.AND:
			match(Tag.AND);

			int lAnd  = code.newLabel();
			lFalse    = code.newLabel();

			bexpr(lAnd);
			code.emit(OpCode.GOto,lFalse);
			code.emitLabel(lAnd);
			bexpr(truelabel);
			code.emit(OpCode.GOto,lFalse);
			code.emitLabel(lFalse);
			break;
		case Tag.OR:
			match(Tag.OR);

			lFalse = code.newLabel();

			bexpr(truelabel);
			bexpr(truelabel);
			code.emit(OpCode.GOto,lFalse);
			code.emitLabel(lFalse);
			break;
		default:
			error("Is not a relational operator");
		}
    }

    private void negbexpr(int truelabel){
		int lFalse; //per i casi dove dobbiamo controllare sia vero o falso (AND e OR)
		switch (look.tag) {
		case Tag.RELOP:
			String relatinaloperator = ((Word)look).lexeme;
			match(Tag.RELOP);
			expr();
			expr();
			switch (relatinaloperator) {
			case ">":
				code.emit(OpCode.if_icmple,truelabel);// <=
				break;
			case "<":
				code.emit(OpCode.if_icmpge,truelabel);// >=
				break;
			case "<=":
				code.emit(OpCode.if_icmpgt,truelabel);// >
				break;
			case ">=":
				code.emit(OpCode.if_icmplt,truelabel);// <
				break;
			case "==":
				code.emit(OpCode.if_icmpne,truelabel);// <>
				break;
			case "<>":
				code.emit(OpCode.if_icmpeq,truelabel);// ==
				break;
			default:
				error("unrecognized relational operator");
			}
		case '!':
			match('!');
			bexpr(truelabel); // !!<bexpr> = <bexpr>
			break;
		case Tag.AND:
			//invertiti i label del and. Cosi se entrambi sono veri va a false,
			//se no va a true.
			match(Tag.AND);

			lFalse = code.newLabel();

			//!(A and B) = !A or !B
			bexpr(lFalse);
			bexpr(lFalse);
			code.emit(OpCode.GOto,truelabel);
			code.emitLabel(lFalse);
			break;
		case Tag.OR:
			//invertiti i label del or. cosi se uno di loro è vero va a false,
			//se no va a true.
			match(Tag.OR);
			lFalse = code.newLabel();

			bexpr(lFalse);
			bexpr(lFalse);
			code.emit(OpCode.GOto,truelabel);
			code.emitLabel(lFalse);
			break;
		default:
			error("Is not a relational operator");
		}
	}

    private void expr(){
		switch (look.tag) {
		case '+': // GUIDA(<expr> -> +(<exprlist>)) = +
			match('+');
			match('(');
			exprlist('+');
			match(')');
			break;

		case '*': // GUIDA(<expr> -> *(<exprlist>)) = *
			match('*');
			match('(');
			exprlist('*');
			match(')');
			break;
		case '-': // GUIDA(<expr> -> -<expr><expr>) = -
			match('-');
			expr();
			expr();
			code.emit(OpCode.isub);
			break;
		case '/': // GUIDA(<expr> -> /<expr><expr>) = /
			match('/');
			expr();
			expr();
			code.emit(OpCode.idiv);
			break;
		case Tag.NUM:
			/*devo fare il cast a NumberTok perche java non sa che il look e'
			 *un NumberTok e non un semplice Token.
			 */
			int num = ((NumberTok)look).num; //(numberTok)look dice al look di comportarsi come NumberTok
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
		// GUIDA(<exprlist> -> <expr><exprlistp>) = +,-,*,/, NUM, ID
		expr();
		if(op == Tag.PRINT){
			/*invokestatic 1 significa print, altrimente sarebbe READ(non e'
			  specificato nel progetto ma possiamo dire che sarebbe 0 in quel caso)*/
			code.emit(OpCode.invokestatic,1);//nel caso del print dobbiamo usarlo per ogni espressione perche e' un'operazione unitaria e non binaria
		}
		//FOLLOW(exprlist)
		exprlistp(op);
    }

    private void exprlistp(int op){
		// GUIDA(<exprlistp> -> ,<expr><exprlistp>) = ,
		// GUIDA(<exprlistp> -> eps) = ),]
		switch(look.tag){
		case ',':
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
		case ')':
		case ']':
			break;
		default:
			error("Error inside the expression list.");
		}
    }

    public static void main(String[] args) {

		Lexer lex = new Lexer();
		String path = "test.lft";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			Translator translator = new Translator(lex, br);
			translator.prog();
			//System.out.println(path+" tradotto correttamente.");
			br.close();
		} catch (IOException e) {e.printStackTrace();}
    }
}
