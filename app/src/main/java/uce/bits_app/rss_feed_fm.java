package uce.bits_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Ubbo on 25.08.2016.
 */
public class rss_feed_fm extends Fragment implements AdapterView.OnItemClickListener {

    private ProgressBar progressBar;
    private ListView listView;
    private View view;
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.rss_fm, container, false);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            listView = (ListView) view.findViewById(R.id.listView);
            listView.setOnItemClickListener(this);
            startService();
        } else {
            // If we are returning from a configuration change:
            // "view" is still attached to the previous view hierarchy
            // so we need to remove it and re-attach it to the current one
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        return view;
    }

    private void startService() {
        Intent intent = new Intent(getActivity(), rss_service.class);
        intent.putExtra(rss_service.RECEIVER, resultReceiver);
        getActivity().startService(intent);
    }

    /**
     * Once the {@link rss_service} finishes its task, the result is sent to this ResultReceiver.
     */
    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            List<rss_Item> items = (List<rss_Item>) resultData.getSerializable(rss_service.ITEMS);
            if (items != null) {
                rss_adapter adapter = new rss_adapter(getActivity(), items);
                listView.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), "An error occured while downloading the rss feed.",
                        Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        };
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        rss_adapter adapter = (rss_adapter) parent.getAdapter();
        rss_Item item = (rss_Item) adapter.getItem(position);
        Uri uri = Uri.parse(item.getLink());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}