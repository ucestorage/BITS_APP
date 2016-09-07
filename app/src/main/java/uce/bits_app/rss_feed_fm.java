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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Ubbo on 25.08.2016. Editiert von Dennis am 5.9
 */
public class rss_feed_fm extends Fragment implements AdapterView.OnItemClickListener {

    private ProgressBar progressBar;
    private ListView listView;
    private View view;
    private TextView stext;
    private rss_adapter adapter;

    private static String rss_source="";
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
            progressBar.setVisibility(View.GONE);
            //Setze den Standart RSS fürs erste Laden auf Hochschullevel
            rss_service.RSS_LINK="https://www.hs-owl.de/hsowl.rss";
            startService();

            //Nikrandt Spinner für Fachbereich
            /*
            Zunächst habe ich in der XML einen Spinner deklariert, dann eine Liste mit optionen
            unter strings.xml erzeugt. Danach wird ein Adapter erstellt, dieser füllt die Liste in
            den Spinner. Im Anschluss habe ich ein Multi If erschaffen welches je nach ausgewähltem
            Punkt den neuen RSS Feed auswählt. Danach wird der RSS Service beendet und neugestartet
            und die Liste zu aktualisieren ohne die Activity neu zu laden.
             */

            Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

            final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                    (view.getContext(), R.array.fachbereiche, android.R.layout.simple_spinner_item);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(0, false);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    if(parentView.getSelectedItem().toString().equals("HS-OWL"))
                    {
                        rss_service.RSS_LINK="https://www.hs-owl.de/hsowl.rss";
                    }
                    if(parentView.getSelectedItem().toString().equals("Fachbereich 8"))
                    {
                        rss_service.RSS_LINK="https://www.hs-owl.de/fb8/fb8.rss";
                    }
                    stopService();
                    startService();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }

            });

            // Nikrandt ende


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
    private void stopService(){
        Intent intent = new Intent(getActivity(), rss_service.class);
        intent.putExtra(rss_service.RECEIVER, resultReceiver);
        getActivity().stopService(intent);

    }

    /**
     * Once the {@link rss_service} finishes its task, the result is sent to this ResultReceiver.
     */
    private ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            List<rss_Item> items = (List<rss_Item>) resultData.getSerializable(rss_service.ITEMS);
            if (items != null) {
                adapter = new rss_adapter(getActivity(), items);
                listView.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), "An error occured while downloading the rss feed.",
                        Toast.LENGTH_LONG).show();
            }
            items.remove(0);    //Der erste RSS eintrag war immer doppelt, dies entfernt eine Kopie!
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