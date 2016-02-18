package at.domkog.dwp.ui.station;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Created by Dominik on 22.01.2016.
 */
public class SaveResponse {

    public void show(StackPane pane, String response, String rectColor) {

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setStyle("-fx-background-color: " + rectColor + ";");
        hBox.setMinSize(300, 50);
        hBox.setMaxSize(300, 50);

        Text text = new Text(response);
        text.getStyleClass().add("editor-response");

        hBox.getChildren().add(text);

        FadeTransition ft = new FadeTransition(Duration.millis(750), hBox);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        ft.setOnFinished(event -> {
            PauseTransition wait = new PauseTransition(Duration.seconds(1));
            wait.setOnFinished((e) -> {
                FadeTransition backFade = new FadeTransition(Duration.millis(1000), hBox);
                backFade.setFromValue(1.0);
                backFade.setToValue(0.0);
                backFade.play();

                backFade.setOnFinished((event1) -> {
                    hBox.getChildren().remove(hBox);
                });
            });
            wait.play();
        });

        pane.getChildren().add(hBox);
        pane.setMargin(hBox, new Insets(310, 0, 0, 0));
    }

}
