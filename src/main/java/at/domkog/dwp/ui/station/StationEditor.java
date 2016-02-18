package at.domkog.dwp.ui.station;

import at.domkog.dwp.DWP;
import at.domkog.dwp.player.stations.Station;
import at.domkog.dwp.player.stations.resolver.StationDataResolver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Created by Dominik on 15.01.2016.
 */
public class StationEditor {

    public enum EditorMode {
        NEW("Add station"), EXISTING("Edit station");

        private String titleText;

        EditorMode(String title) {
            titleText = title;
        }

        public String getTitleText() {
            return this.titleText;
        }

    }

    public Station station;
    public EditorMode mode;

    public TextField nameInput, urlInput;

    boolean opened = false;

    public StackPane pane;
    public VBox vBox;

    public SaveResponse saveResponse;

    public void init() {
        saveResponse = new SaveResponse();

        pane = new StackPane();
        pane.setMinSize(320, 420);
        pane.setMaxSize(320, 420);

        VBox vBox = new VBox(30);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setMinSize(300, 400);
        vBox.setMaxSize(300, 400);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header");
        header.setMinSize(300, 50);
        header.setMaxSize(300, 50);

        vBox.getChildren().add(header);

        Button back = new Button();
        back.getStyleClass().add("btn-round");
        ImageView backImg = new ImageView(StationUI.imgBackLeft);
        backImg.setFitHeight(26);
        backImg.setFitWidth(26);
        back.setGraphic(backImg);
        back.setPadding(new Insets(0, 0, 0, 6));

        back.setMinSize(40, 40);
        back.setMaxSize(40, 40);

        back.setOnAction((event) -> this.close(StationUI.rootPane));

        header.getChildren().add(back);

        Text text = new Text(mode.titleText);
        text.getStyleClass().add("editor-header");
        header.getChildren().add(text);

        VBox nameBox = new VBox(5);
        nameBox.setAlignment(Pos.CENTER);

        Text nameHeader = new Text("Name:");
        nameHeader.getStyleClass().add("editor-input");

        nameBox.getChildren().add(nameHeader);

        nameBox.setMargin(nameHeader, new Insets(0, 190, 0, 0));

        if(mode == EditorMode.EXISTING) nameInput = new TextField(station.displayName);
        else nameInput = new TextField();

        nameInput.setMinSize(250, 25);
        nameInput.setMaxSize(250, 25);

        nameBox.getChildren().add(nameInput);

        vBox.getChildren().add(nameBox);

        VBox urlBox = new VBox(5);
        urlBox.setAlignment(Pos.CENTER);

        Text urlHeader = new Text("URL:");
        urlHeader.getStyleClass().add("editor-input");

        urlBox.getChildren().add(urlHeader);

        urlBox.setMargin(urlHeader, new Insets(0, 205, 0, 0));

        if(mode == EditorMode.EXISTING) urlInput = new TextField(station.hostURL);
        else urlInput = new TextField();

        urlInput.setMinSize(250, 25);
        urlInput.setMaxSize(250, 25);

        urlBox.getChildren().add(urlInput);

        vBox.getChildren().add(urlBox);

        VBox resolverBox = new VBox(5);
        resolverBox.setAlignment(Pos.CENTER);

        Text resolverHeader = new Text("Resolver:");
        resolverHeader.getStyleClass().add("editor-input");

        resolverBox.getChildren().add(resolverHeader);

        ComboBox<String> resolver = new ComboBox<>();
        ObservableList<String> comboContent = FXCollections.observableArrayList();
        comboContent.add("None");
        for(StationDataResolver s: DWP.stationDataResolverManager.getAll()) {
            comboContent.add(s.properties.getResolverID());
        }
        resolver.setItems(comboContent);

        resolver.setMinSize(250, 40);
        resolver.setMaxSize(250, 40);

        if(mode == EditorMode.EXISTING) {
            if(DWP.stationDataResolverManager.contains(station.resolver)) {
                resolver.getSelectionModel().select(station.resolver);
            }
        }

        resolverBox.getChildren().add(resolver);

        vBox.getChildren().add(resolverBox);
        resolverBox.setMargin(resolverHeader, new Insets(0, 170, 0, 0));

        Button save = new Button("Save");
        save.getStyleClass().add("editor-save");

        save.setOnAction((event) -> {
            if(nameInput.getText() == null || nameInput.getText().equalsIgnoreCase("")) {
                saveResponse.show(pane, "Fill in name and URL.", "#D46A6A");
                return;
            }
            if(urlInput.getText() == null || urlInput.getText().equalsIgnoreCase("")) {
                saveResponse.show(pane, "Fill in name and URL.", "#D46A6A");
                return;
            }
            if(!urlInput.getText().startsWith("http://")) {
                saveResponse.show(pane, "URL need to start with 'http://'.", "#D46A6A");
                return;
            }
            station.displayName = nameInput.getText();
            station.hostURL = urlInput.getText();

            boolean response = true;
            if(mode == EditorMode.NEW) response = DWP.stationManager.createStation(station);
            else if(mode == EditorMode.EXISTING) response = DWP.stationManager.updateStation(station);
            if(!response) {
                saveResponse.show(pane, "Database error", "#D46A6A");
                return;
            }
            if(!resolver.getSelectionModel().isEmpty() && (station.resolver == null || !station.resolver.equalsIgnoreCase(resolver.getSelectionModel().getSelectedItem()))) {
                if(resolver.getSelectionModel().getSelectedItem().equalsIgnoreCase("none")) station.resolver = "";
                else station.resolver = resolver.getSelectionModel().getSelectedItem();
                DWP.stationManager.updateResolver(station);
            }

            this.close(StationUI.rootPane);
            StationUI.updateList();
        });

        vBox.getChildren().add(save);

        pane.getChildren().add(vBox);
    }

    public void open(StackPane rootPane, Station station, EditorMode mode) {
        if(!opened) {
            this.station = station;
            this.mode = mode;
            init();
            rootPane.getChildren().add(pane);
            rootPane.getChildren().remove(StationUI.vBox);
            opened = true;
        }
    }

    public void close(StackPane rootPane) {
        if(opened) {
            rootPane.getChildren().add(StationUI.vBox);
            rootPane.getChildren().remove(pane);
            station = null;
            mode = null;
            opened = false;
        }
    }

    public boolean hasChangedData() {
        return true;
    }

}
