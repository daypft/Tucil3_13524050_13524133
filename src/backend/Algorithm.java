package backend;

public abstract class Algorithm {
    private StringBuilder logger = new StringBuilder();
    private StringBuilder output = new StringBuilder();
    private long time;

    public abstract Result solve(Board board);

    protected long clearLog() {
        logger.setLength(0);
        time = 0;
        return System.nanoTime();
    }

    protected void clearOutput() {
        output.setLength(0);
    }

    protected Result finishRun(Result result, long start) {
        time = (System.nanoTime() - start) / 1_000_000;
        return result;
    }

    protected void log(String message) {
        logger.append(message).append(System.lineSeparator());
    }

    protected void printBoard(String title, Board board, Tile currentTile) {
        if (board == null) {
            return;
        }

        board.currentTile = currentTile;
        output.append(title).append(System.lineSeparator());
        output.append(board.printBoard()).append(System.lineSeparator());
    }

    public String getLog() {
        return logger.toString();
    }

    public String getOutput() {
        return output.toString();
    }

    public long getTime() {
        return time;
    }
}
