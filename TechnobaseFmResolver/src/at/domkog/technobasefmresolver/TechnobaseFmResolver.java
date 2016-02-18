package at.domkog.technobasefmresolver;

import at.domkog.dwp.player.stations.StationData;
import at.domkog.dwp.player.stations.resolver.CustomStationData;
import at.domkog.dwp.player.stations.resolver.StationDataResolver;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Created by Dominik on 30.01.2016.
 */
public class TechnobaseFmResolver extends StationDataResolver {

    @Override
    public void onLoad() {
        System.out.println("[Resolver] Loading " + properties.getResolverID() + "!");
    }

    @Override
    public void onEnable() {
        System.out.println("[Resolver] " + properties.getResolverID() + " enabled!");
    }

    @Override
    public void onDisable() {
        System.out.println("[Resolver] " + properties.getResolverID() + " disabled!");
    }

    @Override
    public StationData fetchData() {
        CustomStationData data = new CustomStationData();
        try {
            InputSource source = new InputSource(new StringReader(getTracklistXml("http://tray.technobase.fm/radio.xml")));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            Document doc = (Document) xpath.evaluate("/", source, XPathConstants.NODE);
            data.title = xpath.evaluate("//radio[name='TechnoBase']/song", doc);
            data.artist = xpath.evaluate("//radio[name='TechnoBase']/artist", doc);
            data.header = data.title + " - " + data.artist;

            data.data.put("moderator", new CustomStationData.DrawableDataEntry(xpath.evaluate("//radio[name='TechnoBase']/song", doc), 20));
            data.data.put("show", new CustomStationData.DrawableDataEntry(xpath.evaluate("//radio[name='TechnoBase']/artist", doc), 18));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return data;
    }

    private static final String getTracklistXml(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.setUseCaches(false);

        InputStream is = null;
        try {
            is = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStreamReader isr = new InputStreamReader(is, Charset
                .forName("ISO-8859-1"));
        BufferedReader reader = new BufferedReader(isr);

        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();

        return sb.toString();
    }

}