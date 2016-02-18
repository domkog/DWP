package at.domkog.dwp.ui;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Dominik on 09.01.2016.
 */
public class UIManager {

    public final LinkedHashMap<String, AbstractUI> registeredUis;

    public AbstractUI currentUi;

    public final Stage stage;

    public UIManager(Stage stage) {
        this.stage = stage;

        registeredUis = new LinkedHashMap<String, AbstractUI>();
    }

    public void init() {
        registeredUis.values().forEach(ui -> ui.init());
    }

    public AbstractUI get(String key) {
        if(registeredUis.containsKey(key)) return registeredUis.get(key);
        return null;
    }

    public void register(String key, AbstractUI ui) {
        registeredUis.put(key, ui);
    }

    public void show(String key) {
        if(registeredUis.containsKey(key)) this.show(this.registeredUis.get(key));
    }

    public void show(AbstractUI ui) {
        if(currentUi != null) currentUi.onHide(stage);
        currentUi = ui;
        currentUi.onShow(stage);
    }

}
