package at.domkog.dwp.player.stations.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Dominik on 25.01.2016.
 */
public class StationDataResolverManager {

    private HashMap<String, StationDataResolver> dataResolver;
    private StationDataResolverLoader loader;

    public StationDataResolverManager() {
        dataResolver = new HashMap<>();
        loader = new StationDataResolverLoader();
        ArrayList<StationDataResolver> resolvers = loader.loadAll();

        resolvers.forEach((resolver) -> {
            register(resolver);
            resolver.onEnable();
        });
    }

    public void disableAll() {
        for(StationDataResolver resolver: dataResolver.values()) {
            resolver.onDisable();
        }
        dataResolver.clear();
    }

    public void disable(StationDataResolver resolver) {
        if(!contains(resolver.properties.getResolverID())) return;
        resolver.onDisable();
        dataResolver.remove(resolver.properties.getResolverID());
    }

    public void register(StationDataResolver resolver) {
        if(!contains(resolver.properties.getResolverID())) {
            dataResolver.put(resolver.properties.getResolverID(), resolver);
        }
    }

    public boolean contains(String ID) {
        return dataResolver.containsKey(ID);
    }

    public StationDataResolver getResolver(String id) {
        if(contains(id)) return dataResolver.get(id);
        else return null;
    }

    public Collection<StationDataResolver> getAll() {
        return dataResolver.values();
    }

}
