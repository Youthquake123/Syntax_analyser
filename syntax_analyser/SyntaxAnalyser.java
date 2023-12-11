import java.io.*;

public class SyntaxAnalyser {
    Token_extract Te ;
    Token nextToken ;
    Generate myGenerate = null;
    String fileName;

    /**
     * returns an error string
     * @param expected
     * @param next
     * @return
     */
    public String ErrorString(String expected, Token next){
        return " In " + this.fileName + ": - Expected token(s): " + expected + " but found: (' " + next.text + " ').";
    }

    /**
     * returns an error string for non-terminal error
     * @param nonTerminal
     * @param token
     * @return
     */
    public String non_terminal_error(String nonTerminal, Token token){
        return " In " + this.fileName + ": - Parsing error : " + nonTerminal;
    }

    /**
     * load file to token_extract
     * @param fileName
     */
    public SyntaxAnalyser(String fileName){
        this.fileName = fileName;
        try {
             Te = new Token_extract(fileName);
        } catch (IOException e) {
            System.out.println("Cant load token_extract");
        }
    }

    /**
     * statement part := Begin <statement list> end
     * @throws IOException
     * @throws CompilationException
     */
	public void _statementPart_() throws IOException, CompilationException {
        try {
            myGenerate.commenceNonterminal("StatementPart");
            acceptTerminal(Token.beginSymbol);
            try {
                statementList();
            } catch (CompilationException e) {
                throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
            }   
            acceptTerminal(Token.endSymbol);
            myGenerate.finishNonterminal("StatementPart");
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement part ", nextToken), e);
        } 
    }

    /**
     * accept next token and generate the structure of the syntax in output
     * @param symbol
     * @throws IOException
     * @throws CompilationException
     */
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        if(nextToken.symbol == symbol){
            myGenerate.insertTerminal(nextToken);
            nextToken = Te.getNextToken();
        }
        else{
            myGenerate.reportError(nextToken, ErrorString(" ' " + Token.getName(symbol) + " ' ", nextToken));
        }
    }

    /**
     * statement list := <statement> | <statement list> ; <statement>
      * @throws IOException
     * @throws CompilationException
     */
    public void statementList() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("StatementList");
        try {
            statement();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement ", nextToken), e);
        }
        while(nextToken.symbol == Token.semicolonSymbol){
            acceptTerminal(Token.semicolonSymbol);
            try {
                statementList();
            } catch (CompilationException e) {
                throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
            }  
        }
        myGenerate.finishNonterminal("StatementList");
    }

    /**
     * statement := <assignment statement> |
     * <if statement> |
     * <while statement> |
     * <procedure statement> |
     * <until statement> |
     * <for statement>
     * @throws IOException
     * @throws CompilationException
     */
    public void statement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("Statement");
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
        myGenerate.finishNonterminal("Statement");
    }

    /**
     * <assignment statement> := indentifier := <expression> | identifier := stringConstant
     * @throws IOException
     * @throws CompilationException
     */
    public void assignmentStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("AssignmentStatement");
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.becomesSymbol);
        if (nextToken.symbol == Token.stringConstant){
            acceptTerminal(Token.stringConstant);
        }
        else{
            try {
                expression();
            } catch (CompilationException e) {
                throw new CompilationException(non_terminal_error(" expression ", nextToken), e);
            }   
        }
        myGenerate.finishNonterminal("AssignmentStatement");
    }

    /**
     * <if statement> := if <condition> then <statement list> end if |
     * if <condition> then <statement list> else <statement list> end if
     * @throws IOException
     * @throws CompilationException
     */
    public void ifStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("IfStatement");
        acceptTerminal(Token.ifSymbol);
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" condition ", nextToken), e);
        }
        acceptTerminal(Token.thenSymbol);
        try {
            statementList();
            if (nextToken.symbol == Token.elseSymbol){
                acceptTerminal(Token.elseSymbol);
                statementList();
            }
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
        }
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);
        myGenerate.finishNonterminal("IfStatement");
    }

    /**
     * while statement := while <condition> loop <statement list> end loop
     * @throws IOException
     * @throws CompilationException
     */
    public void whileStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("WhileStatement");
        acceptTerminal(Token.whileSymbol);
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" condition ", nextToken), e);
        }
        acceptTerminal(Token.loopSymbol);
        try {
            statementList();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
        }
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        myGenerate.finishNonterminal("WhileStatement");
    }

    /**
     * procedure statement := call identifier ( <argument list> )
     * @throws IOException
     * @throws CompilationException
     */
    public void procedureStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("ProcedureStatement");
        acceptTerminal(Token.callSymbol);
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.leftParenthesis);
        try {
            argumentList();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" argument list ", nextToken), e);
        }
        acceptTerminal(Token.rightParenthesis);
        myGenerate.finishNonterminal("ProcedureStatement");
    }

    /**
     * until statement := do <statement list> until <condition>
     * @throws IOException
     * @throws CompilationException
     */
    public void untilStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("UntilStatement");
        acceptTerminal(Token.doSymbol);
        try {
            statementList();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
        }
        acceptTerminal(Token.untilSymbol);
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" condition ", nextToken), e);
        }
        myGenerate.finishNonterminal("UntilStatement");
    }

    /**
     * for statement := for ( <assignment statement> ; <condition> ; assignment statement> ) do <statement list> , indentifier
     * @throws IOException
     * @throws CompilationException
     */
    public void forStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("ForStatement");
        acceptTerminal(Token.forSymbol);
        acceptTerminal(Token.leftParenthesis);
        try {
            assignmentStatement();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" assignment statement ", nextToken), e);
        }
        acceptTerminal(Token.semicolonSymbol);
        try {
            condition();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" condition ", nextToken), e);
        }
        acceptTerminal(Token.semicolonSymbol);
        try {
            assignmentStatement();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" assignment statement ", nextToken), e);
        }
        acceptTerminal(Token.rightParenthesis);
        acceptTerminal(Token.doSymbol);
        try {
            statementList();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" statement list ", nextToken), e);
        }
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        myGenerate.finishNonterminal("ForStatement");
    }

    /**
     * argument list := identifier | <argument list> , identifier
     * @throws IOException
     * @throws CompilationException
     */
    public void argumentList() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("ArgumentList");
        acceptTerminal(Token.identifier);
        while (nextToken.symbol == Token.commaSymbol){
            acceptTerminal(Token.commaSymbol);
            try {
                argumentList();
            } catch (CompilationException e) {
                throw new CompilationException(non_terminal_error(" argument list ", nextToken), e);
            }   
        }
        myGenerate.finishNonterminal("ArgumentList");
    }

    /**
     * condition := identifer <conditional operator> identifier |
     * identifier <conditional operator> numberConstant |
     * identifier <conditional operator> stringConstant
     * @throws IOException
     * @throws CompilationException
     */
    public void condition() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("Condition");
        acceptTerminal(Token.identifier);
        try {
            conditionalOperator();  
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" conditional operator ", nextToken), e);
        }
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
        myGenerate.finishNonterminal("Condition");

    }

    /**
     * conditional operator := > | >= | = | /= | < | <=
     * @throws IOException
     * @throws CompilationException
     */
    public void conditionalOperator() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("ConditionalOperator");
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
        myGenerate.finishNonterminal("ConditionalOperator");
    }

    /**
     * expression := <term> | <expression> + <term> | <expression> - <term>
     * @throws IOException
     * @throws CompilationException
     */
    public void expression() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("Expression");
        try {
            term();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" term ", nextToken), e);
        }
        while(nextToken.symbol == Token.plusSymbol || nextToken.symbol == Token.minusSymbol){
            if (nextToken.symbol == Token.plusSymbol){
                acceptTerminal(Token.plusSymbol);
                try {
                    expression();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" expression ", nextToken), e);
                }
            }
            if (nextToken.symbol == Token.minusSymbol){
                acceptTerminal((Token.minusSymbol));
                try {
                    expression();
                } catch (CompilationException e) {
                    throw new CompilationException(non_terminal_error(" expression ", nextToken), e);
                }
            }
        }
        myGenerate.finishNonterminal("Expression");
    }

    /**
     * term := <factor> | <term> * <factor> | <term> / <factor>
     * @throws IOException
     * @throws CompilationException
     */
    public void term() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("Term");
        try {
            factor();
        } catch (CompilationException e) {
            throw new CompilationException(non_terminal_error(" factor ", nextToken), e);
        }
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
     * @throws IOException
     * @throws CompilationException
     */
    public void factor() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("Factor");
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

    /**
     * Use printstream to print the output into the file
     * @param ps
     * @throws IOException
     */
    public void parse( PrintStream ps ) throws IOException
    {
        myGenerate = new Generate();
        try {
            nextToken = Te.getNextToken() ;
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
