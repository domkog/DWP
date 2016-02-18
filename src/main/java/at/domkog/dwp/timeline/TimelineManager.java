package at.domkog.dwp.timeline;

import at.domkog.dwp.player.stations.Station;

import java.util.HashMap;

/**
 * Created by Dominik on 09.02.2016.
 */
public class TimelineManager {

    public HashMap<Integer, TrackTimeline> timelines;

    public TimelineManager() {
        timelines = new HashMap<>();
    }

    public TrackTimeline getTimeline(Station station) {
        if(timelines.containsKey(station.id)) return timelines.get(station.id);
        else {
            TrackTimeline timeline = new TrackTimeline(station);
            timelines.put(station.id, timeline);
            return timeline;
        }
    }

}
