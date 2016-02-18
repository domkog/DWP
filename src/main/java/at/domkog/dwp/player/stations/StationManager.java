package at.domkog.dwp.player.stations;

import at.domkog.dwp.DWP;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

/**
 * Created by Dominik on 10.01.2016.
 */
public class StationManager {

    public LinkedList<Station> stations;
    public SQLiteDatabase db;

    public StationManager() {
        db = new SQLiteDatabase("stations");
        stations = new LinkedList<>();
    }

    public Station getFromName(String name) {
        for(Station s: stations) {
            if(s.displayName.equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    public boolean createStation(Station station) {
        if(db == null) return false;
        stations.add(station);
        try {
            PreparedStatement statement = db.connection.prepareStatement("INSERT INTO stations(name, url) VALUES(?, ?);");
            statement.setString(1, station.displayName);
            statement.setString(2, station.hostURL);
            statement.executeUpdate();
            statement.close();

            PreparedStatement idStatement = db.connection.prepareStatement("SELECT MAX(ID) AS LAST FROM stations;");
            ResultSet rs = idStatement.executeQuery();

            String maxId = rs.getString("LAST");
            //Max Table Id Convert to Integer and
            int iMaxId = Integer.parseInt(maxId);

            station.id = iMaxId;

            System.out.println("Station " + station.displayName + " id: " + iMaxId);

            rs.close();
            idStatement.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStation(Station station) {
        if(db == null) return false;
        try {
            PreparedStatement statement = db.connection.prepareStatement("UPDATE stations SET name = ?, url = ? WHERE id = ?;");
            statement.setString(1, station.displayName);
            statement.setString(2, station.hostURL);
            statement.setInt(3, station.id);
            statement.execute();
            statement.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateResolver(Station station) {
        if(db == null) return false;
        try {
            PreparedStatement statement = db.connection.prepareStatement("UPDATE stations SET resolver = ? WHERE id = ?;");
            if(!station.resolver.equalsIgnoreCase("") && station.resolver != null) {
                statement.setString(1, station.resolver);
            } else {
                statement.setString(1, null);
            }
            statement.setInt(2, station.id);
            statement.execute();
            statement.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStation(Station station) {
        if(db == null) return false;
        stations.remove(station);
        try {
            PreparedStatement statement = db.connection.prepareStatement("DELETE FROM stations WHERE id = ?;");
            statement.setInt(1, station.id);
            statement.execute();
            statement.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadStations() {
        if(db == null) return;
        try {
            PreparedStatement statement = db.connection.prepareStatement("SELECT * FROM stations ORDER BY 'id';");
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                Station station = new Station(rs.getString("name"), rs.getString("url"));
                station.id = rs.getInt("id");
                station.resolver = rs.getString("resolver");
                if(!DWP.stationDataResolverManager.contains(station.resolver)) station.resolver = "";
                this.stations.add(station);
            }

            rs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
