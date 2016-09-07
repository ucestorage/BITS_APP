package uce.bits_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private  chat c1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //WebView
       WebViewFragment();
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){

                    //Nikrandt
                    /*
                    Jedes Menu Item guckt ob ein chat gestartet war, wenn ja sendet es den
                    Verbindungsabbruch befehl um multible Verbindungen zu verbieten.
                     */
                    //Replacing the main content with fm_Indoor_navi Which is our Inbox View;
                    case R.id.start:
                        if(c1!=null)
                        {
                            c1.end();
                        }
                        Toast.makeText(getApplicationContext(),"Willkommen!",Toast.LENGTH_SHORT).show();
                        WebViewFragment();
                        return true;

                    case R.id.indoornavi:
                        if(c1!=null)
                        {
                            c1.end();
                        }
                        Toast.makeText(getApplicationContext(),"Zu implementieren...",Toast.LENGTH_SHORT).show();


                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.raumplan:
                        if(c1!=null)
                        {
                            c1.end();
                        }
                        Toast.makeText(getApplicationContext(),"Ausfälle und Verschiebungen werden geladen...",Toast.LENGTH_SHORT).show();
                        WebViewFragment2();
                        return true;
                    case R.id.anleitung:
                        if(c1!=null)
                        {
                            c1.end();
                        }
                        //  Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                        AnleitungStundenplanFragment();
                        return true;
                    case R.id.stundenplan:
                        if(c1!=null)
                        {
                            c1.end();
                        }
                      //  Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                        GoogleCalendarFragment();
                        return true;
                    case R.id.news:
                        Toast.makeText(getApplicationContext(),"News Ticker",Toast.LENGTH_SHORT).show();
                       RSSFragment();
                        if(c1!=null)
                        {
                            c1.end();
                        }
                        return true;
                    case R.id.chat:
                        if(c1!=null)
                        {
                            c1.end();
                        }
                        Toast.makeText(getApplicationContext(), "Chat", Toast.LENGTH_SHORT).show();
                        chat();
                        return true;


                    default:
                        Toast.makeText(getApplicationContext(),"Hallo!",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();






    }
    private void WebViewFragment () {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new fm_WebView());
        transaction.commit();

    }
    private void WebViewFragment2 () {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new fm_verschiebungen());
        transaction.commit();

    }
    private void RSSFragment () {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new rss_feed_fm());
        transaction.commit();
    }
    private void AnleitungStundenplanFragment () {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new fm_anleitungstundenplan());
        transaction.commit();
    }
    private void GoogleCalendarFragment () {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new fm_googlecalendar());
        transaction.commit();
    }
    private void chat() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        c1=new chat(); //Neuen Chat als instanz speicher um später ende() aufzurufen
        transaction.replace(R.id.container, c1);
        transaction.commit();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.einstellungen) {
            Toast.makeText(getApplicationContext(),"Einstellungen",Toast.LENGTH_SHORT).show();
            FragmentTransaction ftsettings = getSupportFragmentManager().beginTransaction();
            ftsettings.replace(R.id.container, new fm_Settings());
            ftsettings.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
