package at.domkog.dwp.ui.station;

import at.domkog.dwp.DWP;
import at.domkog.dwp.player.stations.Station;
import at.domkog.dwp.ui.AbstractUI;
import at.domkog.dwp.ui.MenuSelector;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by Dominik on 10.01.2016.
 */
public class StationUI extends AbstractUI {

        public static Image imgStations;

        public static StackPane rootPane;
        public static Scene scene;

        public static VBox vBox;

        public static HBox header, footer;

        public static ListView<String> listView;

        public static Image imgAdd;
        public static Image imgEdit;
        public static Image imgDelete;
        public static Image imgBackRight;
        public static Image imgBackLeft;

        public MenuSelector menuSelector;
        public StationEditor stationEditor;

        public Station selectedStation = null;

        @Override
        public String getMenuName() {
            return "Stations";
        }

        @Override
        public Image getMenuImage() {
            return imgStations;
        }

        @Override
        public void init() {
            menuSelector = new MenuSelector();
            stationEditor = new StationEditor();

            imgStations = new Image(DWP.class.getResourceAsStream("/images/stationIcon.png"));
            imgAdd = new Image(DWP.class.getResourceAsStream("/images/addDark.png"));
            imgEdit = new Image(DWP.class.getResourceAsStream("/images/editDark.png"));
            imgDelete = new Image(DWP.class.getResourceAsStream("/images/deleteDark.png"));
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

            Button add = new Button();
            ImageView viewAdd = new ImageView(imgAdd);
            viewAdd.setFitHeight(20);
            viewAdd.setFitWidth(20);

            add.setGraphic(viewAdd);
            add.getStyleClass().add("btn-round");
            add.setMinSize(30, 30);
            add.setMaxSize(30, 30);

            add.setPadding(new Insets(0, 0, 0, 0));

            add.setOnAction((event) -> stationEditor.open(rootPane, new Station(), StationEditor.EditorMode.NEW));

            header.getChildren().add(add);

            Button edit = new Button();
            ImageView viewEdit = new ImageView(imgEdit);
            viewEdit.setFitHeight(20);
            viewEdit.setFitWidth(20);

            edit.setGraphic(viewEdit);
            edit.getStyleClass().add("btn-round");
            edit.setMinSize(30, 30);
            edit.setMaxSize(30, 30);

            edit.setPadding(new Insets(0, 0, 0, 0));

            edit.setOnAction((event) -> {
                if(selectedStation != null) {
                    stationEditor.open(rootPane, selectedStation, StationEditor.EditorMode.EXISTING);
                }
            });

            header.getChildren().add(edit);
            edit.setDisable(true);

            Button del = new Button();
            ImageView viewDel = new ImageView(imgDelete);
            viewDel.setFitHeight(20);
            viewDel.setFitWidth(20);

            del.setGraphic(viewDel);
            del.getStyleClass().add("btn-round");
            del.setMinSize(30, 30);
            del.setMaxSize(30, 30);

            del.setPadding(new Insets(0, 0, 0, 0));

            del.setOnAction((event) -> {
                if(selectedStation != null) {
                    DWP.stationManager.deleteStation(selectedStation);
                    updateList();
                }
            });

            header.getChildren().add(del);
            del.setDisable(true);

            header.setMargin(back, new Insets(5, 0, 0, 5));
            header.setMargin(add, new Insets(10, 0, 0, 120));
            header.setMargin(edit, new Insets(10, 0, 0, 0));
            header.setMargin(del, new Insets(10, 0, 0, 0));


            vBox.getChildren().add(header);

            listView = new ListView<String>();
            listView.setStyle("-fx-background-color: transparent;");
            ObservableList<String> listContent = FXCollections.observableArrayList();
            for(Station s: DWP.stationManager.stations) {
                listContent.add(s.displayName);
            }
            listView.setItems(listContent);

            listView.setMinSize(250, 300);
            listView.setMaxSize(250, 300);

            listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if(newValue == null) {
                        selectedStation = null;
                        edit.setDisable(true);
                        del.setDisable(true);
                    } else {
                        selectedStation = DWP.stationManager.getFromName(newValue);
                        edit.setDisable(false);
                        del.setDisable(false);
                    }
                }
            });

            vBox.getChildren().add(listView);

            footer = new HBox();
            footer.setAlignment(Pos.CENTER);
            footer.getStyleClass().add("footer");
            footer.setMinSize(300, 50);
            footer.setMaxSize(300, 50);

            Text menuTitle = new Text(this.getMenuName());
            menuTitle.getStyleClass().add("footer-text");

            footer.getChildren().add(menuTitle);

            vBox.getChildren().add(footer);

            rootPane.getChildren().add(vBox);

            scene = new Scene(rootPane, 300, 400);

            scene.getStylesheets().add("stationStyle.css");
        }

        @Override
        public void onShow(Stage stage) {
            stage.setScene(scene);
        }

        @Override
        public void onHide(Stage stage) {
        }

    public static void updateList() {
        ObservableList<String> listContent = FXCollections.observableArrayList();
        for(Station s: DWP.stationManager.stations) {
            listContent.add(s.displayName);
        }
        listView.setItems(listContent);
    }

    @Override
    public boolean listed() {
        return true;
    }

}
