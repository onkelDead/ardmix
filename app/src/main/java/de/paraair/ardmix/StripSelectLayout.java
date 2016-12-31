package de.paraair.ardmix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by onkel on 19.10.16.
 */

public class StripSelectLayout extends ListView {

    OnClickListener onClickListener;
    private Context context;
    private ArrayAdapter<Bank.Strip> adapter;

    public StripSelectLayout(Context context) {
        super(context);
        this.context = context;

    }

    public void setRoutes(HashMap<Integer, Track> routes, Bank bank) {

        Bank b = new Bank();

        for( int index : routes.keySet()) {
            Track t = routes.get(index);
            if(t.type == Track.TrackType.AUDIO || t.type == Track.TrackType.BUS ) {
                b.add(t.name, t.remoteId, bank.contains(t.remoteId),t.type);
            }
        }
        adapter = new TrackAdapter(context, b.getStrips());

        this.setAdapter(adapter);

    }

    public void selectAll() {
        for( int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, null, this);
            CheckBox tvName = (CheckBox) view.findViewById(R.id.checkBox);
            tvName.setChecked(true);
        }
    }

    private class TrackAdapter extends ArrayAdapter<Bank.Strip> {
        public TrackAdapter(Context context, ArrayList<Bank.Strip> strip ) {
            super(context, 0, strip);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Bank.Strip strip = getItem(position);


            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.strip_select_item, parent, false);
            }
            // Lookup view for data population
            CheckBox tvName = (CheckBox) convertView.findViewById(R.id.checkBox);

            // Populate the data into the template view using the data object
            tvName.setText(strip.name + " - " + Track.getTrackTypeName(strip.type));
            tvName.setTag(strip.id);
            tvName.setChecked(strip.enabled);
            tvName.setOnClickListener(onClickListener);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
