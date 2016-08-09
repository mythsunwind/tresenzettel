package net.nostate.drugstore.tresenzettel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.nostate.drugstore.tresenzettel.models.Beverage;

import java.util.List;

public class BeverageCountAdapter extends ArrayAdapter<Beverage> {

    private static class ViewHolder {
        TextView name;
        TextView cases;
        TextView bottles;
    }

    public BeverageCountAdapter(Context context, List<Beverage> beverages) {
        super(context, 0, beverages);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.beverage_with_count, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.beverage_label);
            viewHolder.cases = (TextView) convertView.findViewById(R.id.cases_label);
            viewHolder.bottles = (TextView) convertView.findViewById(R.id.bottles_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Beverage beverage = getItem(position);

        // Populate the data into the template view using the data object
        viewHolder.name.setText(beverage.getName());
        viewHolder.cases.setText(String.valueOf(beverage.getCases()));
        viewHolder.bottles.setText(String.valueOf(beverage.getBottles()));
        // Return the completed view to render on screen
        return convertView;
    }
}
