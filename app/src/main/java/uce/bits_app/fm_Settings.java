package uce.bits_app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class fm_Settings extends Fragment {
    private FragmentActivity myContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.settingsfm, container, false);


        //Nikrandt Spinner für Fachbereich
        /*
        Gleiches vorgehen wie im RSS Spinner, Spinner im XML erzeugt, mit Daten aus string xml gefüllt.
        danach hier eine Logik programmiert. Diese reagiert nicht auf die Auswahl des Objekts im Spinner
        sondern auf den Druck des Channel setzen Knopfes.
         */
        myContext=getActivity();
        final Spinner spinner = (Spinner) v.findViewById(R.id.spinner2);
        Button cbutton = (Button) v.findViewById(R.id.chanbutton);
        final View v2=v;
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(),
                R.array.channel, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);
        cbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences mPrefs = getContext().getSharedPreferences("chat", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putString("channel", spinner.getSelectedItem().toString()).commit();
                Toast.makeText(getContext(), "Channel auf "+spinner.getSelectedItem().toString()
                        +" gesetzt", Toast.LENGTH_LONG).show();
                FragmentTransaction transaction =
                        myContext.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new chat());
                transaction.commit();

            }
        });


        // Nikrandt ende


        return v;
    }


}