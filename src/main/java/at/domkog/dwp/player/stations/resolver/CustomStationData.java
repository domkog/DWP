package at.domkog.dwp.player.stations.resolver;

import at.domkog.dwp.player.stations.StationData;

import java.util.HashMap;

/**
 * Created by Dominik on 25.01.2016.
 */
public class CustomStationData extends StationData {

    public HashMap<String, DataEntry> data;

    public CustomStationData() {
        data = new HashMap<>();
    }

    public static class DataEntry {

        public String value;

        public DataEntry(String entry) {
            this.value = entry;
        }

    }

    public static class DrawableDataEntry extends DataEntry {

        public int fontSize;

        public DrawableDataEntry(String entry, int fontSize) {
            super(entry);
            this.fontSize = fontSize;
        }

    }

}
