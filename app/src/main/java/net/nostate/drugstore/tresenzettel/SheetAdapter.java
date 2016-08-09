package net.nostate.drugstore.tresenzettel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.nostate.drugstore.tresenzettel.models.Sheet;

import java.util.List;

public class SheetAdapter extends ArrayAdapter<Sheet> {

    private static class ViewHolder {
        TextView name;
    }

    public SheetAdapter(Context context, List<Sheet> sheets) {
        super(context, 0, sheets);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.sheet_adapter, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.SheetTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Sheet sheet = getItem(position);

        // Populate the data into the template view using the data object
        viewHolder.name.setText("Tresenzettel #" + sheet.getNumber());
        // Return the completed view to render on screen
        return convertView;
    }
}
