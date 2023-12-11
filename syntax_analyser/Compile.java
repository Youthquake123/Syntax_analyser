import java.io.* ;
import java.util.Scanner;
import java.util.Stack;

public class Compile {

	public static String fileName;

	private static final String INDENT = "    "; // 4 spaces for indentation

	private void run(String outputFile) throws IOException {
		System.out.println("Please choose the input file, 1: Correct test case, 2,3,4: Incorrect test case");
		Scanner input =new Scanner(System.in);
		int choose = input.nextInt();
		String flag = String.valueOf(choose);
		PrintStream out = null;
		boolean goon = true;

		try {
			if(choose==1){
				fileName = "test_right.txt";
			}
			if(choose==2){
				fileName = "test_wrong_1.txt";
			}
			if(choose==3) {
				fileName = "test_wrong_2.txt";
			}
			if(choose==4) {
				fileName = "test_wrong_3.txt";
			}
			out = new PrintStream( new FileOutputStream(outputFile) );
		} catch( Exception e ) {
			System.out.println("unable to open output file "+e);
			System.exit(0);
		}

		while( goon ) {
			goon = ((new File(fileName)).exists());
			if( goon ) {
				System.out.println();
				SyntaxAnalyser syn = new SyntaxAnalyser(fileName) ;
				System.setOut(out);
				System.out.println( "RDPSTART" );
				System.out.println( "RDPFILE " + fileName );
				syn.parse( out ) ;
				System.out.println() ;
				System.out.println("RDPFINISH") ;
				break;
			} else System.out.println(fileName+" does not exist");
		}

		out.flush();
		out.close();

		if (flag.matches("[1-4]")) {
			String outputFilePath = "output_" + flag + ".txt";
			convertToTree(outputFile, outputFilePath); // Convert to tree using the new file name

		} else {
			System.out.println("Invalid input. Please enter a number between 1 and 4.");
		}
	}

	/**
	 * Build the output file with tree structure
	 */
	public static void convertToTree(String filePath, String outputFilePath) {
		File inputFile = new File(filePath);
		File outputFile = new File(outputFilePath);

		Stack<String> stack = new Stack<>();
		int currentIndentation = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
			 PrintWriter out = new PrintWriter(new FileWriter(outputFile))) {

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("BEGIN")) {
					printWithIndentation(out, currentIndentation, line);
					stack.push(line);
					currentIndentation++;
				} else if (line.contains("END")) {
					if (!stack.isEmpty()) stack.pop();
					currentIndentation--;
					printWithIndentation(out, currentIndentation, line);
				} else {
					printWithIndentation(out, currentIndentation, line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adjust the output file with tree structure
	 */
	private static void printWithIndentation(PrintWriter out, int indentationLevel, String content) {
		for (int i = 0; i < indentationLevel; i++) {
			out.print(INDENT);
		}
		out.println(content.trim());
	}

	public static void main(String[] args) throws IOException, CompilationException {
		String outputFile = "output.txt";
		Compile c = new Compile();
		c.run(outputFile); // Use the output file name for the compilation process
		File fileToDelete = new File(outputFile);
		fileToDelete.delete(); // Delete the temporary file
	}
}
