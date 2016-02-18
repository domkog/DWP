package at.domkog.dwp.ui.options;

import at.domkog.dwp.DWP;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Created by Dominik on 24.01.2016.
 */
public class ToggleOption extends HBox {

    private String name, optionKey;

    private Text text;
    private ToggleButton button;

    public ToggleOption(String nama, String optionKey) {
        super(10);

        this.name = name;
        this.optionKey = optionKey;

        this.setAlignment(Pos.CENTER_LEFT);

        this.text = new Text(nama + ":");
        text.getStyleClass().add("option-text");

        this.getChildren().add(this.text);

        this.button = new ToggleButton();
        this.button.getStyleClass().add("option-button");

        this.button.setSelected(DWP.options.get(optionKey).asBoolean());

        this.button.setMinSize(28, 28);
        this.button.setMaxSize(28, 28);

        this.button.setOnAction((event) -> {
            DWP.options.set(optionKey, this.button.isSelected());
        });

        this.getChildren().add(this.button);

        this.setPadding(new Insets(10, 0, 0, 0));
    }

}
