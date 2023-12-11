/**
 *
 * Represents a lexical token for 312 exercise. 
 * 
 * This class has been provided to students
 *
 * @Author: Roger Garside, John Mariani, John Vidler, Paul Rayson
 *
 *
 **/
public class Token
{

	public static final int ifSymbol = 1;
	public static final int elseSymbol = 2 ;
	public static final int thenSymbol = 3 ;
	public static final int doSymbol = 4 ;
	public static final int whileSymbol = 5 ;
	public static final int loopSymbol = 6 ;
	public static final int forSymbol = 7 ;
	public static final int untilSymbol = 8 ;
	public static final int beginSymbol = 9 ;
	public static final int endSymbol = 10 ;
	public static final int procedureSymbol = 11 ;
	public static final int callSymbol = 12 ;

	// Operators
	public static final int plusSymbol = 13 ;
	public static final int minusSymbol = 14 ;
	public static final int timesSymbol = 15 ;
	public static final int divideSymbol = 16 ;
	public static final int equalSymbol = 17 ;
	public static final int notEqualSymbol = 18 ;
	public static final int lessThanSymbol = 19 ;
	public static final int greaterThanSymbol = 20 ;
	public static final int lessEqualSymbol = 21 ;
	public static final int greaterEqualSymbol = 22 ;

	// Symbol Types
	public static final int identifier = 23 ;
	public static final int numberConstant = 24 ;
	public static final int stringConstant = 25 ;
	public static final int floatSymbol = 26 ;
	public static final int integerSymbol = 27 ;
	public static final int stringSymbol = 28 ;

	// Miscellaneous
	public static final int becomesSymbol = 29 ;
	public static final int colonSymbol = 30 ;
	public static final int commaSymbol = 31 ;
	public static final int leftParenthesis = 32 ;
	public static final int rightParenthesis = 33 ;
	public static final int semicolonSymbol = 34 ;
	public static final int eofSymbol = 35 ; // End of File
	public static final int isSymbol = 36 ;
	public static final int errorSymbol = 37 ; // Error

	// Corresponding to Control Structures
	private static final String[] names = {
			"if",                 // ifSymbol = 1
			"else",               // elseSymbol = 2
			"then",               // thenSymbol = 3
			"do",                 // doSymbol = 4
			"while",              // whileSymbol = 5
			"loop",               // loopSymbol = 6
			"for",                // forSymbol = 7
			"until",              // untilSymbol = 8
			"begin",              // beginSymbol = 9
			"end",                // endSymbol = 10
			"procedure",          // procedureSymbol = 11
			"call",               // callSymbol = 12

			// Corresponding to Operators
			"+",                  // plusSymbol = 13
			"-",                  // minusSymbol = 14
			"*",                  // timesSymbol = 15
			"/",                  // divideSymbol = 16
			"=",                  // equalSymbol = 17
			"/=",                 // notEqualSymbol = 18
			"<",                  // lessThanSymbol = 19
			">",                  // greaterThanSymbol = 20
			"<=",                 // lessEqualSymbol = 21
			">=",                 // greaterEqualSymbol = 22

			// Corresponding to Symbol Types
			"IDENTIFIER",         // identifier = 23
			"NUMBER",             // numberConstant = 24
			"STRING",             // stringConstant = 25
			"float",              // floatSymbol = 26
			"integer",            // integerSymbol = 27
			"string",             // stringSymbol = 28

			// Corresponding to Miscellaneous
			":=",                 // becomesSymbol = 29
			":",                  // colonSymbol = 30
			",",                  // commaSymbol = 31
			"(",                  // leftParenthesis = 32
			")",                  // rightParenthesis = 33
			";",                  // semicolonSymbol = 34
			"EOF",                // eofSymbol = 35
			"is",                 // isSymbol = 36
			"ERROR",              // errorSymbol = 37
	};

	/** The symbol this token instance represents */
	public int symbol ;
	/** The original text. */
	public String text ;
	/** The line number of the original text in the source file. */
	public int lineNumber ;

	/** Constructs a new token with a given token type and line number.

	  @param s The type of symbol, typically as a class constant from Token.
	  @param t The original string recognised from the source file.
	 */
	public Token(int s, String t)
	{
		symbol = s ;
		text = t ;
	} // end of constructor method

	/** Returns a string representation of a symbol type.

	  @param i The value of a symbol, typically as a class constant from Token.
	  @return The name of this symbol.
	 */
	public static String getName(int i)
	{
		if ((i < 1) || (i > names.length))
			return "UNKNOWN" ;
		else
			return names[i - 1] ;
	} // end of method getName

	/** @see Object.toString */
	public String toString()
	{
		String tt = "(" ;
		tt += text + ", " + symbol + ")";
		return tt ;
	} // end of method toString
} // end of class Token

