package uce.bits_app;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ubbo Eicke on 23.08.2016.
 */
class rss_ParseHandler extends DefaultHandler {

    private List<rss_item> rssItems;

    // Used to reference item while parsing
    private rss_item currentItem;

    // Parsing title indicator
    private boolean parsingTitle;
    // A buffer used to build current title being parsed
    private StringBuffer currentTitleSb;

    // Parsing link indicator
    private boolean parsingLink;

    public rss_ParseHandler() {
        rssItems = new ArrayList<rss_item>();
    }

    public List<rss_item> getItems() {
        return rssItems;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("item".equals(qName)) {
            currentItem = new rss_item();
        } else if ("title".equals(qName)) {
            parsingTitle = true;

            currentTitleSb = new StringBuffer();
        } else if ("link".equals(qName)) {
            parsingLink = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("item".equals(qName)) {
            rssItems.add(currentItem);
            currentItem = null;
        } else if ("title".equals(qName)) {

            parsingTitle = false;

            // Set item's title when we parse item->title tag not the channel title tag
            if (currentItem != null) {
                // Set item's title here
                currentItem.setTitle(currentTitleSb.toString());
            }

        } else if ("link".equals(qName)) {
            parsingLink = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (parsingTitle) {
            if (currentItem != null) {
                // Here we append the title to the buffer due to network issues.
                // Sometimes this characters method is called multiple times for a tag contents.
                currentTitleSb.append(new String(ch, start, length));
            }
        } else if (parsingLink) {
            if (currentItem != null) {
                currentItem.setLink(new String(ch, start, length));
                parsingLink = false;
            }
        }
    }
}
