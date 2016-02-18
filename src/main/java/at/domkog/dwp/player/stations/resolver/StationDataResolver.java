package at.domkog.dwp.player.stations.resolver;

import at.domkog.dwp.player.stations.StationData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Dominik on 25.01.2016.
 */
public abstract class StationDataResolver {

    public StationDataResolverProperties properties;

    public void onLoad() {};

    public void onEnable() {};
    public void onDisable() {};

    public abstract StationData fetchData() throws IOException;

    public static class StationDataResolverProperties {

        protected final Properties property;
        private final File file;

        public StationDataResolverProperties(File file) throws IOException {
            this.file = file;
            this.property = new Properties();
            if(!file.exists()) file.createNewFile();
            this.property.load(new FileInputStream(file));
        }

        public Object getValue(Object key) { return property.get(key); }

        public String getValueAsString(Object key) { return (String) getValue(key); }

        public String getResolverID() {
            return getValueAsString("ID");
        }

    }

}
