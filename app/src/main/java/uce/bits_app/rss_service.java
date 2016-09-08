package uce.bits_app;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * Created by Ubbo on 25.08.2016.
 */
public class rss_service extends IntentService {

    public static String RSS_LINK = "";
    public static final String ITEMS = "items";
    public static final String RECEIVER = "receiver";

    public rss_service() {
        super("RssService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //neuen rss parser erstellen der den InputStream der website parst
        List<rss_Item> rssItems = null;
        try {
            rss_parser parser = new rss_parser();
            rssItems = parser.parse(getInputStream(RSS_LINK));
        } catch (XmlPullParserException e) {
            Log.w(e.getMessage(), e);
        } catch (IOException e) {
            Log.w(e.getMessage(), e);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(ITEMS, (Serializable) rssItems);
        ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        receiver.send(0, bundle);
    }
    //Input stream von der angegebenen Website holen
    public InputStream getInputStream(String link) {
        try {
            URL url = new URL(link);
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }
}