package at.domkog.dwp.ui;

import at.domkog.dwp.DWP;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Dominik on 10.01.2016.
 */
public class MenuSelector {

    public StackPane rootPane;
    public boolean opened = false;

    public Animation show, hide;

    public StackPane pane;
    public VBox vBox;

    public void init() {
        pane = new StackPane();
        pane.getStyleClass().add("menu-pane");
        pane.setMinSize(320, 420);
        pane.setMaxSize(320, 420);

        vBox = new VBox(20);
        vBox.getStyleClass().add("menu-box");
        vBox.setMinSize(240, 420);
        vBox.setMaxSize(240, 420);
        vBox.setPadding(new Insets(10, 0, 0, 0));
        vBox.setAlignment(Pos.TOP_CENTER);

        for(AbstractUI ui: DWP.uiManager.registeredUis.values()) {
            if(!ui.listed()) continue;
            Button btn = new Button("   " + ui.getMenuName());
            btn.setContentDisplay(ContentDisplay.LEFT);

            ImageView img = new ImageView(ui.getMenuImage());
            img.setFitHeight(24);
            img.setFitWidth(24);

            btn.setGraphic(img);
            btn.getStyleClass().add("menu-btn");
            btn.setMinSize(200, 40);
            btn.setMaxSize(200, 40);

            btn.setPadding(new Insets(0, 0, 0, 0));

            btn.setOnAction((event) -> {
                hide.play();
                DWP.uiManager.show(ui);
            });

            vBox.getChildren().add(btn);
        }

        pane.getChildren().add(vBox);

        Button back = new Button("Return");

        back.setContentDisplay(ContentDisplay.LEFT);
        ImageView imgBack = new ImageView(new Image(DWP.class.getResourceAsStream("/images/arrowLeft.png")));
        imgBack.setFitWidth(24);
        imgBack.setFitHeight(24);

        back.setGraphic(imgBack);
        back.getStyleClass().add("menu-btn");
        back.setMinSize(200, 40);
        back.setMaxSize(200, 40);

        back.setPadding(new Insets(0, 0, 0, 0));

        back.setOnAction((event) -> {
            hide.play();
        });

        pane.getChildren().add(back);





        pane.setMargin(back, new Insets(340, 0, 0, 0));

        final double startWidth = 300;
        hide = new Transition() {
            { setCycleDuration(Duration.millis(150)); }
            protected void interpolate(double frac) {
                final double curWidth = startWidth * (1.0 - frac);
                pane.setTranslateX(-startWidth + curWidth);
            }
        };
        hide.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                MenuSelector.this.close(rootPane);
            }
        });

        // create an animation to show a sidebar.
        show = new Transition() {
            { setCycleDuration(Duration.millis(150)); }
            protected void interpolate(double frac) {
                final double curWidth = startWidth * frac;
                pane.setTranslateX(-startWidth + curWidth);
            }
        };

        rootPane.getChildren().add(pane);
    }

    public void open(StackPane pane) {
        if(!opened) {
            this.rootPane = pane;
            this.init();

            opened = true;
        }
    }

    public void close(StackPane pane) {
        if(opened) {
            pane.getChildren().remove(this.pane);
            opened = false;
        }
    }

}
