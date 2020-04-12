package com.example.weather;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * First screen, quick activity displaying the icon and name,
 * then it goes to the main activity.
 */
public class StartupActivity extends AppCompatActivity {

    /**
     * Creates the activity, calls other activities after 2000 milliseconds = 2 sec.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            Toast.makeText(this, "Please enable WiFi or mobile data", Toast.LENGTH_LONG).show();
            //Intent turnWifiOn = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            //startActivity(turnWifiOn);
        }



//        It pauses program for 2 seconds to display loading screen
//        A Handler allows you to send and process Message and Runnable objects associated with
//        a thread's MessageQueue. Each Handler instance is associated with a single thread and
//        that thread's message queue.
//        There are two main uses for a Handler: (1) to schedule messages and runnables
//        to be executed at some point in the future; and (2) to enqueue an action to be
//        performed on a different thread than your own.
        Handler handler = new Handler();
//        Causes the Runnable r to be added to the message queue, to be run after the specified
//        amount of time elapses. The runnable will be run on the thread to which this handler is
//        attached.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }, 2000);



        }






    /**
     * When user changes the window he's focused on,
     * the app should be still fullscreen.
     *
     * @param hasFocus says if it's fullscreen or running in the background.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUI();

        //hiding action bar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null){
            actionbar.hide();
        }

//        makes notification bar completely transparent
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }



    /**
     * Hides the system UI with several flags.
     * Sets the IMMERSIVE flag.
     * Sets the content to appear under the system bars so that the content
     * doesn't resize when the system bars hide and show.
     */
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }




}