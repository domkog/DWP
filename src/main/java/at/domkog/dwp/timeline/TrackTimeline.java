package at.domkog.dwp.timeline;

import at.domkog.dwp.DWP;
import at.domkog.dwp.player.stations.SQLiteDatabase;
import at.domkog.dwp.player.stations.Station;
import at.domkog.dwp.player.stations.StationData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Created by Dominik on 09.02.2016.
 */
public class TrackTimeline {

    public int stationID;
    public SQLiteDatabase db;

    public TrackTimeline(Station station) {
        stationID = station.id;
        db = DWP.stationManager.db;
    }

    public void log(TimelineEntry entry) {
        try {
            PreparedStatement statement = db.connection.prepareStatement("INSERT INTO timeline(station, title, artist) VALUES(?, ?, ?);");
            statement.setInt(1, stationID);
            statement.setString(2, entry.data.title);
            statement.setString(3, entry.data.artist);
            statement.execute();

            statement.close();

            this.deleteOldEntries();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<TimelineEntry> fetch() {
        this.deleteOldEntries();

        LinkedList<TimelineEntry> entries = new LinkedList<>();
        try {
            PreparedStatement statement = db.connection.prepareStatement("SELECT * FROM timeline WHERE station = ? ORDER BY time DESC;");
            statement.setInt(1, stationID);
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                TimelineEntry entry = new TimelineEntry();
                entry.stationID = rs.getInt("station");
                entry.timestamp = rs.getDate("time");
                entry.data = new StationData();
                entry.data.title = rs.getString("title");
                entry.data.artist = rs.getString("artist");
                entries.add(entry);
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public void deleteOldEntries() {
        System.out.println("Cleaning database");
        try {
            PreparedStatement statement = db.connection.prepareStatement("DELETE FROM timeline WHERE time <= date('now','-6 hours');");
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
