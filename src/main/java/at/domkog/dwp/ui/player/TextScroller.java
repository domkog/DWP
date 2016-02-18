package at.domkog.dwp.ui.player;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

/**
 * Created by Dominik on 31.01.2016.
 */
public class TextScroller extends ScheduledService<Void> {

    public Label text;
    private int scrollPeriod;

    private String baseValue;

    private int startChar = 0;
    private int endChar;

    private final int charLength;

    private long lastScroll;
    private boolean scrollWaiting = false;

    public TextScroller(Label text, int endChar, int scrollPeriod) {
        this.text = text;
        this.scrollPeriod = scrollPeriod;
        this.charLength = endChar;
        this.endChar = charLength;
    }

    public void setBaseValue(String baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if(baseValue.equalsIgnoreCase("")) return null;
                if(baseValue.length() <= charLength) {
                    Platform.runLater(() -> text.setText(baseValue));
                    return null;
                }
                if(startChar == endChar) {
                    startChar = 0;
                    endChar = charLength;
                    if(!scrollWaiting) {
                        lastScroll = System.currentTimeMillis();
                        scrollWaiting = true;
                    }
                } else if(!scrollWaiting) {
                    startChar++;
                    if(endChar < baseValue.length()) endChar++;
                }
                if(scrollWaiting) {
                    Platform.runLater(() -> text.setText(baseValue));
                    if((System.currentTimeMillis() - lastScroll) >= scrollPeriod) scrollWaiting = false;
                    return null;
                }
                Platform.runLater(() -> text.setText(baseValue.substring(startChar, endChar)));
                return null;
            };
        };
    }
}
