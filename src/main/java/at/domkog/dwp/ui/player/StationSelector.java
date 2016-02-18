package at.domkog.dwp.ui.player;

import at.domkog.dwp.DWP;
import at.domkog.dwp.player.MediaPlayerWrapper;
import at.domkog.dwp.player.stations.Station;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Created by Dominik on 10.01.2016.
 */
public class StationSelector {

    private StackPane pane;

    private ScrollPane sp;

    public Animation show, hide;

    private boolean opened = false;

    public void init() {
        sp = new ScrollPane();
        sp.setStyle("-fx-focus-color: transparent;");
        sp.setStyle("-fx-background-color:transparent;");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.getStyleClass().add("scrollbar");
        sp.setHmax(180);
            VBox vBox = new VBox(5);
            vBox.setStyle("-fx-focus-color: transparent;");
            vBox.setPadding(new Insets(5, 0, 0, 0));
            vBox.setAlignment(Pos.TOP_CENTER);
            vBox.getStyleClass().add("sidebar");

            Button back = new Button("Return");
            back.setContentDisplay(ContentDisplay.RIGHT);
            ImageView imgBack = new ImageView(new Image(DWP.class.getResourceAsStream("/images/arrowRight_White.png")));
            imgBack.setFitWidth(20);
            imgBack.setFitHeight(20);
            back.setGraphic(imgBack);
            back.getStyleClass().add("btn-sidebar-back");

            back.setOnAction((event) -> {
                hide.play();
            });
            vBox.getChildren().add(back);

            Image stationImg = new Image(DWP.class.getResourceAsStream("/images/station.png"));
            for(Station s: DWP.stationManager.stations) {
                ToggleButton btn = new ToggleButton(s.displayName);
                if(MediaPlayerWrapper.currentStation == s) btn.setSelected(true);
                btn.setContentDisplay(ContentDisplay.LEFT);
                btn.setAlignment(Pos.CENTER_LEFT);
                ImageView imgStation = new ImageView(stationImg);
                imgStation.setFitHeight(20);
                imgStation.setFitWidth(20);
                btn.setGraphic(imgStation);
                btn.getStyleClass().add("btn-sidebar");
                btn.setMinSize(180, 30);
                btn.setMaxSize(180, 30);

                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        MediaPlayerWrapper.onChangeStation(s);
                        hide.play();
                        //StationSelector.this.close(StationSelector.this.pane);
                    }
                });

                vBox.getChildren().add(btn);
            }

            vBox.setMinSize(190, 411);
            vBox.setMaxWidth(190);

        sp.setMinSize(196, 411);
        sp.setMaxSize(196, 411);

        //Disable HScrolling
        sp.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaX() != 0) {
                    event.consume();
                }
            }
        });

        final double startWidth = -200;
        hide = new Transition() {
            { setCycleDuration(Duration.millis(150)); }
            protected void interpolate(double frac) {
                final double curWidth = startWidth * (1.0 - frac);
                sp.setTranslateX(-startWidth + curWidth);
            }
        };
        hide.onFinishedProperty().set(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent actionEvent) {
                StationSelector.this.close(pane);
            }
        });

        // create an animation to show a sidebar.
        show = new Transition() {
            { setCycleDuration(Duration.millis(150)); }
            protected void interpolate(double frac) {
                final double curWidth = startWidth * frac;
                sp.setTranslateX(-startWidth + curWidth);
            }
        };

        sp.setContent(vBox);

    }

    public void open(StackPane pane) {
        if(!opened) {
            init();
            this.pane = pane;
            pane.getChildren().add(sp);
            pane.setMargin(sp, new Insets(0, 0, 0, 115));
            opened = true;
        }
    }

    public void close(StackPane pane) {
        if(opened) {
            pane.getChildren().removeAll(sp);
            opened = false;
            this.pane = null;
        }
    }

}
