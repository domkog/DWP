package at.domkog.dwp.player;

import at.domkog.dwp.DWP;
import at.domkog.dwp.player.stations.PlayerStateUpdater;
import at.domkog.dwp.player.stations.Station;
import at.domkog.dwp.timeline.TimelineEntry;
import at.domkog.dwp.ui.player.PlayerUI;
import com.sun.jna.NativeLibrary;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

/**
 * Created by Dominik on 09.01.2016.
 */
public class MediaPlayerWrapper {

    public static Station currentStation;
    public static int volume;

    public static HeadlessMediaPlayer player;

    public static boolean started = false, paused = false, muted = false;

    public static void init() {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
    }

    public static void play() {
        if(currentStation != null) {
            if(player == null) player = (new MediaPlayerFactory()).newHeadlessMediaPlayer();
            if(player.isPlaying()) {
                player.stop();

            }
            player.prepareMedia(currentStation.hostURL);

            player.play();
            player.setVolume(0);
            player.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

                @Override
                public void mediaMetaChanged(MediaPlayer paramMediaPlayer, int paramInt) {
                    PlayerUI.startChar = 0;
                    PlayerUI.endChar = 13;
                    if(paramMediaPlayer.getMediaMeta().getArtwork() != null) Platform.runLater(() -> PlayerUI.stationImage.setImage(SwingFXUtils.toFXImage(paramMediaPlayer.getSnapshot(), new WritableImage(100, 100))));
                }

            });
            new PlayerStateUpdater();
            started = true;
        }
    }

    public static void pause() {
        if(player != null) {
            if(player.isPlaying()) player.pause();
            else play();
            paused = !paused;
        }
    }

    public static void mute() {
        if(player != null) {
            muted = !muted;
            player.mute();
        }
    }

    public static void onChangeStation(Station s) {
        currentStation = s;
        ((PlayerUI) DWP.uiManager.get("player")).update();
        if(player != null && player.isPlaying()) {
            play();
        }
    }

    public static void onChangeVolume(double amount) {
        volume = (int) (amount * 100);
        if(player != null) player.setVolume(volume);
    }

    public static void onTrackChange() {
        TimelineEntry entry = new TimelineEntry();
        entry.data = currentStation.stationData;
        entry.stationID = currentStation.id;
        DWP.timelineManager.getTimeline(currentStation).log(entry);
    }

    public static void onClose() {
        if(player != null) player.stop();
        DWP.stationDataResolverManager.disableAll();
        Platform.exit();
        System.exit(0);
    }

}
