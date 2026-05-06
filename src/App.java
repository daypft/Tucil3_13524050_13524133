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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

public class App extends Application {

    private int cellSize = 100;
    private int sceneWidth = 1280;
    private int sceneHeight = 720;

    private Board board;
    private Rectangle[][] cells;
    private Tile currentTile;
    private Timeline activeTimeline;
    private Stage primaryStage;
    private BorderPane root;
    private ScrollPane boardScroll;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        Scene scene = new Scene(initScene(), sceneWidth, sceneHeight);

        stage.setTitle("Tucil 3");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(450);
        stage.show();
    }

    private Board loadBoard(String filePath) {
        Parser parser = new Parser();
        try {
            return parser.parseBoard(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BorderPane initScene() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        boardScroll = new ScrollPane();
        boardScroll.setFitToWidth(true);
        boardScroll.setFitToHeight(true);
        boardScroll.setPannable(true);
        root.setCenter(boardScroll);

        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            if (board == null) {
                return;
            }

            Algorithm algorithm = new GBFS();
            Result result = algorithm.solve(board);
            animatePath(result);
        });

        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> configChoose());

        HBox bottomBox = new HBox(10, loadButton, startButton);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        setBoard(loadBoard("data/1.txt"));

        return root;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void animatePath(Result result) {
        if (board == null) {
            return;
        }

        if (activeTimeline != null) {
            activeTimeline.stop();
        }

        currentTile = board.start;
        renderBoard();

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
                    renderBoard();
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

    private void configChoose() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Config");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Conf", "*.txt"));
        chooser.setInitialDirectory(new java.io.File("data").getAbsoluteFile());

        java.io.File selectedFile = chooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }

        setBoard(loadBoard(selectedFile.getAbsolutePath()));
    }

    private void setBoard(Board newBoard) {
        if (newBoard == null) {
            return;
        }

        if (activeTimeline != null) {
            activeTimeline.stop();
            activeTimeline = null;
        }

        board = newBoard;
        currentTile = board.start;
        updateCellSize();
        boardScroll.setContent(createGrid(board));
        renderBoard();
    }

    private void updateCellSize() {
        int availableWidth = sceneWidth - 20;
        int availableHeight = sceneHeight - 20 - 80;
        cellSize = Math.max(20, Math.min(availableWidth / board.col, availableHeight / board.row));
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

    private void renderBoard() {
        if (board == null || cells == null) {
            return;
        }

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
