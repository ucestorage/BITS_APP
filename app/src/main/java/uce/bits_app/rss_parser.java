package uce.bits_app;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ubbo on 25.08.2016.
 */
public class rss_parser {

    private final String ns = null;
    //Hier wird der rss parser mit Daten gefüttert, Leerzeichen werden nicht mit geparst
    public List<rss_Item> parse(InputStream inputStream) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            inputStream.close();
        }
    }
    //Items abfragen
    private List<rss_Item> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "rss");
        String title = null;
        String link = null;
        List<rss_Item> items = new ArrayList<rss_Item>();
        //while schleife geht dokument bis zum ende durch, bis alle vorhandenen Items gebaut wurden
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                //Titel wird abgefragt
                title = readTitle(parser);
            } else if (name.equals("link")) {
                //LInk wird abgefragt
                link = readLink(parser);
            }
            if (title != null && link != null) {
                //Rss-Item wird aus Titel und Link zusammengebaut
                rss_Item item = new rss_Item(title, link);
                items.add(item);
                title = null;
                link = null;
            }
        }
        return items;
    }
    //Link abfragen
    private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }
    //Titel abfragen
    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Die Texte von Titel und Link der Items abfragen
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
