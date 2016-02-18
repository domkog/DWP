package at.domkog.dwp.player.stations.resolver;

import at.domkog.dwp.player.stations.resolver.exceptions.ResolverException;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Dominik on 25.01.2016.
 */
public class StationDataResolverLoader {

    public File resolverDir = new File(System.getenv("APPDATA") + File.separator + "DWP" + File.separator + "resolver");

    public ArrayList<StationDataResolver> loadAll() {
        if(!resolverDir.exists()) resolverDir.mkdirs();

        ArrayList<StationDataResolver> result = new ArrayList<>();

        for(File f: resolverDir.listFiles()) {
            if(f.isDirectory()) continue;
            if(!isJarFile(f)) continue;

            result.add(load(f));
        }

        return result;
    }

    public StationDataResolver load(File file) {
        URLClassLoader cl = null;
        try {
            cl = new URLClassLoader(new URL[]{file.toURI().toURL()}, StationDataResolverLoader.class.getClassLoader());
        } catch (MalformedURLException e) {
            throw new ResolverException("An error occurred while loading " + file.getName() + ". Cannot convert from URI to URL!");
        }

        File fProperties = null;
            try {
                fProperties = File.createTempFile("resolver", ".properties");
                try (FileOutputStream out = new FileOutputStream(fProperties)) {
                    IOUtils.copy(cl.getResourceAsStream("resolver.properties"), out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        StationDataResolver.StationDataResolverProperties properties = null;
        try {
            properties = new StationDataResolver.StationDataResolverProperties(fProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String sMainClass = properties.getValueAsString("main");
        if(sMainClass == null || sMainClass.equalsIgnoreCase("")) throw new ResolverException("An error occurred while loading " + file.getName() + ". resolver.properties does not contains a main class declaration!");

        Class<?> mainClass = null;
        try {
            mainClass = cl.loadClass(sMainClass);
        } catch (ClassNotFoundException e) {
            throw new ResolverException("An error occurred while loading " + file.getName() + ". Couldn't load the main class!" + "\n" + e.getMessage());
        }

        if(!StationDataResolver.class.isAssignableFrom(mainClass)) throw new ResolverException("An error occurred while loading " + file.getName() + ". Main class must be an instance of StationDataResolver!");

        if(properties.getResolverID() == null || properties.getResolverID().equalsIgnoreCase("")) throw new ResolverException("An error occurred while loading " + file.getName() + ". resolver.properties does not contains an ID!");

        try {
            JarFile jar = new JarFile(file);
            Enumeration<JarEntry> enumeration = jar.entries();

            while(enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();

                //If the entry is not a class file we skip it.
                if(entry.isDirectory() || !entry.getName().endsWith(".class")) continue;

                //Split the raw class name from the file to load it.
                String className = entry.getName().substring(0,entry.getName().length()-6);
                className = className.replace('/', '.');

                //Finally load our class.
                try {
                    cl.loadClass(className);
                } catch (ClassNotFoundException e) {
                    throw new ResolverException("An error occurred while loading " + file.getName() + ". Unable to load " + entry.getName() + "!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StationDataResolver resolver = null;
        try {
            resolver = (StationDataResolver) mainClass.newInstance();

            resolver.properties = properties;
            resolver.onLoad();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return resolver;
    }

    private boolean isJarFile(File file) {
        System.out.println(file.getAbsolutePath() + " - " + file.getName().endsWith(".jar"));
        return file.getName().endsWith(".jar");
    }

}
