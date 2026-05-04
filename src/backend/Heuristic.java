package backend;

public interface Heuristic {
    int estimate(Algorithm.SearchState state, Board board);
}
