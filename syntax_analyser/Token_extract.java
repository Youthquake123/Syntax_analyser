import java.io.* ;
import java.util.Scanner;

public class Token_extract
{
	private BufferedReader sourceFile ;
	String[] current_token;
	String text;
	String sy;
	private String currentLine ;

	/**
	 * Read tokens from output files from Lexical Analyser by lines
	 * @param fileName
	 * @throws IOException
	 */
	public Token_extract(String fileName) throws IOException
	{
		sourceFile = new BufferedReader(new FileReader(fileName)) ;
		currentLine = sourceFile.readLine() ;
	}

	/**
	 * Read next tokens, extract text from source code and identify the class of tokens
	 * @return
	 * @throws IOException
	 */
	public Token getNextToken() throws IOException
	{
		if (currentLine == null) {
			// Handle the end of file or initial state appropriately
			// For example, you might want to return a special end-of-file token
			// or throw an exception if the file has not been read properly
			return new Token(35, "EOF");}
		current_token = currentLine.split(", ");
		text = current_token[0].substring(1,current_token[0].length());
		sy = current_token[1].substring(0,current_token[1].length() - 1);
		currentLine = sourceFile.readLine();
		return new Token(Integer.parseInt(sy),text);
	}
}
