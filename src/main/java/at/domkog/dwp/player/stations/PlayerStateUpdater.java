package at.domkog.dwp.player.stations;

import at.domkog.dwp.DWP;
import at.domkog.dwp.player.MediaPlayerWrapper;
import at.domkog.dwp.player.stations.resolver.CustomStationData;
import at.domkog.dwp.player.stations.resolver.StationDataResolver;
import at.domkog.dwp.ui.IdleScreen;
import at.domkog.dwp.ui.player.PlayerUI;
import at.domkog.dwp.ui.player.TextScroller;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;

/**
 * Created by Dominik on 22.01.2016.
 */
public class PlayerStateUpdater {

    public PlayerStateUpdater() {

        ScheduledService<Void> updater = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if(MediaPlayerWrapper.currentStation != null && MediaPlayerWrapper.started) {

                            if(DWP.options.get("idle").asBoolean() && !DWP.isIdle && (System.currentTimeMillis() - DWP.lastInput) >= 60000) {
                                Platform.runLater(() -> {
                                    DWP.idleScreen.open(PlayerUI.rootPane);
                                    DWP.idleScreen.show.play();
                                });
                                DWP.isIdle = true;
                            }

                            Station s = MediaPlayerWrapper.currentStation;
                            if(s.resolver != null && !s.resolver.equalsIgnoreCase("") && DWP.stationDataResolverManager.contains(s.resolver)) {
                                StationDataResolver resolver = DWP.stationDataResolverManager.getResolver(s.resolver);

                                //s.stationData = resolver.fetchData();

                                StationData newData = resolver.fetchData();
                                try {
                                    if (s.stationData == null || hasTrackChanged(s.stationData, newData)) {
                                        s.stationData = newData;
                                        Thread t = new Thread(() -> MediaPlayerWrapper.onTrackChange());
                                        t.start();
                                    }
                                } finally {
                                    s.stationData = newData;
                                }

                                if(s.stationData instanceof CustomStationData) {
                                    CustomStationData data = (CustomStationData) s.stationData;
                                    if(data.data.containsKey("image")) {
                                        Image img = new Image(new URL(data.data.get("image").value).openStream());
                                        PlayerUI.stationImage.setImage(img);
                                    }
                                }
                                if(DWP.isIdle) {
                                    DWP.idleScreen.titleScroller.setBaseValue(s.stationData.title);
                                    DWP.idleScreen.artistScroller.setBaseValue(s.stationData.artist);
                                }
                            } else {
                                try {
                                    if (hasTrackChanged(MediaPlayerWrapper.player.getMediaMeta().getNowPlaying(), MediaPlayerWrapper.player.getMediaMeta().getArtist())) {
                                        System.out.println("Data changed!");
                                        s.stationData.artist = MediaPlayerWrapper.player.getMediaMeta().getArtist();
                                        s.stationData.title = MediaPlayerWrapper.player.getMediaMeta().getNowPlaying();
                                        s.stationData.header = s.stationData.title;
                                        MediaPlayerWrapper.onTrackChange();
                                    }
                                } finally {
                                    s.stationData.artist = MediaPlayerWrapper.player.getMediaMeta().getArtist();
                                    s.stationData.title = MediaPlayerWrapper.player.getMediaMeta().getNowPlaying();
                                    s.stationData.header = s.stationData.title;
                                }

                                DWP.idleScreen.titleScroller.setBaseValue(s.stationData.title);
                                DWP.idleScreen.artistScroller.setBaseValue(s.stationData.artist);
                            }
                        }
                        return null;
                    };
                };
            }
        };
        updater.setPeriod(Duration.seconds(3));
        updater.start();

    }

    public boolean hasTrackChanged(StationData oldData, StationData newData) {
        return !oldData.title.equalsIgnoreCase(newData.title) || !oldData.artist.equalsIgnoreCase(newData.artist);
    }

    public boolean hasTrackChanged(String newTitle, String newArtist) {
        return !MediaPlayerWrapper.currentStation.stationData.title.equalsIgnoreCase(newTitle) || !MediaPlayerWrapper.currentStation.stationData.artist.equalsIgnoreCase(newArtist);
    }

}
