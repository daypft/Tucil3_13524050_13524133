import backend.Board;
import backend.Parser;
import backend.Tile;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class App extends Application {

    private int cellSize = 100;
    private int size = 1000;

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

        VBox orderBox = new VBox(10);

        return borderPane;
    }

    public static void main(String[] args) {
        launch(args);
    }


    private GridPane createGrid(Board board) {
        GridPane grid = new GridPane();
        for (int r = 0; r < board.row; r++) {
            for (int c = 0; c < board.col; c++) {
                Tile tile = board.tiles[r][c];
                String color = backgroundColor(tile);
                grid.add(new Rectangle(cellSize, cellSize, Color.web(color)), c, r);
            }
        }
        return grid;
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
