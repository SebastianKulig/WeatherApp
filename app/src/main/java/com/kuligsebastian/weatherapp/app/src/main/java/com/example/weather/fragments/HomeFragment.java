package com.example.weather.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.weather.R;
import com.example.weather.api.Permissions;
import com.example.weather.api.NetworkClient;
import com.example.weather.api.WeatherAPIs;
import com.example.weather.api.WeatherImage;
import com.example.weather.model.WeatherResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


import static java.lang.Math.abs;
public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentHomeListener listener;
    private String cityNameText = null;
    private CheckBox checkBox;
    private boolean isAddButtonPressed = false;
    private static final String API_KEY="06d5e4957656768f786a9fd4dac684f0";
    private static final String UNITS= "metric";

    private TextView current_weather;
    private ImageView current_weather_icon, main_secondary_screen_weather_week_day_first_icon, main_secondary_screen_weather_week_day_second_icon, main_secondary_screen_weather_week_day_third_icon;
    private TextView main_secondary_screen_weather_first, main_secondary_screen_weather_second, main_secondary_screen_weather_third, main_secondary_screen_weather_week_day_first,
            main_secondary_screen_weather_week_day_second,main_secondary_screen_weather_week_day_third, cityName;

    private double lat, tmp_lat, lon, tmp_lon;

    private LocationRequest mLocationRequest;
    private boolean localizationSwitch = true;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private long UPDATE_INTERVAL = 1 * 1000;  /* 1 secs */
    private long FASTEST_INTERVAL = 1000; /* 1 sec */


    public interface FragmentHomeListener {
        void onInputHomeSent(String data);
        void onInputHomeCitySent(String data);
        void onInputHomeCityRemoved(String data);
        boolean checkPresence(String name);
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional,
     * and non-graphical fragments can return null. This will be called between onCreate(Bundle)
     * and onActivityCreated(Bundle)
     *
     * @param inflater: The LayoutInflater object that can be used to inflate any
     *                  views in the fragment,
     *
     * @param container: If non-null, this is the parent view that the fragment's UI should
     *                   be attached to. The fragment should not add the view itself,
     *                   but this can be used to generate the LayoutParams of the view.
     *
     * @param savedInstanceState : If non-null, this fragment is being re-constructed from
     *                             a previous saved state as given here.
     **/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        checkBox = view.findViewById(R.id.add);
        checkBox.post(new Runnable() {
            @Override
            public void run() {
                checkBox.setChecked(isAddButtonPressed);
            }
        });

        checkBox.setOnClickListener(this);
        cityName = view.findViewById(R.id.city_name);

        current_weather = view.findViewById(R.id.current_weather);
        current_weather_icon =  view.findViewById(R.id.current_weather_icon);
        main_secondary_screen_weather_first = view.findViewById(R.id.main_secondary_screen_weather_first);
        main_secondary_screen_weather_second = view.findViewById(R.id.main_secondary_screen_weather_second);
        main_secondary_screen_weather_third = view.findViewById(R.id.main_secondary_screen_weather_third);

        main_secondary_screen_weather_week_day_first = view.findViewById(R.id.main_secondary_screen_weather_week_day_first);
        main_secondary_screen_weather_week_day_second = view.findViewById(R.id.main_secondary_screen_weather_week_day_second);
        main_secondary_screen_weather_week_day_third = view.findViewById(R.id.main_secondary_screen_weather_week_day_third);

        main_secondary_screen_weather_week_day_first_icon = view.findViewById(R.id.main_secondary_screen_weather_week_day_first_icon);
        main_secondary_screen_weather_week_day_second_icon = view.findViewById(R.id.main_secondary_screen_weather_week_day_second_icon);
        main_secondary_screen_weather_week_day_third_icon = view.findViewById(R.id.main_secondary_screen_weather_week_day_third_icon);

        if (cityNameText != null) {
            cityName.setText(cityNameText);
        }
        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
        fetchWeatherDetails();
    }
    //=================================================API==================================================
    private void fetchWeatherDetails() {
        Permissions.isInternetEnable(getContext());
        //Obtain an instance of Retrofit by calling the static method.
        Retrofit retrofit = NetworkClient.getRetrofitClient();
        /*
        The main purpose of Retrofit is to create HTTP calls from the Java interface based on the annotation associated with each method. This is achieved by just passing the interface class as parameter to the create method
        */
        WeatherAPIs weatherAPIs = retrofit.create(WeatherAPIs.class);
        /*
        Invoke the method corresponding to the HTTP request which will return a Call object. This Call object will used to send the actual network request with the specified parameters
        */
        Call call;
        if(cityNameText != null && !localizationSwitch){
            call = weatherAPIs.getWeatherByCity(cityNameText, UNITS, API_KEY);
            isAddButtonPressed = listener.checkPresence(cityNameText);
            checkBox.setChecked(isAddButtonPressed);
        } else {
            call = weatherAPIs.getWeatherByCoordinates(lat, lon, UNITS, API_KEY);
        }
        /*
        This is the line which actually sends a network request. Calling enqueue() executes a call asynchronously. It has two callback listeners which will invoked on the main thread
        */
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WeatherResponse POJO class
                 */
                if (response.body() != null) {
                    WeatherResult weatherResult = (WeatherResult) response.body();
                    String city = weatherResult.city.getName();
                    cityName.setText(city);
                    isAddButtonPressed = listener.checkPresence(city);
                    checkBox.setChecked(isAddButtonPressed);
                    int hour12=Integer.parseInt(weatherResult.getList().get(0).dt_txt.substring(11,13));
                    current_weather.setText(getString(R.string.main_weather_message, (int) weatherResult.getList().get(0).main.getTemp(),
                            weatherResult.getList().get(0).getMain().getPressure(),
                             weatherResult.getList().get(0).wind.getSpeed()));
                    int indexToDisp = ((24 - hour12)/3);

                    main_secondary_screen_weather_first.setText(getString(R.string.first_day_weather_message, weatherResult.getList().get(indexToDisp + 4).main.getTemp()));
                    main_secondary_screen_weather_second.setText(getString(R.string.second_day_weather_message, weatherResult.getList().get(indexToDisp + 12).main.getTemp()));
                    main_secondary_screen_weather_third.setText(getString(R.string.third_day_weather_message, weatherResult.getList().get(indexToDisp + 20).main.getTemp()));

                    main_secondary_screen_weather_week_day_first.setText(weatherResult.getList().get(indexToDisp+4).dt_txt.substring(0, 10));
                    main_secondary_screen_weather_week_day_second.setText(weatherResult.getList().get(indexToDisp+12).dt_txt.substring(0, 10));
                    main_secondary_screen_weather_week_day_third.setText(weatherResult.getList().get(indexToDisp+20).dt_txt.substring(0, 10));

                    String icon = weatherResult.getList().get(0).getWeather().get(0).getIcon();
                    current_weather_icon.setImageResource(WeatherImage.switchImage(icon));

                    String icon1 = weatherResult.getList().get(indexToDisp+4).getWeather().get(0).getIcon();
                    main_secondary_screen_weather_week_day_first_icon.setImageResource(WeatherImage.switchImage(icon1));

                    String icon2 = weatherResult.getList().get(indexToDisp+12).getWeather().get(0).getIcon();
                    main_secondary_screen_weather_week_day_second_icon.setImageResource(WeatherImage.switchImage(icon2));

                    String icon3 = weatherResult.getList().get(indexToDisp+20).getWeather().get(0).getIcon();
                    main_secondary_screen_weather_week_day_third_icon.setImageResource(WeatherImage.switchImage(icon3));
                } else {
                    Toast.makeText(getContext(), getString(R.string.wrong_city_name), Toast.LENGTH_LONG).show();
                    cityName.setText(getString(R.string.wrong_city_name));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t){
            }
        });
    }

    protected void startLocationUpdates() {
        if (Permissions.checkPermissions(getContext())) {
            if (Permissions.isLocationEnabled(getActivity())) {
                // Create the location request to start receiving updates
                mLocationRequest = new LocationRequest();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(UPDATE_INTERVAL);
                mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
                // Create LocationSettingsRequest object using location request
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(mLocationRequest);

                LocationSettingsRequest locationSettingsRequest = builder.build();

                SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
                settingsClient.checkLocationSettings(locationSettingsRequest);

                fusedLocationProviderClient = new FusedLocationProviderClient(getActivity());
                fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                onLocationChanged(locationResult.getLastLocation());
                            }
                        },
                        Looper.myLooper());
            } else {
                Toast.makeText(getContext(), getString(R.string.turn_on_GPS), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            Permissions.requestPermissions(getActivity());
        }
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        tmp_lat  = location.getLatitude();
        tmp_lon = location.getLongitude();
        Log.d("lok:", String.valueOf(tmp_lat));
        Log.d("lok:", String.valueOf(tmp_lon));
        if( abs(tmp_lat - lat) > 0.01 || abs(tmp_lon - lon)> 0.01) {
            lat = tmp_lat;
            lon = tmp_lon; //check if change isn't too small
            fetchWeatherDetails();
        }
    }

    public void updateCityName(String name) {
        cityNameText = name;
        localizationSwitch = false;
    }

    public String getCityNameText(){
        return (String) cityName.getText();
    }

    public void setCheckbox(boolean newState) {
        isAddButtonPressed = newState;
        checkBox.setChecked(newState);
    }

    public void localizationSwitchState(boolean state){
        localizationSwitch = state;
        if(localizationSwitch){
            cityNameText = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                if (checkBox.isChecked()) {
                    listener.onInputHomeCitySent(String.valueOf(cityName.getText()));
                    Toast.makeText(getContext(), R.string.add_home_toast,Toast.LENGTH_LONG).show();
                    isAddButtonPressed = !isAddButtonPressed;
                } else {
                    listener.onInputHomeCityRemoved(String.valueOf(cityName.getText()));
                    Toast.makeText(getContext(), R.string.del_home_toast,Toast.LENGTH_LONG).show();
                    isAddButtonPressed = !isAddButtonPressed;
                }
        }
    }
    /**
     * Called when a fragment is first attached to its context (Main Activity).
     * onCreate(android.os.Bundle) will be called after this.
     * If you override this method you must call through to the superclass implementation.
     * To ensure that the host activity implements this interface, fragment A's onAttach() callback
     * method (which the system calls when adding the fragment to the activity) instantiates an
     * instance of FragmentFavouritesListener by casting the Activity that is passed into onAttach():
     * @param context: Context
     **/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHomeListener) {
            listener = (FragmentHomeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentHomeListener");
        }
    }
    /**
     * Called when the fragment is being disassociated from the activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    /**
     * Called when the view hierarchy associated with the fragment is being removed.
     */
    @Override
    public void onStop() {
        super.onStop();
        listener.onInputHomeSent(cityNameText);
    }
    @Override
    public void onPause() {
        super.onPause();
        if(fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
}