package uce.bits_app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

/**
 * Created by Dennis Nikrandt on 9/6/2016.
 */
public class chat extends Fragment {
    private static Button send;
    private static  EditText input;
    private static  View vi;
    private static String name;
    private WeakReference<MyAsyncTask> asyncTaskWeakRef;
    private Activity act;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.chat, container, false);
        vi=v;
        final LayoutInflater inflater2=inflater;
        final ViewGroup container2=container;
        SharedPreferences mPrefs = vi.getContext().getSharedPreferences("chat", 0);
        String username = mPrefs.getString("username", "");
        final uce.bits_app.chat app = this;
        final SharedPreferences.Editor mEditor = mPrefs.edit();
        act=getActivity();
        LayoutInflater li = LayoutInflater.from(this.getContext());
        View promptsView = li.inflate(R.layout.namenswahl, null);
        username="";
        if(username.equals(""))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle("Bitte Vor- und Nachname eingeben");
            builder.setView(promptsView);
           final EditText field = (EditText)promptsView.findViewById(R.id.username);
            builder.setPositiveButton("Namen eingeben", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {



                    String user=field.getText().toString();
                    Log.d("uce.bits_app","name "+  user);
                    mEditor.putString("username", field.getText().toString()).commit();
                    mEditor.putString("channel", "HS-OWL").commit();
                    startNewAsyncTask();
                }
            });
            builder.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                   act.finish();
                    startActivity(act.getIntent());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else
        {
            name=username;
            startNewAsyncTask();
        }



        return v;
    }
    private static BufferedReader in = null;
    private static PrintWriter out = null;
    private static Socket socket;


    public static void start() throws NumberFormatException, IOException {


        try {
            send = (Button) vi.findViewById(R.id.sbutton);
            final EditText input = (EditText) vi.findViewById(R.id.chat);

            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("uce.bits_app","send "+input.getText().toString());
                    out.println(input.getText().toString());
                    out.flush();
                }
            });

            if (socket.isConnected()) {
                System.out.println("writing");
                try {
                    out.println("clientrequest "+name);
                    out.flush();

                    SharedPreferences mPrefs = vi.getContext().getSharedPreferences("chat", 0);
                    String mString = mPrefs.getString("channel", "HS-OWL");

                    out.println("channel "+name+ " "+ mString);
                    out.flush();

                } catch (Exception e) {
                    System.out.println(e);
                }
                new Thread(new Runnable() {
                    public void run() {
                        System.out.println("threading");
                        while (true) {

                            String lineb = null;
                            try {
                                lineb = in.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.d("uce.bits_app",lineb);

                            if (lineb.equals("hello")) {

                                out.println("Hello back!");
                                out.flush();
                            }




                        }
                    }
                }).start();



            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (e.toString().contains("refused")) {
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                start();
            }

        }
    }
    private void startNewAsyncTask() {
        MyAsyncTask asyncTask = new MyAsyncTask(this);
        this.asyncTaskWeakRef = new WeakReference<MyAsyncTask >(asyncTask );
        asyncTask.execute();
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        public static MyAsyncTask loadTextDataTask;
        private WeakReference<chat> fragmentWeakRef;
        private chat fragments =null;

        private MyAsyncTask (chat fragment ) {
            this.fragmentWeakRef = new WeakReference<chat>(fragment);
            fragments=fragment;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                socket = new Socket("10.0.2.2", 1038);
                try {
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            if (this.fragmentWeakRef.get() != null) {

            }
        }
        public void selfRestart() {
            loadTextDataTask = new MyAsyncTask(fragments);
        }
    }
    public static void channel(String chan)
    {
        out.println("channel "+chan);
        out.flush();
    }

}
