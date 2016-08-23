package uce.bits_app;

/**
 * Created by Ubbo Eicke on 23.08.2016.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;



public class rss_feed extends Fragment {

    // A reference to the local object
    private rss_feed local;

    /**
     * This method creates main application view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.rss_feed, container, false);
        local = this;

        GetRSSDataTask task = new GetRSSDataTask();

        // Start download RSS task
        task.execute("https://www.hs-owl.de/fb8/fb8.rss");

        // Debug the thread name
        Log.d("Rss", Thread.currentThread().getName());


        return v;
    }
  /*  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set view
        setContentView(R.layout.rss_feed);

        // Set reference to this activity
        local = this;

        GetRSSDataTask task = new GetRSSDataTask();

        // Start download RSS task
        task.execute("https://www.hs-owl.de/fb8/fb8.rss");

        // Debug the thread name
        Log.d("Rss", Thread.currentThread().getName());
    }*/

    private class GetRSSDataTask extends AsyncTask<String, Void, List<rss_item> > {
        @Override
        protected List<rss_item> doInBackground(String... urls) {

            // Debug the task thread name
            Log.d("Rss", Thread.currentThread().getName());

            try {
                // Create RSS reader
                rss_Reader rssReader = new rss_Reader(urls[0]);

                // Parse RSS, get items
                return rssReader.getItems();

            } catch (Exception e) {
                Log.e("Rss", e.getMessage());
            }

            return null;
        }

       /* @Override
        protected void onPostExecute(List<rss_item> result) {

            // Get a ListView from main view
            ListView itcItems = (ListView) findViewById(R.id.listMainView);

            // Create a list adapter
            ArrayAdapter<rss_item> adapter = new ArrayAdapter<rss_item>(local,android.R.layout.simple_list_item_1, result);
            // Set list adapter for the ListView
            itcItems.setAdapter(adapter);

            // Set list view item click listener
            itcItems.setOnItemClickListener(new rss_listlistener(result, local));
        }*/
    }
}

