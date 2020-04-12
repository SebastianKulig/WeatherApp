package com.example.weather.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.weather.MainActivity;
import com.example.weather.R;

import java.util.Random;

public class SearchFragment extends Fragment {
    private FragmentSearchListener listener;
    private EditText cityName;
    private String[] curiosities;
    private int curiositiesSize;
    private Random r;
    private TextView placeToDisplayCuriosities;
    private Switch localizationSwitch;
    private boolean localizationSwitchState = true;

    public interface FragmentSearchListener{
        void onInputSearchSent(String input);
        void onInputSearchLocalizationSent(boolean state);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        curiosities = res.getStringArray(R.array.weather_curiosities);
        curiositiesSize = curiosities.length;
        r = new Random();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        cityName = view.findViewById(R.id.city_name);
        localizationSwitch = view.findViewById(R.id.localization_switch);
        placeToDisplayCuriosities = view.findViewById(R.id.curiosities);
        placeToDisplayCuriosities.setText(curiosities[r.nextInt(curiositiesSize)]);

        localizationSwitch.setChecked(localizationSwitchState);
        cityName.setOnEditorActionListener(editorActionListener);

        localizationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(localizationSwitch.isChecked()){
                    listener.onInputSearchLocalizationSent(true);//on
                    localizationSwitchState=true;
                }else{
                    listener.onInputSearchLocalizationSent(false);   //off
                    localizationSwitchState=false;
                }
            }
        });

        return view;
    }

    public void changeLocalizationSwitch(boolean state){
        localizationSwitchState = state;
        Log.wtf("stan wlaczenia z metody w settings", String.valueOf(state));
    }

    /**
     * editorActionListener checks if there is a user input from EditText
     */
    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        /**
         * This method checks what is the type of special button on the keyboard. Special button is
         * displayed in place of "enter" key, it could be "done", "search" (this case), "next", "send"
         * @param v: EditText
         * @param actionId: "key code" - what was pressed (enter, next, done, ...)
         * @param event: If triggered by an enter key, this is the event; otherwise, this is null.
         * @return boolean: true if you have consumed the action, else false.
         */
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String obtainedText = String.valueOf(cityName.getText());
                if (!obtainedText.isEmpty()) {
                    listener.onInputSearchSent(obtainedText);
                    localizationSwitch.setChecked(false);
                    localizationSwitchState = false;
                    cityName.setText("");
                } else {
                    Toast.makeText(getContext(), "Please enter city name",Toast.LENGTH_LONG).show();
                }
            }
            return false;
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentSearchListener){
            listener=(FragmentSearchListener) context;
        }else{
            throw new RuntimeException(context.toString() + "most implement FragmentSearchListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }
}