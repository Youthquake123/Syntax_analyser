public abstract class AbstractGenerate
{
    /**
     *Print the token in the output file
     */
    public void insertTerminal( Token token ) {
        String tt = Token.getName( token.symbol );
        
        if( (token.symbol == Token.identifier) || (token.symbol == Token.numberConstant) || (token.symbol == Token.stringConstant) )
            tt += " '" + token.text + "'";

        System.out.println( "RDPTOKEN " + tt );
    }

    public void commenceNonterminal( String name ) {
        System.out.println( "RDPBEGIN " + name );
    }

    public void finishNonterminal( String name ) {
        System.out.println( "RDPEND " + name );
    }

    /**
    * Report success information
    **/
    public void reportSuccess()
    {
        System.out.println( "RDPSUCCESS" );
    }

    /**
     * Report an error to the user
     */
    public abstract void reportError( Token token, String explanatoryMessage ) throws CompilationException;

}
