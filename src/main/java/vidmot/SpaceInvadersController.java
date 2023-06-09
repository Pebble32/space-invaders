package vidmot;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import vinnsla.Data;
import vinnsla.Game;

import java.io.IOException;
import java.util.Objects;

public class SpaceInvadersController {

    @FXML
    private BorderPane fxBorderPane;
    @FXML
    private Label fxScoreMain;
    @FXML
    private Leikbord fxGamePane;
    private final Game game = new Game();
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
        s.addEventFilter(KeyEvent.ANY,
                event -> {
                    try {
                        if ((event.getCode()) == KeyCode.RIGHT){
                           fxGamePane.getShip().moveRight();
                        } else if ((event.getCode()) == KeyCode.LEFT){
                            fxGamePane.getShip().moveLeft();
                        }
                    } catch (NullPointerException e){
                        event.consume();
                    }
        });
    }

    /**
     * Stoppa animation og kalla á Alert fall
     */
    public void leikLokid() throws IOException {
        t.stop();
        data.setScore(game.getPoints());
        gameOverScene();
    }

    /**
     * Kallar á föll til að endurstilla lofsteina
     * Núllstílla stig og level
     * Byrja Timeline frá upphafi
     */
    private void gameOverScene() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("gameover-view.fxml")));
        Stage stage = (Stage) fxBorderPane.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * KeyFrame til að byrja leik skoðar score hverja 20ms
     */
    public void startTheGame(){
        KeyFrame k = new KeyFrame(Duration.millis(game.getInterval()),
                e-> {
                    fxScoreMain.setText(String.valueOf(game.getPoints()));
                    if(fxGamePane.isGameOver()){
                        try {
                            leikLokid();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    game.setPoints(fxGamePane.getScore());
                });
        t = new Timeline(k);           // tengjum timeline og tímabilið
        t.setCycleCount(Timeline.INDEFINITE);// hve lengi tímalínan keyrist
        t.play();
    }
}