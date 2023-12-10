import java.io.IOException;
import java.io.PrintStream;

/**
 * SyntaxAnalyser which extends AbstractSyntaxAnalyser
 * Implemented a syntax analyser using recursive descent recogniser
 * 
 * @Author: Lincoln Kingsley Harrison
 * 
 */

public class SyntaxAnalyser {
    Token_extract lex ;
    /** A cache of the token to be processed next. */
    Token nextToken ;
    /** A code generator, descendant of AbstractGenerate. */
    Generate myGenerate = null;
    String fileName;
    
    /***
     * returns an error string for all tokens. 
     * @param expected is the expected value
     * @param next is the next token
     * @return
     */
    public String ErrorString(String expected, Token next){
        return " In " + this.fileName + ": - Expected token(s): " + expected + " but found: (' " + next.text + " ').";
    }
 
    /**
     * returns an error string for all non terminals.
     * @param nonTerminal is the name of the non terminal
     * @param token used to receive the line number of the error
     * @return
     */
    public String non_terminal_error(String nonTerminal, Token token){
        return " In " + this.fileName + ": - Parsing error : " + nonTerminal;
    }
    
    /**
     * @param fileName loads filename into lexical analyser
     */
    public SyntaxAnalyser(String fileName){
        this.fileName = fileName;
        try {
            lex = new Token_extract(fileName);
        } catch (IOException e) {
            System.out.println("Cant load lexical analyzer");
        }
    }
    
    /**
     * statement part := Begin <statement list> end 
     * @throws IOEXception 
     * @throws CompliationException
     */
	public void _statementPart_() throws IOException, CompilationException {
        //Try statement part, and if anything fails, then know it came from statement part. 
        try {
            //begins parsing
            myGenerate.commenceNonterminal("StatementPart");
            //using accept terminal to find begin. 
            acceptTerminal(Token.beginSymbol);
            //try to enter statementList
            try {
                statementList();
                //Compliation error to res.txt when error found. 
            } catch (CompilationException e) {
                throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
            }   
            acceptTerminal(Token.endSymbol);
            // parsing ends. 
            myGenerate.finishNonterminal("StatementPart");
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement part ", nextToken), e);
        } 
    }

    /**
     * Takes a token from parsing statement and compares it to what the next token in the text.
     * If its the same, it will accept the token. 
     * If not the same it will report an error with the syntax.
     * @throws IOEXception 
     * @throws CompliationException
     * @param symbol is the token number
     */
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        if(nextToken.symbol == symbol){
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        }
        else{
            myGenerate.reportError(nextToken, ErrorString(" ' " + Token.getName(symbol) + " ' ", nextToken));
        }
    }

    /**
     * statement list := <statement> | <statement list> ; <statement>
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void statementList() throws IOException, CompilationException{
        // start parsing through statement list
        myGenerate.commenceNonterminal("StatementList");
        try {
            // try catch to look through statement 
            statement();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement ", nextToken), e);
        }  
        //see if there is a semi colon
        while(nextToken.symbol == Token.semicolonSymbol){
            // accept semi colon
            acceptTerminal(Token.semicolonSymbol);
            try {
                statementList();
            } catch (CompilationException e) {
                throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
            }  
        }
        // finish parsing throguh statement list
        myGenerate.finishNonterminal("StatementList");
    }

    /**
     * statement := <assignment statement> | 
     * <if statement> | 
     * <while statement> |
     * <procedure statement> |
     * <until statement> |
     * <for statement>
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void statement() throws IOException, CompilationException{
        // start parsing through statement
        myGenerate.commenceNonterminal("Statement");
        //switch statement to see if the next token meets one of the cases. If so look through that statement.
        // use a try catch for every case to see which specific statement doesnt go through if it fails. 
        switch(nextToken.symbol){
            case Token.identifier:
                try {
                    assignmentStatement();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" assignment statement ", nextToken), e);
                }  
                break;
            case Token.ifSymbol:
                try {
                    ifStatement();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" if statement ", nextToken), e);
                }  
                break;
            case Token.whileSymbol:
                try {
                    whileStatement();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" while statement ", nextToken), e);
                }  
                break;
            case Token.callSymbol:
                try {
                    procedureStatement();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" procedure statement ", nextToken), e);
                }  
                break;
            case Token.untilSymbol:
                try {
                    untilStatement();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" until statement ", nextToken), e);
                }  
                break;
            case Token.forSymbol:
                try {
                    forStatement();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" for statement ", nextToken), e);
                } 
                break;     
            default:
                myGenerate.reportError(nextToken, ErrorString(" ' if ' , ' assignment ' , ' until ', ' while ' or ' procedure ' ", nextToken));
        }
        // finish parsing statement
        myGenerate.finishNonterminal("Statement");
    }

    /**
     * <assignment statement> := indentifier := <expression> | identifier := stringConstant
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void assignmentStatement() throws IOException, CompilationException{
        // start parsing through assignment statement
        myGenerate.commenceNonterminal("AssignmentStatement");
        // see if identifier is accepted
        acceptTerminal(Token.identifier);
        // see if become symbol is accepted
        acceptTerminal(Token.becomesSymbol);
        // see if string Constant exists 
        if (nextToken.symbol == Token.stringConstant){
            acceptTerminal(Token.stringConstant);
        }
        else{
            try {
                //try expression statement.
                expression();
            } catch (CompilationException e) {
                throw new CompilationException(non_terminal_error(" expression ", nextToken), e);
            }   
        }
        // finish parsing assignment statement
        myGenerate.finishNonterminal("AssignmentStatement");
    }

    /**
     * <if statement> := if <condition> then <statement list> end if | 
     * if <condition> then <statement list> else <statement list> end if
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void ifStatement() throws IOException, CompilationException{
        //start parsing through if statement
        myGenerate.commenceNonterminal("IfStatement");
        //see if 'if' symbol exists
        acceptTerminal(Token.ifSymbol);
        try {
            // try catch through condition
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" condition ", nextToken), e);
        }   
        // see if then symbol exists
        acceptTerminal(Token.thenSymbol);
        try {
            // try catch through statementList
            statementList();
            //if else symbol exists then accept it
            if (nextToken.symbol == Token.elseSymbol){
                acceptTerminal(Token.elseSymbol);
                //try catch through statementList
                statementList();
            }
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
        }  
        // see if end symbol exists
        acceptTerminal(Token.endSymbol);
        // see if 'if' symbol exists
        acceptTerminal(Token.ifSymbol);
        // finish parsing if statement
        myGenerate.finishNonterminal("IfStatement");
    }

    /**
     * while statement := while <condition> loop <statement list> end loop
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void whileStatement() throws IOException, CompilationException{
        // start parsing looking through while statement
        myGenerate.commenceNonterminal("WhileStatement");
        // see if while symbol exists
        acceptTerminal(Token.whileSymbol);
        // try catch through condition statement
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" condition ", nextToken), e);
        } 
        // see if loop symbol exists
        acceptTerminal(Token.loopSymbol);
        // try catch through statementList
        try {
            statementList();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
        }   
        // see if end symbol exists
        // see if loop symbol exists
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        // finish parsing while statement
        myGenerate.finishNonterminal("WhileStatement");
    }

    /**
     * procedure statement := call identifier ( <argument list> )
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void procedureStatement() throws IOException, CompilationException{
        // start parsing procedure statement 
        myGenerate.commenceNonterminal("ProcedureStatement");
        // see if call symbol exists 
        acceptTerminal(Token.callSymbol);
        // see if identifier symbol exists
        acceptTerminal(Token.identifier);
        // see if left parenthesis exists
        acceptTerminal(Token.leftParenthesis);
        // try catch through argument List
        try {
            argumentList();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" argument list ", nextToken), e);
        }   
        // see if right parenthesis exists
        acceptTerminal(Token.rightParenthesis);
        // finish parsing procedure statement
        myGenerate.finishNonterminal("ProcedureStatement");
    }

    /**
     * until statement := do <statement list> until <condition>
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void untilStatement() throws IOException, CompilationException{
        // start parsing through until statement 
        myGenerate.commenceNonterminal("UntilStatement");
        // see if do symbol exists 
        acceptTerminal(Token.doSymbol);
        // try catch through statement list
        try {
            statementList();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
        }   
        // see if until symbol exists
        acceptTerminal(Token.untilSymbol);
        // try catch through condition
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" condition ", nextToken), e);
        } 
        // finish parsing through until statement
        myGenerate.finishNonterminal("UntilStatement");
    }
    /**
     * for statement := for ( <assignment statement> ; <condition> ; assignment statement> ) do <statement list> , indentifier
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void forStatement() throws IOException, CompilationException{
        // start parsing through for statement
        myGenerate.commenceNonterminal("ForStatement");
        // see if for symbol exists
        acceptTerminal(Token.forSymbol);
        // see if left parenthesis exists
        acceptTerminal(Token.leftParenthesis);
        // try catch through assignment statement
        try {
            assignmentStatement();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" assignment statement ", nextToken), e);
        } 
        // see if semi colon exists 
        acceptTerminal(Token.semicolonSymbol);
        // try catch through condition statement
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" condition ", nextToken), e);
        } 
        // see if semi colon exists 
        acceptTerminal(Token.semicolonSymbol);
        // try catch through assignment statement
        try {
            assignmentStatement();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" assignment statement ", nextToken), e);
        } 
        // see if right parenthesis symbol exists
        acceptTerminal(Token.rightParenthesis);
        // see if do symbol exists
        acceptTerminal(Token.doSymbol);
        // try catch through statement list
        try {
            statementList();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
        }   
        // see if end symbol exists
        acceptTerminal(Token.endSymbol);
        // see if loop symbol exists
        acceptTerminal(Token.loopSymbol);
        // finish parsing through for statement
        myGenerate.finishNonterminal("ForStatement");
    }

    /**
     * argument list := identifier | <argument list> , identifier
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void argumentList() throws IOException, CompilationException{
        // start parsing through argument list
        myGenerate.commenceNonterminal("ArgumentList");
        // see if identifier symbol exists
        acceptTerminal(Token.identifier);
        //see if a comma exists in the code and then accepts comma symbol
        while (nextToken.symbol == Token.commaSymbol){
            acceptTerminal(Token.commaSymbol);
            // try catch through argument list
            try {
                argumentList();
            } catch (CompilationException e) {
                throw new CompilationException(non_terminal_error(" argument list ", nextToken), e);
            }   
        }
        // finish parsing through argument list
        myGenerate.finishNonterminal("ArgumentList");
    }

    /**
     * condition := identifer <conditional operator> identifier | 
     * identifier <conditional operator> numberConstant |
     * identifier <conditional operator> stringConstant
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void condition() throws IOException, CompilationException{
        // start parsing through condition 
        myGenerate.commenceNonterminal("Condition");
        // see if identifier symbol exists
        acceptTerminal(Token.identifier);
        // try catch to go through conditional operator
        try {
            conditionalOperator();  
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" conditional operator ", nextToken), e);
        }
        // switch statement to see which case is the next token
        switch(nextToken.symbol){
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal((Token.numberConstant));
                break;
            case Token.stringConstant:
                acceptTerminal(Token.stringConstant);
                break;
            default:
                myGenerate.reportError(nextToken, ErrorString(" < identifer > , < number constant > or < string constant > ", nextToken));

        }
        // finish condition
        myGenerate.finishNonterminal("Condition");

    }

    /**
     * conditional operator := > | >= | = | /= | < | <=
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void conditionalOperator() throws IOException, CompilationException{
        // start parsing through conditional operator
        myGenerate.commenceNonterminal("ConditionalOperator");
        // switch statement to see which case will be the next symbol it will then accept that token symbol 
        switch(nextToken.symbol){
            case Token.greaterThanSymbol:
                acceptTerminal(Token.greaterThanSymbol);
                break;
            case Token.greaterEqualSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.equalSymbol:
                acceptTerminal(Token.equalSymbol);
                break;
            case Token.notEqualSymbol:
                acceptTerminal(Token.notEqualSymbol);
                break;
            case Token.lessThanSymbol:
                acceptTerminal(Token.lessThanSymbol);
                break;
            case Token.lessEqualSymbol:
                acceptTerminal(Token.lessEqualSymbol);
                break;
            default:
                myGenerate.reportError(nextToken, ErrorString("  ' > '  , ' >= ' , ' = ' , ' /= ' , ' < ' or ' <= ' ", nextToken));
        }
        // finish conditional operator
        myGenerate.finishNonterminal("ConditionalOperator");
    }

    /**
     * expression := <term> | <expression> + <term> | <expression> - <term>
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void expression() throws IOException, CompilationException{
        // start expression condition
        myGenerate.commenceNonterminal("Expression");
        // try catch through term 
        try {
            term();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" term ", nextToken), e);
        }   
        // see if plus or mius symbol exists 
        while(nextToken.symbol == Token.plusSymbol || nextToken.symbol == Token.minusSymbol){
            // if plus symbol, then accept that token and go through expression. 
            if (nextToken.symbol == Token.plusSymbol){
                acceptTerminal(Token.plusSymbol);
                try {
                    expression();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" expression ", nextToken), e);
                }
            }
            // if minus symbol, then accept that token and go through expression. 
            if (nextToken.symbol == Token.minusSymbol){
                acceptTerminal((Token.minusSymbol));
                try {
                    expression();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" expression ", nextToken), e);
                }
            }
        }
        // finish expression
        myGenerate.finishNonterminal("Expression");
    }

    /**
     * term := <factor> | <term> * <factor> | <term> / <factor> 
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void term() throws IOException, CompilationException{
        // start term statement
        myGenerate.commenceNonterminal("Term");
        // try catch through factor
        try {
            factor();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" factor ", nextToken), e);
        }
        // see if times or divide exists, and if it does accept the correct symbol and then go through the term statement
        while(nextToken.symbol == Token.timesSymbol|| nextToken.symbol == Token.divideSymbol){
            if (nextToken.symbol == Token.timesSymbol){
                acceptTerminal(Token.timesSymbol);
                try {
                    term();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" term ", nextToken), e);
                }
            }
            if (nextToken.symbol == Token.divideSymbol){
                acceptTerminal((Token.divideSymbol));
                try {
                    term();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" term ", nextToken), e);
                }
            }
        }
        myGenerate.finishNonterminal("Term");
    }
    /**
     * factor := identifer | numberConstant | ( <expression> )
     * @throws IOEXception 
     * @throws CompliationException
     */
    public void factor() throws IOException, CompilationException{
        // start factor statement
        myGenerate.commenceNonterminal("Factor");
        // switch cases to see which token is next. If not, default case a report error. 
        switch(nextToken.symbol){
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.leftParenthesis:
                acceptTerminal(Token.leftParenthesis);
                try {
                    expression();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" expression ", nextToken), e);
                }
                acceptTerminal(Token.rightParenthesis);
                break;
            default:
                myGenerate.reportError(nextToken, ErrorString(" ' identifier ' , ' number constant ' , ' ( ' , ' ) ' ", nextToken));
        }
        myGenerate.finishNonterminal("Factor");
    }
    public void parse( PrintStream ps ) throws IOException
    {
        myGenerate = new Generate();
        try {
            nextToken = lex.getNextToken() ;
            _statementPart_() ;
            acceptTerminal(Token.eofSymbol) ;
            myGenerate.reportSuccess() ;
        }
        catch( CompilationException ex )
        {
            ps.println( "Compilation Exception" );
            ps.println( ex.toTraceString() );
        }
    }
}