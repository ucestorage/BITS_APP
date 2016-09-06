package uce.bits_app;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.settingsfm, container, false);

        //Nikrandt Spinner für Fachbereich

        final Spinner spinner = (Spinner) v.findViewById(R.id.spinner2);
        Button cbutton = (Button) v.findViewById(R.id.chanbutton);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(),
                R.array.channel, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        cbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences mPrefs = getContext().getSharedPreferences("chat", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putString("channel", spinner.getSelectedItem().toString()).commit();
                Toast.makeText(getContext(), "Channel auf "+spinner.getSelectedItem().toString()+" gesetzt", Toast.LENGTH_LONG).show();

            }
        });


        // Nikrandt ende


        return v;
    }

}