package at.domkog.dwp.ui.options;

import at.domkog.dwp.DWP;
import at.domkog.dwp.ui.AbstractUI;
import at.domkog.dwp.ui.MenuSelector;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
public class OptionUI extends AbstractUI {

    public static Image imgOptions;
    public static Image imgBackRight;
    public static Image imgBackLeft;

    public MenuSelector menuSelector;

    public static StackPane rootPane;
    public VBox vBox;
    public HBox header, footer;

    public static Scene scene;

    @Override
    public String getMenuName() {
        return "Options";
    }

    @Override
    public Image getMenuImage() {
        return imgOptions;
    }

    @Override
    public void init() {
        menuSelector = new MenuSelector();

        imgOptions = new Image(DWP.class.getResourceAsStream("/images/list_dark.png"));
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

        vBox.getChildren().add(header);

        VBox content = new VBox();

        content.setMinSize(250, 300);
        content.setMaxSize(250, 300);

        content.getChildren().add(new ToggleOption("Idle theme", "idle"));

        vBox.getChildren().add(content);

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

        scene.getStylesheets().add("optionStyle.css");
    }

    @Override
    public void onShow(Stage stage) {
        stage.setScene(scene);
    }

    @Override
    public void onHide(Stage stage) {

    }

    @Override
    public boolean listed() {
        return true;
    }

}
