package uce.bits_app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private chat c1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar initialisieren und als ActionBar setzen
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //NavigationView initialisieren
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //Startseite der Hochschule aufrufen, Fragment wird in den ViewContainer übergeben
        WebViewFragment();
        //ItemClickListener auf den NagivationView setzen, der die Klicks auf die Menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Überprüfung ob das Menü Item gewählt wurde, falls nicht wähle es
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Menü be Item Klick schließen
                drawerLayout.closeDrawers();

                //Überprüfen welches Item geklickt wurde und die zugehörige Action ausführen
                switch (menuItem.getItemId()) {

                    //Nikrandt
                    /*
                    Jedes Menu Item guckt ob ein chat gestartet war, wenn ja sendet es den
                    Verbindungsabbruch befehl um multible Verbindungen zu verbieten.
                     */
                    //Beim Start der App wird die Hochschulseite durch das Fragment aufgerufen
                    //es wird die Nachricht "Willkommen" in einem sogenannten Toast gezeigt
                    case R.id.start:
                        if (c1 != null) {
                            chat.end();
                        }
                        Toast.makeText(getApplicationContext(), "Willkommen!", Toast.LENGTH_SHORT).show();
                        WebViewFragment();
                        return true;

                    case R.id.indoornavi:
                        if (c1 != null) {
                            chat.end();
                        }
                        Toast.makeText(getApplicationContext(), "Zu implementieren...", Toast.LENGTH_SHORT).show();


                        return true;


                    case R.id.raumplan:
                        if (c1 != null) {
                            chat.end();
                        }
                        Toast.makeText(getApplicationContext(), "Ausfälle und Verschiebungen werden geladen...", Toast.LENGTH_SHORT).show();
                        WebViewFragment2();
                        return true;
                    case R.id.anleitung:
                        if (c1 != null) {
                            chat.end();
                        }

                        AnleitungStundenplanFragment();
                        return true;
                    case R.id.stundenplan:
                        if (c1 != null) {
                            chat.end();
                        }

                        GoogleCalendarFragment();
                        return true;
                    case R.id.news:
                        Toast.makeText(getApplicationContext(), "News Ticker", Toast.LENGTH_SHORT).show();
                        RSSFragment();
                        if (c1 != null) {
                            chat.end();
                        }
                        return true;
                    case R.id.chat:
                        if (c1 != null) {
                            chat.end();
                        }
                        Toast.makeText(getApplicationContext(), "Chat", Toast.LENGTH_SHORT).show();
                        chat();
                        return true;


                    default:
                        Toast.makeText(getApplicationContext(), "Hallo!", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // DrawerLayout und ActionBarToggle initialisieren
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
            }
        };

        //ActionBarToggle dem DrawerLayout zuweisen
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //SyncState für die ActionBarToggle aufrufen,
        // da sonst die 3 Striche für das Menü oben links nicht angezeigt werden
        actionBarDrawerToggle.syncState();
    }

    private void WebViewFragment() {
        //Methode zum aufrufe neines neuen Fragments,
        // es wird eine neue Transaction erstellt,
        // diese ersetzt das alte Fragment mit einem neuen,
        // und übergibt das ganze
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new fm_Startseite());
        transaction.commit();

    }

    private void WebViewFragment2() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new fm_verschiebungen());
        transaction.commit();

    }

    private void RSSFragment() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new rss_feed_fm());
        transaction.commit();
    }

    private void AnleitungStundenplanFragment() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new fm_anleitungstundenplan());
        transaction.commit();
    }

    private void GoogleCalendarFragment() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new fm_googlecalendar());
        transaction.commit();
    }

    private void chat() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        c1 = new chat(); //Neuen Chat als instanz speicher um später ende() aufzurufen
        transaction.replace(R.id.container, c1);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Dies fügt die 3 Punkte rechts hinzu, Einstellungs Menü
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // In dieser Methode werden die Klicks auf die Einstellungs Menü Items gehandelt
        int id = item.getItemId();

//falls es mehrere items gibt wird das if sinnvoll
        if (id == R.id.einstellungen) {
            Toast.makeText(getApplicationContext(), "Einstellungen", Toast.LENGTH_SHORT).show();
            //fragment transaction bei item klick
            FragmentTransaction ftsettings = getSupportFragmentManager().beginTransaction();
            ftsettings.replace(R.id.container, new fm_Settings());
            ftsettings.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
