package uce.bits_app;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.xml.sax.SAXException;



/**
 * https://github.com/viktorkifer/RssReader/tree/master/RSSClient/src/android/edu/rss
 * Created by Ubbo Eicke on 23.08.2016.
 */
public class rss_feed_fm extends Fragment {

    private ListView feedList;
    private FeedAdapter adapter;
    private ProgressBar pbLoad;

    private final String url = "http://9gagrss.com/feed/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        feedList = (ListView)view.findViewById(R.id.feedList);
        feedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if(getActivity() instanceof OnRSSItemSelected) {
                    OnRSSItemSelected listener = (OnRSSItemSelected) getActivity();
                    listener.onRSSItemSelected(((RssItem)adapter.getItem(arg2)).getLink());
                } else {
                    Toast.makeText(getActivity(), "Activity should implement OnRSSItemSelected interface", Toast.LENGTH_LONG).show();
                }
            }
        });
        pbLoad = (ProgressBar)view.findViewById(R.id.pbLoad);
        new GetRSSFeedTask().execute(url);

        return view;
    }

    private void updateFeedList(ArrayList<RssItem> items){
        adapter = new FeedAdapter(items, getActivity());
        feedList.setAdapter(adapter);
        showList();
    }

    private void showList(){
        pbLoad.setVisibility(View.GONE);
        feedList.setVisibility(View.VISIBLE);
    }

    private void showProgress(){
        pbLoad.setVisibility(View.VISIBLE);
        feedList.setVisibility(View.GONE);
    }

    class GetRSSFeedTask extends AsyncTask<String, Void, RssFeed>{

        @Override
        protected void onPreExecute() {
            showProgress();
            super.onPreExecute();
        }
        @Override
        protected RssFeed doInBackground(String... params) {
            RssFeed feed = null;
            try {
                URL url = new URL(params[0]);
                feed = RssReader.read(url);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return feed;
        }

        @Override
        protected void onPostExecute(RssFeed feed) {
            super.onPostExecute(feed);
            if(feed == null)
                return;
            updateFeedList(feed.getRssItems());
        }
    }

    public interface OnRSSItemSelected {
        public void onRSSItemSelected(String url);
    }

}
