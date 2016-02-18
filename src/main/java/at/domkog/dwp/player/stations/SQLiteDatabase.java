package at.domkog.dwp.player.stations;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Dominik on 22.01.2016.
 */
public class SQLiteDatabase {

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String dbName;
    private File dbDir;
    private String dbPath;
    public Connection connection = null;

    public SQLiteDatabase(String dbName) {
        this.dbName = dbName;
        dbDir = new File(System.getenv("APPDATA") + File.separator + "DWP");
        if(!dbDir.exists()) dbDir.mkdirs();
        dbPath = dbDir.getAbsolutePath() + File.separator + dbName + ".db";
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(connection == null) return;
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS stations(" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "url STRING," +
                    "resolver STRING" +
                    ");");
            statement.execute();
            PreparedStatement timelineStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS timeline(" +
                    "station INTEGER," +
                    "time TIMESTAMP NOT NULL DEFAULT current_timestamp," +
                    "title TEXT," +
                    "artist TEXT" +
                    ");");
            timelineStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
