package at.domkog.dwp.player.stations;

/**
 * Created by Dominik on 10.01.2016.
 */
public class Station {

    public String displayName, hostURL;
    public int id;
    public String resolver;

    public StationData stationData = new StationData();

    public String customDataResolver;

    public Station(String displayName, String hostURL) {
        this.displayName = displayName;
        this.hostURL = hostURL;
    }

    public Station() {}

}
