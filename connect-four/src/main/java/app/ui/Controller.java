package app.ui;

import app.logic.Board;
import app.logic.Heuristics.Heuristic;
import app.logic.Heuristics.IHeuristic;
import app.logic.minimax.MiniMax;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    public static final boolean PRUNE = true;
    public static final boolean PRINT_MINIMAX_TREE = false;
    public static final int MINIMAX_DEPTH = 3;
    public static final int COLUMNS = 7;
    public static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 80;
    public static final String discColor1 = "0x000000ff";
    public static final String discColor2 = "0xffffffff";
    public static Board board = new Board(discColor2, discColor1);

    public static String PLAYER_ONE = "Player One";
    public static String PLAYER_Two = "Player Two";

    public static boolean isPlayerOne = true;

    private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];

    public static IHeuristic heuristic = new Heuristic();

    private boolean isAllowed = true;

    @FXML
    public GridPane rootGridPane;
    @FXML
    public Pane insertDiscsPane;

    @FXML
    public Label playerNameLabel;

    @FXML
    public TextField pl1, pl2;

    @FXML
    public Label score1, score2;

    @FXML
    public Button setBtn;


    public void createPlayground() {


        pl1.setFocusTraversable(false);
        pl2.setFocusTraversable(false);


        setBtn.setOnAction(event -> {

            PLAYER_ONE = pl1.getText();
            PLAYER_Two = pl2.getText();

            if (PLAYER_ONE.isEmpty() || PLAYER_Two.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Enter all Details");
                alert.setContentText("Enter details for both Player One and Player Two");
                alert.show();
                PLAYER_ONE = "PLAYER ONE";
                PLAYER_Two = "PLAYER TWO";
                resetGame();
                pl1.clear();
                pl2.clear();


            }
            resetGame();


        });


        Shape rectangleWithHoles = createGameStructuralGrid();


        rootGridPane.add(rectangleWithHoles, 0, 1);

        List<Rectangle> rectangleList = createClickableColumns();

        for (Rectangle rectangle : rectangleList
        ) {
            rootGridPane.add(rectangle, 0, 1);

        }

    }


    private Shape createGameStructuralGrid() {

        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);


        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {

                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);

            }


        }


        rectangleWithHoles.setFill(Color.DARKSALMON);

        return rectangleWithHoles;


    }


    private List<Rectangle> createClickableColumns() {

        List<Rectangle> rectangleList = new ArrayList<>();


        for (int col = 0; col < COLUMNS; col++) {
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);


            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;
            rectangle.setOnMouseClicked(event -> {

                if (isAllowed) {
                    isAllowed = false;
                    insertDisc(new Disc(isPlayerOne), column);

                }
            });

            rectangleList.add(rectangle);

        }

        return rectangleList;

    }

    private void insertDisc(Disc disc, int column) {

        int row = ROWS - 1;

        while (row >= 0) {

            if (getDiscIfPresent(row, column) == null)
                break;
            row--;
        }


        if (row < 0) {
            return;
        }



        insertDiscsPane.getChildren().add(disc);

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + 20);

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + 20);

        int currentRow = row;

        translateTransition.setOnFinished(event -> {
            isAllowed = true;

            insertedDiscArray[currentRow][column] = disc;
            board.set(currentRow, column, disc.getFill().toString());
            System.out.println(board.toSimpleString());

            if (Heuristic.gameEnded(board,disc.getFill().toString())) {

                gameOver();

            }
            isPlayerOne = !isPlayerOne;

            playerNameLabel.setText(isPlayerOne ? PLAYER_ONE : PLAYER_Two);


            displayScore();
            if(!isPlayerOne){
                isPlayerOne = !isPlayerOne;
                makeAIMove();
            }
        });

        translateTransition.play();

    }

    private void makeAIMove(){
        Disc disc = new Disc(!isPlayerOne);
        int column = MiniMax.determineBestMove(board,Controller.MINIMAX_DEPTH,Controller.PRUNE,Controller.PRINT_MINIMAX_TREE);
        System.out.println("Make AI Move in col: "+column);
        isPlayerOne = !isPlayerOne;
        insertDisc(disc, column);
    }

    private void displayScore() {
        double score = Controller.heuristic.determineScore(board);
        if(score==100.0){

        }
        this.score1.setText(String.valueOf(score));
    }

    private Disc getDiscIfPresent(int row, int column) {

        if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
            return null;
        else
            return insertedDiscArray[row][column];
    }

    private void gameOver() {

        String winner = isPlayerOne ? PLAYER_ONE : PLAYER_Two;
        System.out.println("Winner is " + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setContentText("Want to play again ?");

        alert.setHeaderText("Winner is " + winner);

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No,Exit");


        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Platform.runLater(() -> {


            Optional<ButtonType> btnCLicked = alert.showAndWait();

            if (btnCLicked.isPresent() && btnCLicked.get() == yesBtn) {
                resetGame();
            } else {

                Platform.exit();
                System.exit(0);


            }


        });


    }

    public void resetGame() {
        insertDiscsPane.getChildren().clear();

        for (int row = 0; row < insertedDiscArray.length; row++) {
            for (int col = 0; col < insertedDiscArray[row].length; col++) {

                insertedDiscArray[row][col] = null;
            }

        }

        isPlayerOne = true;
        playerNameLabel.setText(PLAYER_ONE);
        board.clear();
        createPlayground();
    }


    private static class Disc extends Circle {

        private final boolean isPlayerOneMove;

        public Disc(boolean isPlayerOneMove) {

            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER / 2);
            setCenterX(CIRCLE_DIAMETER / 2);
            setCenterY(CIRCLE_DIAMETER / 2);

            setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
