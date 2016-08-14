package net.nostate.drugstore.tresenzettel;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.nostate.drugstore.tresenzettel.models.CalculationLogEntry;

import java.util.List;

import static net.nostate.drugstore.tresenzettel.models.CalculationLogEntry.LogLevel;

public class CalculationAdapter extends ArrayAdapter<CalculationLogEntry> {

    private static class ViewHolder {
        TextView entry;
    }

    public CalculationAdapter(Context context, List<CalculationLogEntry> logEntries) {
        super(context, 0, logEntries);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.calculation_adapter, parent, false);
            viewHolder.entry = (TextView) convertView.findViewById(R.id.EntryTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CalculationLogEntry logEntry = getItem(position);


        // Populate the data into the template view using the data object
        viewHolder.entry.setText(logEntry.getEntry());
        if(logEntry.getLevel() != LogLevel.VERBOSE) {
            viewHolder.entry.setTypeface(null, Typeface.BOLD);
        }
        viewHolder.entry.setTextColor(setTextColorByLevel(logEntry.getLevel()));
        viewHolder.entry.setBackgroundResource(getBackgroundColorByLevel(logEntry.getLevel()));
        // Return the completed view to render on screen
        return convertView;
    }

    private int getBackgroundColorByLevel(LogLevel level) {
        switch (level) {
            case WARN:
                return R.color.backgroundColorLogEntryWarn;
            case ERROR:
                return R.color.backgroundColorLogEntryError;
            case RESULT:
                return R.color.backgroundColorLogEntryResult;
            default:
                return R.color.backgroundColorLogEntryVerbose;
        }
    }

    private int setTextColorByLevel(LogLevel level) {
        int resource;
        switch (level) {
            case WARN:
                resource = R.color.textColorLogEntryWarn;
                break;
            case ERROR:
                resource =  R.color.textColorLogEntryError;
                break;
            case RESULT:
                resource =  R.color.textColorLogEntryResult;
                break;
            default:
                resource =  R.color.textColorLogEntryVerbose;
                break;
        }
        return getContext().getResources().getColor(resource);
    }
}
