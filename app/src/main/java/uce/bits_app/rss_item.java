package uce.bits_app;

/**
 * Created by Ubbo Eicke on 23.08.2016.
 */
public class rss_item {
    // item title
    private String title;
    // item link
    private String link;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    @Override
    public String toString() {
        return title;
    }
}
