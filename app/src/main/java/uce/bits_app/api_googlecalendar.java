package uce.bits_app;

/**
 * Created by Ubbo on 26.08.2016.
 * Als Grundlage wurde das Tutorial Google Calendar API Quickstart verwendet
 * An einigen Stellen wurde der Code angepasst und kommentiert
 */

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class api_googlecalendar extends Activity
        implements EasyPermissions.PermissionCallbacks {
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String BUTTON_TEXT = "Die nächsten 10 Termine abrufen";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;
    private TextView mOutputText;
    private Button mCallApiButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout innerhalb der Activity erstellen
        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
// Button erstellen, dem button text zuweisen, onclicklistener
// bei klick werden die Resultate vom Kalender abgefragt
        mCallApiButton = new Button(this);
        mCallApiButton.setText(BUTTON_TEXT);
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mOutputText.setText("");
                getResultsFromApi();
                mCallApiButton.setEnabled(true);
            }
        });
        activityLayout.addView(mCallApiButton);
// textview erstellen welches Fehlermeldungen etc ausgibt
        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        mOutputText.setTextSize(20);
        activityLayout.addView(mOutputText);
//Fortschrittsbalken erstellen
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Lade...");

        setContentView(activityLayout);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }


    /* Es wird probiert die Daten aus dem Kalender abzufragen, aber nur wenn gewisse Konditionen
       erfüllt sind, GooglePlayServices müssen installiert sein, das Gerät muss eine
       aktive Internetverbindung haben, und es muss ein Account ausgewählt sein,
       falls eine dieser Konditionen nicht erfüllt ist, wird das Programm dazu auffordern*/
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            mOutputText.setText("Keine Netzwerkverbindung vorhanden.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }


    /*In dieser Mehtode wird der Account gewählt, sie muss die Berechtigung haben die vorhandenen
        Accounts auf dem Gerät abzufragen. Falls noch kein Account gewählt wurde,
        erscheint ein Dialog der dies ermöglicht.*/
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Wähle einen neuen Account aus
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Durch eine Dialog wird noch einmal die Berechtigung erfragt,
            // falls diese noch nicht erteilt wurde.
            EasyPermissions.requestPermissions(
                    this,
                    "Diese Applikation braucht Zugang zu Ihrem Google Account (via Kontakte)",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    //Methode checkt nochmals ob alle Konditionen erfüllt sind
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //Google Play Services müssen installiert sein
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "Diese App benötigt Google Play Services. Bitte installieren Sie " +
                                    "Google Play Services auf Ihrem Gerät und starten Sie die App neu.");
                } else {
                    //wenn sie installiert sind, können Daten abgefragt werden
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                //Überprüfen ob ein Account gewählt wurde, und ob dieser gespeichert wurde
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                //wenn die Authorisierung bestätigt wird, werden daten abgefragt
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Ab Android 6 muss es die Möglichkeit geben Berechtigungen in der Laufzeit anzufragen
     * das tut diese Methode.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
    }

    //Hat das Gerät eine aktive Internetverbindung?
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    //Sind Google Play Services installiert und auf dem neusten Stand?
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Falls Google Play Services veraltet oder nicht richtig installiert sind
     * wird durch einen Dialog der dem Nutzer gezeigt wird, dies veruscht zu beheben.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Zeigt eine Fehlernachricht, das Google Play nicht richtig installiert oder veraltet ist.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                api_googlecalendar.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    // Diese AsyncTask handelt alle Anfragen die an die Google Calendar API gemacht werden ab.
    // Die Abfragen werden in einer eigenen Task gemacht damit das User Interface
    // während der Abfrage flexibel bleibt
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        // neuer calendar service wird erzeugt
        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Termin-Abfrage")
                    .build();
        }

        // Hintergrund Aufgabe um Daten von der API abzufragen
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /* Es wird in allen Kalendern gesucht ob der Titel mit "Stundenplan HS_OWL"
         übereinstimmt, falls dies der Fall ist wird von diesem Kalender die ID abgefragt, diese wird
           in eine Variable übergeben, dann werden die nächsten 10 events des Kalenders, von welchem
            die ID übergeben wurde abgefragt, es werden die Informationen zu dem Termin abgefragt
            (Zeit, Ort, Titel) diese werden als String an eine Array List übergeben und durch diese
            angezeigt*/
        private List<String> getDataFromApi() throws IOException {
            List<String> eventsout = new ArrayList<String>();
            CalendarList calendarList = mService.calendarList().list().execute();
            List<CalendarListEntry> items = calendarList.getItems();
            for (CalendarListEntry calendarListEntry : items) {
                if (calendarListEntry.getSummary().contains("Stundenplan HS-OWL")) {

                    String callist = calendarListEntry.getId();

                    DateTime now = new DateTime(new Date());
                    Events events = mService.events().list(callist)
                            .setMaxResults(10)
                            .setTimeMin(now)
                            .setOrderBy("startTime")
                            .setSingleEvents(true)
                            .execute();
                    List<Event> itemsevent = events.getItems();
                    for (Event event : itemsevent) {

                        DateTime start = event.getStart().getDateTime();
                        if (start == null) {
                            //Für Termine die den ganzen Tag stattfinden, diese haben keine Anfangs
                            //    und Endzeit sondern nur ein Datum
                            start = event.getStart().getDate();
                        }
                        DateTime ende = event.getStart().getDateTime();
                        if (ende == null) {
                            ende = event.getStart().getDate();
                        }
                        String location = event.getLocation();
                        eventsout.add(
                                String.format("Name: %s %nRaum: %s%nStart: (%s) %nEnde:(%s)%n%n", event.getSummary(), location, start, ende));
                    }
                }
            }
            return eventsout;
        }

        //Beim ausführen wird der Ladebalken angezeigt
        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        //Nach dem ausführen wird der Ladebalken versteckt, falls keine Daten abgefragt werden konnten
        // wir eine Nachricht ausgegeben
        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("Keine Ergebnisse.");
            } else {
                output.add(0, "");
                mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        //Error Handling, die "falls etwas schief läuft"-Klasse
        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            api_googlecalendar.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("Ein Fehler ist passiert\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Anfrage abgebrochen.");
            }
        }
    }
}
