package uce.bits_app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.Space;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.actions.ItemListIntents;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dennis Nikrandt on 9/6/2016.
 * Diese Klasse kümmert sich um den Chat Client, da dies eigentlich Herr Wüstenbeckers Aufgabe
 * war und dieser die Gruppe 2 Tage vor der Abgabe verlassen hat, ist diese Klasse in dem Zeitraum
 * von mir programmiert worden. Ich bitte Sie dies im Kopf zu behalten.
 *
 * Das Chat Fragment besteht aus zwei Teilen, dem normalen Thread und dem AsyncTask welches die
 * Netzwerk Operationen durchführt, als resultat dafür, sind fast alle Teile des Chats dort
 * programmiert.
 *
 */
public class chat extends Fragment {
    private static Button send;
    private static Button list;
    private static  View vi;
    private static String name;
    private WeakReference<MyAsyncTask> asyncTaskWeakRef;
    private static TextView chatfenster;
    private static TextView channel;
    private static Activity act;
    private static int i=1;
    private static EditText input ;
    private static int chatid=0;
    private static   LinearLayout layout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.chat, container, false);
        vi=v;
        input = (EditText) v.findViewById(R.id.chat);
        final LayoutInflater inflater2=inflater;
        final ViewGroup container2=container;
        SharedPreferences mPrefs = vi.getContext().getSharedPreferences("chat", 0);
        String username = mPrefs.getString("username", "");
        final uce.bits_app.chat app = this;
        final SharedPreferences.Editor mEditor = mPrefs.edit();
        //Zunächst clear und dann requeste ich den Focus für das Textfeld Input.
        //Danach erzeuge ich einen Focus Change Listener welcher die Soft Tastatur anzeigen
        //oder ausblenden kann.
        input.clearFocus();
        input.requestFocus();
        input.setText("");
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                Log.d("uce.bits_app","On Foucs. Has Focus = " + hasFocus);

                if (hasFocus)
                {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow
                            (v.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                }
                else
                {
                    //close keyboard
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            v.getWindowToken(), 0);
                }
            }
        });



        act=getActivity();
        layout = (LinearLayout) vi.findViewById(R.id.chatfenster);
        channel = (TextView) vi.findViewById(R.id.channelanzeige);

        channel.setText(  Html.fromHtml("<b><u>"+mPrefs.getString("channel", "HS-OWL") +"</u></b>") );
        LayoutInflater li = LayoutInflater.from(this.getContext());
        View promptsView = li.inflate(R.layout.namenswahl, null);
        //Sofern der User die App zum ersten mal startet(oder die Einstellungen gelöscht hat)
        //wird er gefragt seinen Vor und Nachnamen einzugeben.
        if(username.equals(""))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle("Bitte Vor- und Nachname eingeben, dieser kann nicht mehr geändert werden.");
            builder.setView(promptsView);
           final EditText field = (EditText)promptsView.findViewById(R.id.username);
            builder.setPositiveButton("Namen eingeben", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {



                    String user=field.getText().toString();
                    user=user.replace(" ", "_");
                    Log.d("uce.bits_app","name "+  user);
                    mEditor.putString("username", user).commit();
                    mEditor.putString("channel", "HS-OWL").commit();
                    //Setze Namen wobei Leerzeichen mit _ ersetzt werden und anschließen setze
                    //den Channel auf HS-OWL da dies der Standart Channel ist.
                    name=user;
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

    public static void text(final String sender, final String nachricht)
    {
        //Einfache Schreibfunktion, wechselt die Farbe via HTMl Tags
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {



                i=-i;
                String text="<br />";
                text+=sender + ": " + nachricht+"";
                RelativeLayout rel = new RelativeLayout(act.getApplicationContext());
                TextView textview=new TextView(act.getApplicationContext());
                textview.setTextSize(17);
                textview.setTextColor(Color.parseColor("#131313"));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,1);

                RelativeLayout.LayoutParams params2=params;
                params2.setMargins(5,5,5,0);
                rel.setLayoutParams(params2);
                textview.setWidth(layout.getWidth()/2);
                textview.setLayoutParams(params);
                textview.setId(chatid);
                if(sender.equals(name))
                {
                    rel.setGravity(Gravity.RIGHT);
                    if(i==1)
                    {
                        textview.setBackgroundResource(R.drawable.rounded_corner2);
                    }
                    else
                    {
                        textview.setBackgroundResource(R.drawable.rounded_corner);
                    }
                }
                else
                {
                    rel.setGravity(Gravity.LEFT);
                    if(i==1)
                    {
                        textview.setBackgroundResource(R.drawable.rounded_corner2);
                    }
                    else
                    {
                        textview.setBackgroundResource(R.drawable.rounded_corner);
                    }

                }
                textview.append(Html.fromHtml((text)));
                textview.append(Html.fromHtml(("<br />")));

                rel.addView(textview);
                layout.addView(rel);

                    final ScrollView scroll = (ScrollView) vi.findViewById(R.id.chatscroll);
                scroll.post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.fullScroll(View.FOCUS_DOWN);
                    }
                });

                chatid=chatid+1;
            }
        });

    }
    private static BufferedReader in = null;
    private static PrintWriter out = null;
    private static Socket socket;

    public static void start() throws NumberFormatException, IOException {


        try {
            send = (Button) vi.findViewById(R.id.sbutton);
            list = (Button) vi.findViewById(R.id.list);


            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Wenn der Senden Button gedrückt wird nehme den Inhalt des Input Felds und
                    //schicke diesen an den Server

                    String buffer=input.getText().toString();

                    if (buffer.trim() !="")
                    {
                        out.println("nachricht " + name + " " + buffer);
                        out.flush();
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                input.setText("");
                            }
                        });
                    }
                }
            });
            list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Wenn der Listen Button gedrückt sende dem Server eine Listen Request

                    out.println("liste " + name );
                    out.flush();

                    }

            });

            if (socket.isConnected()) {

                System.out.println("writing");
                try {
                    //On Connect muss sich der Client mit einem Namen registrieren.
                    out.println("clientrequest "+name);
                    out.flush();

                    SharedPreferences mPrefs = vi.getContext().getSharedPreferences("chat", 0);
                    String mString = mPrefs.getString("channel", "HS-OWL");
                    //Der Client wählt einen Channel in dem er schreiben will.
                    out.println("channel "+name+ " "+ mString);
                    out.flush();

                } catch (Exception e) {
                    System.out.println(e);
                }
                new Thread(new Runnable() {
                    public void run() {
                        while (true) {

                            String lineb = null;
                            try {
                                lineb = in.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(lineb !=null) {

                                if (lineb.startsWith("nachricht")) {
                                    //Wenn die vom Server erhaltene Nachricht mit nachricht anfängt
                                    //Teile sie nach namen und nachricht und schreibe sie in das
                                    //Textfeld
                                    String nbuffer[] = lineb.split("\\s+", 3);
                                    final String sender = nbuffer[1];
                                    final String nachricht = nbuffer[2];

                                 text(sender,nachricht);

                                }
                                if (lineb.startsWith("liste")) {
                                    //Wenn die vom Server erhaltene Nachricht mit liste anfängt
                                    //Teile sie und schreibe sie in das extra Fenster

                                    String nbuffer[] = lineb.split("\\s+",100);
                                    String message="";
                                    int z=1;
                                    do {
                                        if(nbuffer[z]!=null)
                                        {
                                            message+=nbuffer[z]+"\n";
                                        }

                                        z=z+1;
                                    }while(z<nbuffer.length);
                                    final String message2=message;
                                    act.runOnUiThread(new Runnable() {
                                        public void run() {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(act);
                                            builder.setTitle("Dies sind alle Leute in deinem Channel:")
                                                    .setMessage(message2)
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                        }
                                                    });
                                            AlertDialog alert = builder.create();
                                            alert.show();

                                        }
                                    });



                                }

                            }
                        }
                    }
                }).start();



            }

        } catch (IOException e) {
            //Sofern der Server nicht erreichbar ist, sage dies dem Nutzer und versuche es in einer
            //Minute erneut
            e.printStackTrace();
            text("System","Server nicht erreichbar!");
            if (e.toString().contains("refused")) {
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e1) {
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
            //Verbinde Socket und dann starte die Client Logik
                try {
                    socket = new Socket("medivhus.ddns.net", 1038);
                    try {
                        start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                  e.printStackTrace();
                }
            if(socket==null)
            {
                text("System","Server nicht erreichbar");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);

        }
        public void selfRestart() {
            loadTextDataTask = new MyAsyncTask(fragments);
        }
    }
    public static void channel(String chan)
    {
        //Funktion damit andere Seiten/settings.fm/ den channel wechseln können.
        out.println("channel "+chan);
        out.flush();
    }
    public static void end()
    {
        //End Funktion die vom Menu aufgerufen wird, schickt dem Server die Goodbye Nachricht
        if(out!=null) {
            out.println("close " + name);
            out.flush();
        }
    }


}
