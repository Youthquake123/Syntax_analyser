public class Generate extends AbstractGenerate{
    /**
     * Report Error
     * @param token
     * @param explanatoryMessage
     * @throws CompilationException
     */
    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        throw new CompilationException(explanatoryMessage);
    }
}
