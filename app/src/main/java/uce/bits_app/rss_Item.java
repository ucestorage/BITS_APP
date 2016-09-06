package uce.bits_app;

/**
 * Created by Ubbo on 25.08.2016.
 */

public class rss_Item {

    private final String title;
    private final String link;

    public rss_Item(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}