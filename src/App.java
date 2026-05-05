import backend.Algorithm;
import backend.Board;
import backend.Board.Direction;
import backend.GBFS;
import backend.Parser;
import backend.Result;
import backend.Tile;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;

public class App extends Application {

    private int cellSize = 100;
    private int size = 1000;

    private Rectangle[][] cells;
    private Tile currentTile;
    private Timeline activeTimeline;

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(initScene(), size, size);

        stage.setTitle("Tucil 3");
        stage.setScene(scene);
        stage.show();
    }

    private Board loadBoard() {
        Parser parser = new Parser();
        String filePath = "data/1.txt";
        try {
            return parser.parseBoard(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BorderPane initScene() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        Board board = loadBoard();
        if (board == null) {
            return borderPane;
        }

        cellSize = Math.min(size / board.col, size / board.row);
        GridPane grid = createGrid(board);
        borderPane.setCenter(grid);

        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            Algorithm algorithm = new GBFS();
            Result result = algorithm.solve(board);
            animatePath(board, result);
        });

        borderPane.setBottom(startButton);

        return borderPane;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void animatePath(Board board, Result result) {
        if (activeTimeline != null) {
            activeTimeline.stop();
        }

        currentTile = board.start;
        renderBoard(board);

        Timeline timeline = new Timeline();
        Tile animationTile = board.start;
        int step = 1;

        for (Direction move : result.moves) {
            Tile next = nextTile(animationTile, move);

            while (next != null && !next.isWall()) {
                if (next.isLava()) {
                    next = null;
                    break;
                }

                Tile destination = next;
                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(50L * step), e -> {
                    currentTile = destination;
                    renderBoard(board);
                }));
                step++;

                animationTile = destination;

                if (destination.isGoal()) {
                    break;
                }

                next = nextTile(destination, move);
            }

            if (animationTile != null && animationTile.isGoal()) {
                break;
            }
        }

        activeTimeline = timeline;
        timeline.play();
    }

    private Tile nextTile(Tile tile, Direction direction) {
        if (tile == null) {
            return null;
        }

        return switch (direction) {
            case UP -> tile.nextUp();
            case DOWN -> tile.nextDown();
            case LEFT -> tile.nextLeft();
            case RIGHT -> tile.nextRight();
        };
    }

    private GridPane createGrid(Board board) {
        GridPane grid = new GridPane();
        cells = new Rectangle[board.row][board.col];

        for (int r = 0; r < board.row; r++) {
            for (int c = 0; c < board.col; c++) {
                Rectangle rect = new Rectangle(cellSize, cellSize, Color.web(backgroundColor(board.tiles[r][c])));
                Text label = new Text(board.tiles[r][c].order >= 0 ? String.valueOf(board.tiles[r][c].order) : "");
                label.setFill(Color.BLACK);
                label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: black;");

                StackPane cell = new StackPane(rect, label);

                cells[r][c] = rect;
                grid.add(cell, c, r);
            }
        }
        return grid;
    }

    private void renderBoard(Board board) {
        for (int r = 0; r < board.row; r++) {
            for (int c = 0; c < board.col; c++) {
                Tile tile = board.tiles[r][c];
                String color = backgroundColor(tile);

                if (tile == currentTile) {
                    color = "#ff006e";
                }

                cells[r][c].setFill(Color.web(color));
            }
        }
    }

    private String backgroundColor(Tile tile) {
        if (tile.isWall()) {
            return "#2f3640";
        }
        if (tile.isLava()) {
            return "#ffb084";
        }
        if (tile.isGoal()) {
            return "#ffd166";
        }
        if (tile.isStart()) {
            return "#87c38f";
        }
        if (tile.order >= 0) {
            return "#8ecae6";
        }
        return "#fffaf2";
    }

}
