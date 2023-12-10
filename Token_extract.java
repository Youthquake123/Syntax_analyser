
/**
 *
 * lexical analyser for 312 exercise.
 *
 * This class has been provided to students
 *
 * Author: Roger Garside, John Mariani, John Vidler, Paul Rayson
 *
 *
 **/
import java.io.* ;
import java.util.Scanner;

public class Token_extract
{

	/** Represents a textual and symbolic reserved word. */
	class ReservedWord
	{
		/** The text used in source. */
		public String text ;
		/** The type (one of the class constants from Token) of this word. */
		public int symbol ;

		/** Creates a new reserved word from the observed text and a given type.

		  @param t The text as seen in source.
		  @param s The type of this word, typically a class constant from Token
		 */
		public ReservedWord(String t, int s)
		{
			text = t ;
			symbol = s ;
		} // end of constructor method
	} // end of class ReservedWord

	/** The maximum number of identifiers to hold during compilation. */
	private static final int maxTableSize = 200 ;
	/** The EOF character. */
	private static final char EOF = '\000' ;

	/** A list of symbols. */
	private ReservedWord[] symbols = new ReservedWord[maxTableSize] ;
	/** Holds the current number of symbols held. */
	private int noOfSymbols ;

	/** Is this the first time we have been called? */
	private boolean firstCall ;

	/** An input stream from the filename mentioned above. */
	private BufferedReader sourceFile ;

	String[] current_token;

	String ct = "1";
	int current_number = 0;

	/* State-change character and offset counts. */
	private char currentCharacter ;
	private String currentLine ;
	private int currentOffset;

	/* input buffer */
	private StringBuffer currentText = new StringBuffer() ;

	/** Adds a reserved word to the internal symbol table.

	  @param t The text as seen in source.
	  @param s The type of this word, typically a class constant from Token
	 */
	private void setReservedWord(String t, int s)
	{
		symbols[noOfSymbols] = new ReservedWord(t, s) ;
		noOfSymbols++ ;
	} // end of method setReservedWord


	/** Sets all initial variables and adds the language's reserved words to the symbol table. */
	private void initialiseScanner()
	{
		noOfSymbols = 0 ;
		setReservedWord("begin", Token.beginSymbol) ;
		setReservedWord("call", Token.callSymbol) ;
		setReservedWord("do", Token.doSymbol) ;
		setReservedWord("else", Token.elseSymbol) ;
		setReservedWord("end", Token.endSymbol) ;
		setReservedWord("float", Token.floatSymbol) ;
		setReservedWord("if", Token.ifSymbol) ;
		setReservedWord("integer", Token.integerSymbol) ;
		setReservedWord("is", Token.isSymbol) ;
		setReservedWord("loop", Token.loopSymbol) ;
		setReservedWord("procedure", Token.procedureSymbol) ;
		setReservedWord("string", Token.stringSymbol) ;
		setReservedWord("then", Token.thenSymbol) ;
		setReservedWord("until", Token.untilSymbol) ;
		setReservedWord("while", Token.whileSymbol) ;
		setReservedWord("for", Token.forSymbol) ;
	} // end of method initialiseScanner


	/** Creates a new LexicalAnalyser which will run over the given file.

	  @param fileName The file to read.
	  @throws IOException if any read errors occur during parsing.
	 */
	public Token_extract(String fileName) throws IOException
	{
		initialiseScanner() ;

		sourceFile = new BufferedReader(new FileReader(fileName)) ;
		currentLine = sourceFile.readLine() ;
		currentOffset = 0 ;
		firstCall = true ;
	} // end of constructor method

	/** Loads the next character of the input into the buffer.

	  @throws IOException in the event that something like velociraptor attack happens to the input stream.
	 */
	private void getNextCharacter() throws IOException {
		if (currentLine == null)
			currentCharacter = EOF;
		else if (currentOffset >= ct.length()) {
			currentLine = sourceFile.readLine();
			currentOffset = 0;
			currentCharacter = '\n';
		} else {
			current_token = currentLine.split(" ");
			ct = current_token[2];
			if (currentOffset < ct.length()) {
				currentCharacter = ct.charAt(currentOffset);
				currentOffset++;
			}
		}
	}
//		if (currentLine == null)
//			currentCharacter = EOF ;
//		else if (currentOffset >= currentLine.length())
//		{
//			currentLine = sourceFile.readLine() ;
//			currentOffset = 0 ;
//			currentCharacter = '\n' ;
//		}
//		else
//		{
//			currentCharacter = currentLine.charAt(currentOffset) ;
//			currentOffset++ ;
//		}
//	}
	// end of method getNextCharacter
	/** Returns the next token from the source file.  Repeatedly calling this
	  will return each token in the file, and eventually null.

	  @throws IOException in the event that the file cannot be read.
	  @return the next token from the source file.
	 */
	public Token getNextToken() throws IOException
	{
		if (firstCall)
		{
			getNextCharacter() ;
			firstCall = false ;
		}

		while ((currentCharacter == ' ') || (currentCharacter == '\t') ||
				(currentCharacter == '\n') || (currentCharacter == '-'))
		{
			if (currentCharacter == '-')
			{
				getNextCharacter() ;
				if (currentCharacter == '-')
				{
					while (currentCharacter != '\n')
						getNextCharacter() ;
				}
				else
					return new Token(Token.minusSymbol, "-") ;
			}

			getNextCharacter() ;
		}

		if (Character.isLetter(currentCharacter))
		{
			currentText.setLength(0) ;
			while ((Character.isLetter(currentCharacter)) ||
					(Character.isDigit(currentCharacter)))
			{
				currentText.append(currentCharacter) ;
				getNextCharacter() ;
			}

			int i = 0 ;
			String t = (new String(currentText)).toLowerCase() ;
			while ((i < noOfSymbols) &&
					(!t.equals(symbols[i].text)))
				i++ ;

			if (i < noOfSymbols)
				return new Token(symbols[i].symbol, currentText) ;
			else
				return new Token(Token.identifier, currentText) ;
		}
		else if (Character.isDigit(currentCharacter))
		{
			currentText.setLength(0);
			while (Character.isDigit(currentCharacter))
			{
				currentText.append(currentCharacter) ;
				getNextCharacter() ;
			}
			if (currentCharacter == '.')
			{
				currentText.append(currentCharacter) ;
				getNextCharacter() ;
				while (Character.isDigit(currentCharacter))
				{
					currentText.append(currentCharacter) ;
					getNextCharacter() ;
				}
			}
			return new Token(Token.numberConstant, currentText) ;
		}
		else if (currentCharacter == '"')
		{
			getNextCharacter() ;
			currentText.setLength(0) ;
			while (currentCharacter != '"')
			{
				currentText.append(currentCharacter) ;
				getNextCharacter() ;
			}
			getNextCharacter() ;
			return new Token(Token.stringConstant, currentText) ;
		}
		else if (currentCharacter == ':')
		{
			getNextCharacter() ;
			if (currentCharacter == '=')
			{
				getNextCharacter() ;
				return new Token(Token.becomesSymbol, ":=") ;
			}
			else
				return new Token(Token.colonSymbol, ":") ;
		}
		else if (currentCharacter == '>')
		{
			getNextCharacter() ;
			if (currentCharacter == '=')
			{
				getNextCharacter() ;
				return new Token(Token.greaterEqualSymbol, ">=") ;
			}
			else
				return new Token(Token.greaterThanSymbol, ">") ;
		}
		else if (currentCharacter == '<')
		{
			getNextCharacter() ;
			if (currentCharacter == '=')
			{
				getNextCharacter() ;
				return new Token(Token.lessEqualSymbol, "<=") ;
			}
			else
				return new Token(Token.lessThanSymbol, "<") ;
		}
		else if (currentCharacter == '/')
		{
			getNextCharacter() ;
			if (currentCharacter == '=')
			{
				getNextCharacter() ;
				return new Token(Token.notEqualSymbol, "/=") ;
			}
			else
				return new Token(Token.divideSymbol, "/") ;
		}
		else if (currentCharacter == '=')
		{
			getNextCharacter() ;
			return new Token(Token.equalSymbol, "=") ;
		}
		else if (currentCharacter == ',')
		{
			getNextCharacter() ;
			return new Token(Token.commaSymbol, ",") ;
		}
		else if (currentCharacter == ';')
		{
			getNextCharacter() ;
			return new Token(Token.semicolonSymbol, ";") ;
		}
		else if (currentCharacter == '+')
		{
			getNextCharacter() ;
			return new Token(Token.plusSymbol, "+") ;
		}
		else if (currentCharacter == '*')
		{
			getNextCharacter() ;
			return new Token(Token.timesSymbol, "*") ;
		}
		else if (currentCharacter == '(')
		{
			getNextCharacter() ;
			return new Token(Token.leftParenthesis, "(") ;
		}
		else if (currentCharacter == ')')
		{
			getNextCharacter() ;
			return new Token(Token.rightParenthesis, ")") ;
		}
		else if (currentCharacter == EOF)
		{
			return new Token(Token.eofSymbol, "") ;
		}
		else
		{
			currentText = new StringBuffer(currentCharacter) ;
			getNextCharacter() ;
			return new Token(Token.errorSymbol, currentText) ;
		}
	} // end of method getNextToken

	/** Entry point to text Lexer */
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter a string: ");
		String fileName = scanner.nextLine();

//		BufferedReader din = new BufferedReader(new InputStreamReader(System.in)) ;
//		System.err.print("file? ") ;
//		System.err.flush() ;
//		String fileName = din.readLine().trim() ;

		Token_extract lex = new Token_extract(fileName);
		Token t;
		String filePath = "test_2.txt";
		try (FileWriter writer = new FileWriter(filePath)) {
			do {
				t = lex.getNextToken();
				writer.write(t.toString() + "\n");
			} while (t.symbol != Token.eofSymbol);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // end of main method
} // end of class LexicalAnalyser
