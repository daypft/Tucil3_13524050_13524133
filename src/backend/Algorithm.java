package backend;

public abstract class Algorithm {
    private StringBuilder logger = new StringBuilder();

    public abstract Result solve(Board board);

    protected void clearLog() {
        logger.setLength(0);
    }

    protected void log(String message) {
        logger.append(message).append(System.lineSeparator());
    }

    public String getLog() {
        return logger.toString();
    }
}
