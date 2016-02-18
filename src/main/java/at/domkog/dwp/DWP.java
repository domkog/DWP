package at.domkog.dwp;

import at.domkog.dwp.player.MediaPlayerWrapper;
import at.domkog.dwp.player.stations.StationManager;
import at.domkog.dwp.player.stations.resolver.StationDataResolverManager;
import at.domkog.dwp.timeline.TimelineManager;
import at.domkog.dwp.timeline.ui.TimelineUi;
import at.domkog.dwp.ui.IdleScreen;
import at.domkog.dwp.ui.UIManager;
import at.domkog.dwp.ui.options.OptionUI;
import at.domkog.dwp.ui.options.Options;
import at.domkog.dwp.ui.player.PlayerUI;
import at.domkog.dwp.ui.station.StationUI;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;

/**
 * Created by Dominik on 09.01.2016.
 */
public class DWP extends Application {

    public static final String version = "Stable.1.1";

    public static UIManager uiManager;
    public static Options options;
    public static StationManager stationManager;
    public static StationDataResolverManager stationDataResolverManager;
    public static TimelineManager timelineManager;

    public static boolean isIdle = false;
    public static long lastInput;

    public static IdleScreen idleScreen;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        options = new Options();

        options.add("idle", new Options.OptionValue(true));

        MediaPlayerWrapper.init();

        this.idleScreen = new IdleScreen();

        primaryStage.getIcons().add(new Image(DWP.class.getResourceAsStream("/images/Logo.png")));
        primaryStage.setOnCloseRequest((event) -> MediaPlayerWrapper.onClose());

        primaryStage.addEventHandler(InputEvent.ANY, new EventHandler<InputEvent>() {
            @Override
            public void handle(InputEvent event) {
                lastInput = System.currentTimeMillis();
                if(options.get("idle").asBoolean() && isIdle) {
                    idleScreen.hide.play();
                    isIdle = false;
                }
            }
        });

        stationDataResolverManager = new StationDataResolverManager();
        timelineManager = new TimelineManager();

        stationManager = new StationManager();
        stationManager.loadStations();

        primaryStage.setTitle("DWP " + version);
        primaryStage.setResizable(false);

        //Create new UIManager for Scene handling
        uiManager = new UIManager(primaryStage);
        //Register all Scenes
        registerUis();
        //Init all Scenes
        uiManager.init();

        //Show default Scene
        uiManager.show("player");

        primaryStage.show();
    }

    private void registerUis() {
        uiManager.register("player", new PlayerUI());
        uiManager.register("timeline", new TimelineUi());
        uiManager.register("stations", new StationUI());
        uiManager.register("options", new OptionUI());
    }

}
