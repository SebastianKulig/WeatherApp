package com.example.weather.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weather.fragments.*;
import com.example.weather.MyArrayAdapter;
import com.example.weather.R;
import com.example.weather.model.FavouritesModel;

import java.util.ArrayList;

/**
 * Fragment that displays list of favourites cities, it allows to delete selected ones (selection
 * after long click) or to display current weather for a chosen city (normal click) - go to the
 * homeFragment
 */

public class FavouritesFragment extends Fragment implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener {

    private FragmentFavouritesListener listener;
    private ArrayList<FavouritesModel> listItems = new ArrayList<FavouritesModel>();
    private MyArrayAdapter myListAdapter;
    private ListView listView;
    private ImageButton del;
    private TextView delText;
    private ArrayList<View> views = new ArrayList<View>();


    /**
     * interface to provide communication between Fragments/Activities through the MainActivity
     */
    public interface FragmentFavouritesListener {

        /**
         * sends data to the activity/class which implements it
         * @param data ArrayList containing favourite cities as objects - type: FavouritesModel
         */
        void onInputFavouritesSent(ArrayList<FavouritesModel> data);

        /**
         * sends only a city's name to HomeFragment
         * @param city city's name
         */
        void onInputFavouritesCitySent(String city, boolean isFavourite);

        /**
         * It checks whether currently displayed on homeFragment city is still favourite after
         * deleting some data by the user for example: home - London, favs - London, Cracow, user
         * deletes London -> we need button update, and that is the job of this method
         * implemented in MainActivity.
         */
        void buttonUpdate();
    }

    /**
     * Called when creating a instance of the fragment. It runs before onCreateView
     * @param savedInstanceState : If non-null, this fragment is being re-constructed from
     *                             a previous saved state as given here.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional,
     * and non-graphical fragments can return null. This will be called between onCreate(Bundle)
     * and onActivityCreated(Bundle)
     *
     * @param inflater:          The LayoutInflater object that can be used to inflate any
     *                           views in the fragment,
     * @param container:         If non-null, this is the parent view that the fragment's UI should
     *                           be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState : If non-null, this fragment is being re-constructed from
     *                           a previous saved state as given here.
     **/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (listItems.isEmpty()) {
            return inflater.inflate(R.layout.fragment_favourites_empty, container, false);
        } else {
            View view = inflater.inflate(R.layout.fragment_favourites, container, false);

            myListAdapter = new MyArrayAdapter(getContext(), R.layout.fragment_element_list, listItems);
            listView = (ListView) view.findViewById(R.id.list_view_list);
            listView.setAdapter(myListAdapter);

            listView.setOnItemLongClickListener(this);
            listView.setOnItemClickListener(this);

            del = (ImageButton) view.findViewById(R.id.button_delete);
            delText = (TextView) view.findViewById(R.id.text_delete);

            return view;
        }
    }

    /**
     * Updates list containing favorite locations, it is called by MainActivity in implementation of
     * FavouritesFragmentListener
     * @param input contains saved locations
     */
    public void updateListItems(ArrayList<FavouritesModel> input){
        listItems = input;
    }

    /**
     * Adds new city to the list
     * @param name: name of the new city
     */
    public void addNewCity(String name) {
        boolean isPresent = isCityFavourite(name);

        if (!isPresent) {
            listItems.add(new FavouritesModel(name, false));
        }
    }

    /**
     * Delete city when you have its name, used in MainActivity
     * @param name: name of a city to remove
     */
    public void deleteCityBasedOnName(String name) {
        for (int i = listItems.size() - 1; i >= 0; i--) {
            if (listItems.get(i).getCityName().equals(name)) {
                listItems.remove(i);
            }
        }
    }

    /**
     * Delete city/cities that was/were previously selected by the user.
     */
    private void deleteCityBasedOnSelection(){
        for (int i = listItems.size() - 1; i >= 0; i--){
            if (listItems.get(i).isSelected()){
                listItems.remove(i);
            }
        }
    }

    /**
     * loops through the ArrayList and checks whether given city is already there
     * @param city: city to check
     * @return true if found else false;
     */
    public boolean isCityFavourite(String city) {
        for (int i = 0; i < listItems.size(); i++){
            if (listItems.get(i).getCityName().toLowerCase().equals(city.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    /**
     * It handles quick click on favourite element
     * @param parent: The AdapterView where the click happened.
     * @param view: The view within the AdapterView that was clicked (this will be a view provided by the adapter)
     * @param position: The position of the view in the adapter.
     * @param id: The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FavouritesModel model = listItems.get(position);
        listener.onInputFavouritesCitySent(model.getCityName(), true);
    }

    /**
     * It defines what happens after user's long click on a element of the ListView
     * @param parent The AbsListView where the click happened, ListView which holds "clickable"
     *               favourite cities
     * @param view The view within the AbsListView that was clicked, clicked favourite city
     * @param position The position of the view in the list, position of the favourite city
     * @param id The row id of the item that was clicked, id of clicked view
     * @return true if the callback consumed the long click, false otherwise
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        myListAdapter.notifyDataSetChanged();
        FavouritesModel model = listItems.get(position);
        if (model.isSelected()) {
            model.setSelected(false);
        } else {
            model.setSelected(true);
        }

        listItems.set(position, model);
        delText.setVisibility(View.VISIBLE);
        del.setVisibility(View.VISIBLE);
        del.setClickable(true);
        del.setOnClickListener(new View.OnClickListener() {
            /**
             * What happens when user clicks it
             * @param v: clicked view, in this case delete icon
             */
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_delete:
                        deleteCityBasedOnSelection();
                        del.setVisibility(View.INVISIBLE);
                        delText.setVisibility(View.INVISIBLE);
                        del.setClickable(false);
                        listener.buttonUpdate();
                        myListAdapter.notifyDataSetChanged();
                        views.clear();
                        break;
                }
            }
        });
        return true;
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
        //checking whether MainActivity (context) implements FragmentFavouritesListener
        if (context instanceof FragmentFavouritesListener) {
            listener = (FragmentFavouritesListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentFavouritesListener");
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
        for (int i = 0; i < listItems.size(); i++){
            if (listItems.get(i).isSelected()){
                listItems.get(i).setSelected(false);
            }
        }
        listener.onInputFavouritesSent(listItems);
    }
}