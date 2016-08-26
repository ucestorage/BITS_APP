package uce.bits_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ubbo Eicke on 26.08.2016.
 */
public class fm_googlecalendar extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.blank, container, false);

    Intent intent = new Intent(getActivity(), api_googlecalendar.class);
        startActivity(intent);
        return v;
    }


}
