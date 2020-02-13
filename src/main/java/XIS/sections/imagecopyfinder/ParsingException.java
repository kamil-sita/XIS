package XIS.sections.imagecopyfinder;

public final class ParsingException extends RuntimeException {
    public ParsingException(String cause, String line, int lineId) {
        super("Parsing exception caused by " + cause + ", at line (" + lineId + "): " + System.lineSeparator() + line);
    }
}
