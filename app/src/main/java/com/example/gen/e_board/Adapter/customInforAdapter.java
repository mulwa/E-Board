package com.example.gen.e_board.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.gen.e_board.Pojo.Event;
import com.example.gen.e_board.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class customInforAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;
    public customInforAdapter(Context context) {
        this.context = context;

    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.inforcustom_layout,null);
        TextView tvTitle = view.findViewById(R.id.event_title);
        TextView tvTime = view.findViewById(R.id.event_time);
        TextView tvDesc = view.findViewById(R.id.event_desc);
        TextView tvDate = view.findViewById(R.id.eventDate);
        TextView tv_target = view.findViewById(R.id.tvTarget);
        TextView tv_address = view.findViewById(R.id.tvAddress);
        TextView tv_cost = view.findViewById(R.id.tvCost);

        tvTitle.setText(marker.getTitle());
        tvDesc.setText(marker.getSnippet());

        Event event = (Event) marker.getTag();
        tvTime.setText(event.getEventTime());
        tvDate.setText(event.getEventDate());
        tv_address.setText(event.getPlaceName());
        tv_target.setText(event.getTargetGroup());
        tv_cost.setText(event.getCost());
        return view;
    }
}
