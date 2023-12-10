public class Generate extends AbstractGenerate{
    /**
     * report error that sends the line of error and what token is expected and found. Also loads to res.txt
     * @throws CompliationException
     */
    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        throw new CompilationException(explanatoryMessage);
    }
}