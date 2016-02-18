package at.domkog.dwp.ui.player;

import at.domkog.dwp.DWP;
import at.domkog.dwp.player.MediaPlayerWrapper;
import at.domkog.dwp.ui.AbstractUI;
import at.domkog.dwp.ui.MenuSelector;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Created by Dominik on 09.01.2016.
 */
public class PlayerUI extends AbstractUI {

    public Scene scene;

    public static ImageView imgMenu;
    public static ImageView imgMusic;
    public static Image imgMusicDark;
    public static ImageView imgStations;
    public ImageView imgStationsDark;
    public static ImageView imgPause;
    public static ImageView imgPlay;
    public static ImageView imgMute;

    public static StackPane rootPane;

    public static Label songLabel;
    public static int startChar = 0, endChar = 13;
    public static boolean scrollWaiting = false;
    public static long lastScroll = -1;
    public static Label stationLabel;

    public static Image defaultStation;
    public static ImageView stationImage;

    public static MenuSelector menuSelector;
    public static StationSelector stationSelector;

    @Override
    public String getMenuName() {
        return "Listen";
    }

    @Override
    public Image getMenuImage() {
        return imgMusicDark;
    }

    @Override
    public void init() {
        menuSelector = new MenuSelector();
        stationSelector = new StationSelector();

        imgMenu = new ImageView(new Image(DWP.class.getResourceAsStream("/images/arrowRight.png")));
        imgMusic = new ImageView(new Image(DWP.class.getResourceAsStream("/images/music.png")));
        imgMusicDark = new Image(DWP.class.getResourceAsStream("/images/music_dark.png"));
        imgStations = new ImageView(new Image(DWP.class.getResourceAsStream("/images/list.png")));
        imgStationsDark = new ImageView(new Image(DWP.class.getResourceAsStream("/images/list_dark.png")));
        imgPause = new ImageView(new Image(DWP.class.getResourceAsStream("/images/pause.png")));
        imgPlay = new ImageView(new Image(DWP.class.getResourceAsStream("/images/play.png")));
        imgMute = new ImageView(new Image(DWP.class.getResourceAsStream("/images/mute.png")));

        rootPane = new StackPane();

        VBox vBox = new VBox(10);

        //Header content BEGIN
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER);

            VBox labelBox = new VBox();
            labelBox.setMinSize(200, 55);
            labelBox.setMaxSize(200, 55);

            Label playLabel = new Label("Playing:");
            playLabel.getStyleClass().add("label-small");

            songLabel = new Label("");
            songLabel.getStyleClass().add("label-mid");

            ScheduledService<Void> scrollLabel = new ScheduledService<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            if(MediaPlayerWrapper.player == null) return null;
                            String title = MediaPlayerWrapper.currentStation.stationData.header;
                            if(title.length() <= 13) {
                                Platform.runLater(() -> PlayerUI.songLabel.setText(title));
                                return null;
                            }
                            if(PlayerUI.startChar == PlayerUI.endChar) {
                                PlayerUI.startChar = 0;
                                PlayerUI.endChar = 13;
                                if(!scrollWaiting) {
                                    lastScroll = System.currentTimeMillis();
                                    scrollWaiting = true;
                                }
                            } else if(!scrollWaiting) {
                                PlayerUI.startChar++;
                                if(PlayerUI.endChar < title.length()) PlayerUI.endChar++;
                            }
                            if(scrollWaiting) {
                                Platform.runLater(() -> PlayerUI.songLabel.setText(title));
                                if((System.currentTimeMillis() - lastScroll) >= 10000) scrollWaiting = false;
                                return null;
                            }
                            Platform.runLater(() -> PlayerUI.songLabel.setText(title.substring(PlayerUI.startChar, PlayerUI.endChar)));
                            return null;
                        };
                    };
                }
            };

        scrollLabel.setPeriod(Duration.millis(200));
        scrollLabel.start();

            labelBox.getChildren().add(playLabel);
            labelBox.getChildren().add(songLabel);

            header.getChildren().add(labelBox);
            header.setMargin(labelBox, new Insets(0, 20, 0, 40));

        header.getChildren().add(imgMusic);

        vBox.getChildren().add(header);
        //Header content END

        //Station label BEGIN

        VBox station = new VBox(5);
        station.setAlignment(Pos.CENTER);

        String stationName = "";
        if(MediaPlayerWrapper.currentStation != null) stationName = MediaPlayerWrapper.currentStation.displayName;
        stationLabel = new Label(stationName);
        stationLabel.getStyleClass().add("label-big");

        Button stationBtn = new Button();
        imgStationsDark.setFitWidth(18);
        imgStationsDark.setFitHeight(18);
        stationBtn.setGraphic(imgStationsDark);
        stationBtn.setMinSize(30, 30);
        stationBtn.setMaxSize(30, 30);
        stationBtn.getStyleClass().add("btn-menu");

        stationBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stationSelector.open(rootPane);
                stationSelector.show.play();
            }
        });

        defaultStation = new Image(DWP.class.getResourceAsStream("/images/stationDefault.png"));
        stationImage = new ImageView(defaultStation);
        stationImage.setFitWidth(100);
        stationImage.setFitHeight(100);

        station.getChildren().add(stationLabel);
        station.getChildren().add(stationBtn);
        station.getChildren().add(stationImage);

        station.setMargin(stationImage, new Insets(20, 0, 0, 0));

        vBox.getChildren().add(station);

        //Station label END

        //Controles BEGIN
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);

            ToggleButton btnPause = new ToggleButton("");
            btnPause.setGraphic(imgPause);
            btnPause.setMinSize(60, 60);
            btnPause.setMaxSize(60, 60);
            btnPause.getStyleClass().add("btn-round-small");

            btnPause.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(MediaPlayerWrapper.player == null) btnPause.setSelected(false);
                    MediaPlayerWrapper.pause();
                }
            });

            Button btnPlay = new Button("");
            btnPlay.setGraphic(imgPlay);
            btnPlay.setMinSize(80, 80);
            btnPlay.setMaxSize(80, 80);
            btnPlay.getStyleClass().add("btn-round-big");

            btnPlay.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    MediaPlayerWrapper.play();
                }
            });

            ToggleButton btnMute = new ToggleButton("");
            btnMute.setGraphic(imgMute);
            btnMute.setMinSize(60, 60);
            btnMute.setMaxSize(60, 60);
            btnMute.getStyleClass().add("btn-round-small");

            btnMute.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    MediaPlayerWrapper.mute();
                }
            });

            hbBtn.getChildren().add(btnPause);
            hbBtn.getChildren().add(btnPlay);
            hbBtn.getChildren().add(btnMute);

            hbBtn.setPadding(new Insets(0, 0,0,0));

        vBox.getChildren().add(hbBtn);
        vBox.setMargin(hbBtn, new Insets(20, 0, 0, 0));

        //Controles END

        //Slider BEGIN

        VBox sliderBox = new VBox();
        sliderBox.setAlignment(Pos.CENTER);

            StackPane volumePane = new StackPane();

                ProgressBar volumeBar = new ProgressBar();
                volumeBar.getStyleClass().add("vol-bar");
                volumeBar.setMaxWidth(270);
                volumeBar.setProgress(0D);

                Slider volumeSlider = new Slider();
                volumeSlider.getStyleClass().add("vol-slider");
                volumeSlider.setMin(0);
                volumeSlider.setMax(1);
                volumeSlider.setMajorTickUnit(0.1D);
                volumeSlider.setMinSize(270, 7);
                volumeSlider.setMaxSize(270, 7);

                volumeSlider.valueProperty().addListener((ov, old_val, new_val) -> {
                    volumeBar.setProgress(new_val.doubleValue());
                    MediaPlayerWrapper.onChangeVolume(new_val.doubleValue());
                });

            volumePane.getChildren().add(volumeBar);
            volumePane.getChildren().add(volumeSlider);

            sliderBox.getChildren().add(volumePane);

        vBox.getChildren().add(sliderBox);

        //Slider END

        rootPane.getChildren().add(vBox);

        Button menuBtn = new Button("");
        menuBtn.setGraphic(imgMenu);
        menuBtn.setContentDisplay(ContentDisplay.RIGHT);
        menuBtn.setMinSize(80, 80);
        menuBtn.setMaxSize(80, 80);
        menuBtn.getStyleClass().add("btn-menu");
        menuBtn.setPadding(new Insets(0, 0, 0, 22));

        menuBtn.setOnAction((event) -> {
            menuSelector.open(rootPane);
            menuSelector.show.play();
        });

        rootPane.getChildren().add(menuBtn);
        rootPane.setMargin(menuBtn, new Insets(-340, 0, 0, -300));

        scene = new Scene(rootPane, 300, 400);

        scene.getStylesheets().add("style.css");
    }

    @Override
    public void onShow(Stage stage) {
        stage.setScene(scene);
    }

    @Override
    public void onHide(Stage stage) {
    }

    public void update() {
        String text = "";
        if(MediaPlayerWrapper.currentStation != null) text = MediaPlayerWrapper.currentStation.displayName;
        stationLabel.setText(text);
    }

    @Override
    public boolean listed() {
        return true;
    }

}
