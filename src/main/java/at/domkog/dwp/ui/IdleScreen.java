package at.domkog.dwp.ui;

import at.domkog.dwp.player.MediaPlayerWrapper;
import at.domkog.dwp.ui.player.TextScroller;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dominik on 25.01.2016.
 */
public class IdleScreen {

    public StackPane rootPane;
    public boolean opened = false;

    public FadeTransition show, hide;

    public StackPane pane;
    public VBox vBox;

    public Text clock;
    public SimpleDateFormat clockDf;
    public Calendar calendar;

    public Text station;
    public Label title, artist;
    public TextScroller stationScroller, titleScroller, artistScroller;

    public ScheduledService<Void> updater;

    public static ArrayList<TextScroller> drawableData;

    public void init() {
        drawableData = new ArrayList<>();

        pane = new StackPane();
        pane.getStyleClass().add("idle-pane");
        pane.setMinSize(320, 420);
        pane.setMaxSize(320, 420);

        vBox = new VBox(20);
        vBox.setMinSize(240, 420);
        vBox.setMaxSize(240, 420);
        vBox.setPadding(new Insets(10, 0, 0, 0));
        vBox.setAlignment(Pos.TOP_CENTER);

        clock = new Text();
        clock.getStyleClass().add("idle-clock");

        clockDf = new SimpleDateFormat("HH:mm:ss");

        updater = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(() -> clock.setText(clockDf.format(new Date())));
                        return null;
                    };
                };
            }
        };
        updater.setPeriod(Duration.seconds(1));
        updater.start();

        vBox.getChildren().add(clock);
        vBox.setMargin(clock, new Insets(30, 0, 0, 0));

        station = new Text(MediaPlayerWrapper.currentStation.displayName);
        station.getStyleClass().add("idle-station");

        vBox.getChildren().add(station);
        vBox.setMargin(station, new Insets(40, 0, 0, 0));

        this.title = new Label(MediaPlayerWrapper.currentStation.stationData.title);
        this.title.getStyleClass().add("idle-data");
        this.title.setStyle("-fx-font-size: 20px;");

        vBox.getChildren().add(this.buildTitledVBox("Song:", this.title));

        this.titleScroller = new TextScroller(this.title, 16, 10000);
        this.titleScroller.setBaseValue(this.title.getText());
        this.titleScroller.setPeriod(Duration.millis(200));
        this.titleScroller.start();

        this.artist = new Label(MediaPlayerWrapper.currentStation.stationData.artist);
        this.artist.getStyleClass().add("idle-data");
        this.artist.setStyle("-fx-font-size: 20px;");

        vBox.getChildren().add(this.buildTitledVBox("Artist:", this.artist));

        this.artistScroller = new TextScroller(this.artist, 16, 10000);
        this.artistScroller.setBaseValue(this.artist.getText());
        this.artistScroller.setPeriod(Duration.millis(200));
        this.artistScroller.start();

        pane.getChildren().add(vBox);

        show = new FadeTransition(Duration.millis(1500), pane);
        show.setFromValue(0.0);
        show.setToValue(1.0);

        hide = new FadeTransition(Duration.millis(1500), pane);
        hide.setFromValue(1.0);
        hide.setToValue(0.0);

        hide.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                IdleScreen.this.close(rootPane);
            }
        });

        rootPane.getChildren().add(pane);
    }

    public VBox buildTitledVBox(String title, Label label) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER_LEFT);

        Text titleText = new Text(title);
        titleText.getStyleClass().add("idle-header");

        vBox.getChildren().add(titleText);

        vBox.getChildren().add(label);
        return vBox;
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
            updater.cancel();
            pane.getChildren().remove(this.pane);
            opened = false;
        }
    }

}
