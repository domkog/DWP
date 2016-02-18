package at.domkog.dwp.ui;

import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by Dominik on 09.01.2016.
 */
public abstract class AbstractUI {

    public abstract boolean listed();

    public abstract String getMenuName();
    public abstract Image getMenuImage();

    public abstract void init();

    public abstract void onShow(Stage stage);
    public abstract void onHide(Stage stage);

}
