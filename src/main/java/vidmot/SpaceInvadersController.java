package vidmot;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import vinnsla.Data;
import vinnsla.Game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class SpaceInvadersController {

    @FXML
    private BorderPane fxBorderPane;
    // Tilviksbreytur
    @FXML
    private Label fxScoreMain;
    @FXML
    private Leikbord fxGamePane;
    public static final String PLAY_AGAIN = ": Want to try again?";
    private final Game game = new Game();
    private static final HashMap<KeyCode, Direction> map = new HashMap<>();
    private Timeline t;
    private final Data data = Data.getInstance();

    public void initialize(){
        fxGamePane.getStyleClass().add("bord");
        startTheGame();
    }

    /**
     * Map fyrir örvartakkar til að færa skipið
     * @param s sena sem það er gert á
     */
    public void orvatakkar(Scene s) {
        map.put(KeyCode.RIGHT, Direction.RIGHT);
        map.put(KeyCode.LEFT, Direction.LEFT);
        s.addEventFilter(KeyEvent.ANY,
                event -> {
                    try {
                        if ((event.getCode()) == KeyCode.RIGHT){
                           moveShipRight();
                        } else if ((event.getCode()) == KeyCode.LEFT){
                            moveShipLeft();
                        }
                    } catch (NullPointerException e){
                        event.consume();
                    }
        });
    }

    /**
     * Stoppa animation og kalla á Alert fall
     */
    public void leikLokid(){
        t.stop();
        Platform.runLater(() -> {
            try {
                synaAlert("GAMOVER");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Býrta AlertDialog til að byrja nýja leik eða fara á scoreboard
     * @param s skilaboð
     * @throws Exception
     */
    private void synaAlert(String s) throws Exception {
        Alert a = new AdvorunDialog("GAMEOVER", SpaceInvadersApplication.TITLE, s + PLAY_AGAIN);
        Optional<ButtonType> u = a.showAndWait();
        if (u.isPresent() && !u.get().getButtonData().isCancelButton()) System.out.println("Nyr leikur");
        else {
            data.setScore(game.getPoints());
            lostScene();
        }

    }

    private void lostScene() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("gameover-view.fxml")));
        Stage stage = (Stage) fxBorderPane.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Færir geimskipið til hægri um 5 pixlar
     */
    private void moveShipRight(){
        fxGamePane.getShip().setX(fxGamePane.getShip().getX()+5);
    }

    /**
     * Færir geimskipið til vinstri um 5 pixlar
     */
    private void moveShipLeft(){
        fxGamePane.getShip().setX(fxGamePane.getShip().getX()-5);
    }

    /**
     * Hækka level eftir 1009 stig og auka hraða
     */
    public void setLevel(){
        int i = game.getPoints();
        if (i % 1009 == 0 && i > 1){ //53 is a prime
            game.levelUp();
            double current = t.getRate();
            t.setRate(current * 1.2);
        }
        game.addScore();
    }

    /**
     * KeyFrame til að byrja leik skoðar score hverja 20ms
     */
    public void startTheGame(){
        KeyFrame k = new KeyFrame(Duration.millis(game.getIntervall()),
                e-> {
                    setLevel();
                    fxScoreMain.setText(String.valueOf(game.getPoints()));
                    if(fxGamePane.isGameover()){
                        leikLokid();
                    }
                });
        t = new Timeline(k);           // tengjum timeline og tímabilið
        t.setCycleCount(Timeline.INDEFINITE);// hve lengi tímalínan keyrist
        t.play();
    }
}