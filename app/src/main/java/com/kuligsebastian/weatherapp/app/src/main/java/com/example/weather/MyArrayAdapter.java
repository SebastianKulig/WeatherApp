package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.weather.model.FavouritesModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Custom adapter to allow the change of background of TextView which holds favourite city
 */
public class MyArrayAdapter extends BaseAdapter {

    private Context context;
    private int resource;
    private List<FavouritesModel> favourites;
    private LayoutInflater layoutInflater;

    static class ViewHolder{
        TextView textView;
    }

    public MyArrayAdapter(@NonNull Context context, int resource, @NonNull List<FavouritesModel> objects) {
        this.context = context;
        this.resource = resource;
        this.favourites = objects;
        layoutInflater = LayoutInflater.from(context);
    }

    public void updateData(ArrayList<FavouritesModel> newData){
        this.favourites = newData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return favourites.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     * @param position The position of the item within the adapter's data set of the item whose
     *                 view we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this
     *                    view is non-null and of an appropriate type before using. If it is not
     *                    possible to convert this view to display the correct data, this method
     *                    can create a new view.
     * @param parent The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_element_list, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.list_item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FavouritesModel model = favourites.get(position);
        holder.textView.setText(model.getCityName());

        if (model.isSelected()) {
            holder.textView.setBackgroundResource(R.color.colorAccentTrans);
        } else {
            holder.textView.setBackgroundResource(android.R.color.transparent);
        }

        return convertView;
    }
}