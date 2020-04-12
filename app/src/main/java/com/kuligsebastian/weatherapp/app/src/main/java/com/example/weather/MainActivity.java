package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.weather.fragments.FavouritesFragment;
import com.example.weather.fragments.HomeFragment;
import com.example.weather.fragments.SearchFragment;
import com.example.weather.fragments.AboutFragment;

import com.example.weather.model.FavouritesModel;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


/**
 * Main activity, the main menu so to speak.
 * It displays footer and weather, it handles fragments.
 * "root" class
 */
public class MainActivity extends AppCompatActivity implements FavouritesFragment.FragmentFavouritesListener,
        HomeFragment.FragmentHomeListener, SearchFragment.FragmentSearchListener {

    BottomNavigationView navBar;
    HomeFragment homeFragment = new HomeFragment();
    FavouritesFragment favouritesFragment = new FavouritesFragment();
    SearchFragment searchFragment = new SearchFragment();
    AboutFragment aboutFragment = new AboutFragment();


    /**
     * Creates the activity.
     * It is the frame which hosts all fragments, it also handles communication between them
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navBar = findViewById(R.id.nav_bar_bottom);
        navBar.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                homeFragment).commit();

    }

    /**
     * Creates listener for navigation bar to detect user's input and make switch to desired Fragment
     * It is outside onCreate() to make it more easy to read and reduce "size" of it
     **/
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                /**
                 * @param item: item that was selected by the user
                 **/
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = homeFragment;
                            break;
                        case R.id.nav_favourites:
                            selectedFragment = favouritesFragment;
                            break;
                        case R.id.nav_search:
                            selectedFragment = searchFragment;
                            break;
                        case R.id.nav_about:
                            selectedFragment = aboutFragment;
                            break;
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
                    fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
                    fragmentTransaction.commit();

                    return true; //display changes to navigation bar, false = do not update nav bar
                }

            };

    /**
     * Handles pressed back button, hitting it on home fragment exits to phone's home screen
     * when user is currently on different fragment app goes to home/current weather
     */
    @Override
    public void onBackPressed() {
        if (navBar.getSelectedItemId() == R.id.nav_home) {
            super.onBackPressed();
        } else {
            navBar.setSelectedItemId(R.id.nav_home);
        }
    }

    /**
     * Called by the system when the device configuration changes while your activity is running.
     * Note that this will only be called if you have selected configurations you would like to
     * handle with the R.attr.configChanges attribute in your manifest. If any configuration change
     * occurs that is not selected to be reported by that attribute, then instead of reporting it
     * the system will stop and restart the activity (to have it launched with the new configuration).
     * It is called because of: <activity android:name=".MainActivity"
     *                  android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"/>
     * in manifest.
     * @param newConfig: The new device configuration. This value must never be null.
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (navBar.getSelectedItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        homeFragment).commit();
                break;
            case R.id.nav_favourites:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        favouritesFragment).commit();
                break;
            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        searchFragment).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        aboutFragment).commit();
                break;
        }
    }


    /**
     * Calls favouritesFragment and updates its data (itemsList) using data that was
     * previously sent from it (return to the sender, kinda)
     * @param data obtained from favouritesFragment
     */
    @Override
    public void onInputFavouritesSent(ArrayList<FavouritesModel> data) {
        favouritesFragment.updateListItems(data);
    }

    /**
     * Calls homeFragment and updates its data (city name) using data that was
     * previously sent from favouritesFragment
     * @param city data obtained from favouritesFragment
     */
    @Override
    public void onInputFavouritesCitySent(String city, boolean isFavourite) {
        searchFragment.changeLocalizationSwitch(false);
        homeFragment.setCheckbox(isFavourite);
        homeFragment.updateCityName(city);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commit();
        navBar.setSelectedItemId(R.id.nav_home);
    }

    /**
     * Calls favourite fragment to check if currently displayed location is still favourite.
     * For details see method declaration in interface in FavouritesFragment
     */
    @Override
    public void buttonUpdate() {
        String name = homeFragment.getCityNameText().toLowerCase();
        boolean isFavourite = favouritesFragment.isCityFavourite(name);
        homeFragment.setCheckbox(isFavourite);
    }

    /**
     * Calls homeFragment and updates its data (cityNameText) using data that was
     * previously sent from it (return to the sender, kinda)
     * @param data obtained from homeFragment
     */
    @Override
    public void onInputHomeSent(String data) {
        homeFragment.updateCityName(data);
    }

    /**
     * Calls favouritesFragment and updates its data (itemsList) using data that was
     * previously sent from homeFragment
     * @param data obtained from homeFragment
     */
    @Override
    public void onInputHomeCitySent(String data) {
        favouritesFragment.addNewCity(data);
    }

    /**
     * Calls favouritesFragment and updates its data (itemsList) using data that was
     * previously sent from homeFragment
     * @param data obtained from homeFragment
     */
    @Override
    public void onInputHomeCityRemoved(String data) {
        favouritesFragment.deleteCityBasedOnName(data);
    }

    /**
     * Receives city name from home fragment and checks if this city is favourite.
     * @param name: city to check
     * @return true if is favourite, otherwise false;
     */
    @Override
    public boolean checkPresence(String name) {
        return favouritesFragment.isCityFavourite(name);

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
        if (actionbar != null) {
            actionbar.hide();
        }
//        makes notification bar completely transparent
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    @Override
    public void onInputSearchSent(String input) {

        homeFragment.updateCityName(input);
        homeFragment.setCheckbox(favouritesFragment.isCityFavourite(input));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commit();
        navBar.setSelectedItemId(R.id.nav_home); // "podswietlenie" odpowiedniego przycisku
    }

    @Override
    public void onInputSearchLocalizationSent(boolean state) {
        homeFragment.localizationSwitchState(state);
    }
//TODO
//    @Override
//    protected void onStop() {
//        super.onStop();
//        String currentCity = homeFragment.getCityNameText();
//        ArrayList<FavouritesModel> listOfFavourites = favouritesFragment.getFa
//    }
}
