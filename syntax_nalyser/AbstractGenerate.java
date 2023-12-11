public abstract class AbstractGenerate
{

    /**
    *
    * insertTerminal
    *
    **/

    public void insertTerminal( Token token ) {
        String tt = Token.getName( token.symbol );
        
        if( (token.symbol == Token.identifier) || (token.symbol == Token.numberConstant) || (token.symbol == Token.stringConstant) )
            tt += " '" + token.text + "'";

        System.out.println( "RDPTOKEN " + tt );
    } // end of method insertTerminal

    /**
    *
    * commenceNonterminal
    *
    **/

    public void commenceNonterminal( String name ) {
        System.out.println( "RDPBEGIN " + name );
    } // end of method commenceNonterminal

    /**
    *
    * finishNonterminal
    *
    **/

    public void finishNonterminal( String name ) {
        System.out.println( "RDPEND " + name );
    } // end of method finishNonterminal

    /**
    *
    * reportSuccess
    *
    **/

    public void reportSuccess()
    {
        System.out.println( "RDPSUCCESS" );
    } // end of method reportSuccess


    /** Report an error to the user. */
    public abstract void reportError( Token token, String explanatoryMessage ) throws CompilationException;

} // end of class "AbstractGenerate"
