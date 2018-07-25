package com.example.gen.e_board.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gen.e_board.Pojo.Event;
import com.example.gen.e_board.R;

import java.util.List;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.myViewHolder>{
    List<Event> eventList;

    public MyEventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_row,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.mEventTitle.setText(event.getEventName());
        holder.mEventDesc.setText(event.getEventDesc());
        holder.mEventLocation.setText(event.getPlaceName());
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        public TextView mEventTitle,mEventDesc,mEventLocation;
        public ImageButton mDelete;

        public myViewHolder(View itemView) {
            super(itemView);
            mEventTitle = itemView.findViewById(R.id.tv_eventTitle);
            mEventDesc = itemView.findViewById(R.id.tv_eventDesc);
            mEventLocation = itemView.findViewById(R.id.tv_eventLocation);
            mDelete = itemView.findViewById(R.id.img_deleteEvent);
        }
    }
}
