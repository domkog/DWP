package at.domkog.dwp.timeline.ui;

import at.domkog.dwp.DWP;
import at.domkog.dwp.player.stations.Station;
import at.domkog.dwp.timeline.TimelineEntry;
import at.domkog.dwp.ui.AbstractUI;
import at.domkog.dwp.ui.MenuSelector;
import at.domkog.dwp.ui.station.StationEditor;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Dominik on 09.02.2016.
 */
public class TimelineUi extends AbstractUI {

    private Image clock, imgBackRight, imgBackLeft;

    public static StackPane rootPane;
    public static Scene scene;

    public static VBox vBox;

    public static HBox header, footer;

    public static ListView<String> listView;
    public MenuSelector menuSelector;
    public Text menuTitle;

    public Station selectedStation;
    public boolean timeline = false;

    @Override
    public boolean listed() {
        return true;
    }

    @Override
    public String getMenuName() {
        return "Timeline";
    }

    @Override
    public Image getMenuImage() {
        return clock;
    }

    @Override
    public void init() {
        menuSelector = new MenuSelector();

        clock = new Image(DWP.class.getResourceAsStream("/images/clock.png"));
        imgBackRight = new Image(DWP.class.getResourceAsStream("/images/arrowRight.png"));
        imgBackLeft = new Image(DWP.class.getResourceAsStream("/images/arrowLeft.png"));

        rootPane = new StackPane();

        vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setMinSize(300, 400);
        vBox.setMaxSize(300, 400);

        header = new HBox(10);
        header.getStyleClass().add("header");
        header.setMinSize(300, 50);
        header.setMaxSize(300, 50);

        Button back = new Button();
        back.getStyleClass().add("btn-round");
        ImageView backImg = new ImageView(imgBackRight);
        backImg.setFitHeight(26);
        backImg.setFitWidth(26);
        back.setGraphic(backImg);
        back.setPadding(new Insets(0, 6, 0, 0));

        back.setMinSize(40, 40);
        back.setMaxSize(40, 40);

        back.setOnAction((event) -> {
            menuSelector.open(rootPane);
            menuSelector.show.play();
        });

        header.getChildren().add(back);

        header.setMargin(back, new Insets(5, 0, 0, 5));

        Button previous = new Button();
        ImageView viewAdd = new ImageView(imgBackLeft);
        viewAdd.setFitHeight(20);
        viewAdd.setFitWidth(20);

        previous.setGraphic(viewAdd);
        previous.getStyleClass().add("btn-round");
        previous.setMinSize(30, 30);
        previous.setMaxSize(30, 30);

        previous.setPadding(new Insets(2, 0, 0, 5));
        previous.setDisable(true);

        previous.setOnAction((event) -> {
            ObservableList<String> listContent = FXCollections.observableArrayList();
            for(Station s: DWP.stationManager.stations) {
                listContent.add(s.displayName);
            }
            Platform.runLater(() -> {
                listView.getItems().clear();
                listView.setItems(listContent);
            });
            menuTitle.setText(this.getMenuName());
            previous.setDisable(true);
            selectedStation = null;
            timeline = false;
        });

        header.getChildren().add(previous);
        header.setMargin(previous, new Insets(10, 0, 0, 200));

        vBox.getChildren().add(header);

        listView = new ListView<String>();
        listView.setStyle("-fx-background-color: transparent;");
        ObservableList<String> listContent = FXCollections.observableArrayList();
        for(Station s: DWP.stationManager.stations) {
            listContent.add(s.displayName);
        }
        listView.setItems(listContent);

        listView.setMinSize(280, 300);
        listView.setMaxSize(280, 300);
        listView.setPrefSize(280, 300);

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(selectedStation != null) return;
                if(newValue == null) {
                    selectedStation = null;
                } else if(!timeline) {
                    selectedStation = DWP.stationManager.getFromName(newValue);
                    menuTitle.setText(selectedStation.displayName);
                    LinkedList<TimelineEntry> timelineList = DWP.timelineManager.getTimeline(selectedStation).fetch();
                    ObservableList<String> listContent = FXCollections.observableArrayList();
                    for(TimelineEntry e: timelineList) {
                        if(e.data.artist.equalsIgnoreCase("")) listContent.add(e.data.title);
                        else listContent.add(e.data.title + " - " + e.data.artist);
                    }
                    Platform.runLater(() -> {listView.getItems().clear();
                    if(!listContent.isEmpty()) listView.setItems(listContent);});
                    previous.setDisable(false);
                    timeline = true;
                    return;
                }
            }
        });

        vBox.getChildren().add(listView);

        footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.getStyleClass().add("footer");
        footer.setMinSize(300, 50);
        footer.setMaxSize(300, 50);

        menuTitle = new Text(this.getMenuName());
        menuTitle.getStyleClass().add("footer-text");

        footer.getChildren().add(menuTitle);

        vBox.getChildren().add(footer);

        rootPane.getChildren().add(vBox);

        scene = new Scene(rootPane, 300, 400);

        scene.getStylesheets().add("timelineStyle.css");
    }

    @Override
    public void onShow(Stage stage) {
        stage.setScene(scene);
    }

    @Override
    public void onHide(Stage stage) {

    }
}
