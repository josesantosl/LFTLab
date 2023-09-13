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

    private void prog(){//prog() -> statlist(lnext) EOF

		switch(look.tag){
		//GUIDA(prog):
		case Tag.ASSIGN:
		case Tag.PRINT:
		case Tag.READ:
		case Tag.WHILE:
		case Tag.COND:
		case '{':
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
			break;
		case Tag.EOF:
			break;
		default:
			error("Incorrect prog start");
		}

    }

    private void statlist(int labelAttuale){
		switch(look.tag){
		//GUIDA(statlist) = {'assign','print','read','while','condition','{',$}
		case Tag.ASSIGN:
		case Tag.PRINT:
		case Tag.READ:
		case Tag.WHILE:
		case Tag.COND:
		case '{':
			stat(labelAttuale);
			int lnext = code.newLabel();
			code.emit(OpCode.GOto,lnext);
			code.emitLabel(lnext);
			statlistp(lnext);
			break;
		case Tag.EOF:
			break;
		default:
			error("unexpected stat inside the statlist.");
		}
    }

    private void statlistp(int labelAttuale){
		//FOLLOW(Statlist) = {';','}','$'}
		switch(look.tag){
		case ';':
			match(';');
			statlist(labelAttuale);
			break;
		case '}':
		case Tag.EOF:
		case Tag.END:
			break;
		default:
			error("unexpected stat inside the statlist.");
		}
    }

    private void stat(int labelAttuale){
		//GUIDA(stat) = {'assign','print','read','while','condition','{'}
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
		case Tag.WHILE: // da modificare a due label
			match(Tag.WHILE);
			int startloop = code.newLabel();
			int endwhile   = code.newLabel();
			match('(');
			bexpr(startloop);
			match(')');
			code.emit(OpCode.GOto,endwhile);
			code.emitLabel(startloop);
			stat(labelAttuale);
			code.emit(OpCode.GOto,labelAttuale);
			code.emitLabel(endwhile);
			break;
		case Tag.COND:
			int lendCondition = code.newLabel();
			match(Tag.COND);
			match('[');
			optlist(lendCondition);
			match(']');
			switch (look.tag) {
			case Tag.ELSE:
				match(Tag.ELSE);
				stat(labelAttuale);
			case Tag.END:
				match(Tag.END);
				code.emitLabel(lendCondition);
				break;
			default:
				error("unclosed OPTION");
			}
			break;
		case '{':
			match('{');
			statlist(labelAttuale);
			match('}');
			break;
		default:
			error("not valid stat");
		}
    }

    private void idlist(int op){
		//Register the identifiers not yet registered in the Symbol Table.
		if(look.tag == Tag.ID){
			int address = st.lookupAddress(look);
			if (address == -1) {
				st.insert(((Word)look).lexeme,count);
				address = count++;
			}
			code.emit(OpCode.istore,address);
			match(Tag.ID);
			if (op == Tag.READ) {
				code.emit(OpCode.invokestatic,0);//invokestatic READ
			}
			if (look.tag == ',' && look.tag == Tag.ASSIGN) {
				code.emit(OpCode.iload,address);
			}
		}else{
			error("is not a identifier.");
		}

		//FOLLOW(idlist) = {',ID',';',']','}','$'}
		idlistp(op);
    }

    private void idlistp(int op){
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
			break;
		default:
			error("no identifier was found P .");
		}
    }

    private void optlist(int lendCondition){
		//GUIDA(optilist) = {'option',']'}
		if(look.tag==Tag.OPTION){
			int lnext = code.newLabel();
			optitem(lnext,lendCondition);
			optlistp(lendCondition);
		}else{
			error("unexpected OPTION inside option list.");
		}

		//FOLLOW(optlist)
		optlistp(lendCondition);
    }

	private void optlistp(int lendCondition){
		//FOLLOW(optlist) = {'option',']'}
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
		int ltrue = code.newLabel();
		match(Tag.OPTION);
		match('(');
		bexpr(ltrue);
		match(')');
		code.emit(OpCode.GOto,lnext);
		code.emitLabel(ltrue);
		match(Tag.DO);
		stat(lnext);
		code.emit(OpCode.GOto,lendCondition);
		code.emitLabel(lnext);
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
			error("Is not a relational operator");
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
