package be.ugent.vopro5.verkeersevents;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by evert on 4/29/16.
 */
public class EventsAdapter extends BaseAdapter {

    private OnClickListener listener;
    private List<Event> eventList;

    public EventsAdapter() {
        eventList = new ArrayList<>();

    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Event event = eventList.get(position);

        View view;
        if(convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event, parent, false);
        }

        TextView description = (TextView) view.findViewById(R.id.description);
        final TextView source = (TextView) view.findViewById(R.id.source);

        description.setText(event.getDescription());
        source.setText(event.getSource());

        Ion.with(source.getContext()).load(event.getSourceImage()).asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                Drawable drawable = null;
                if(result != null) {
                    drawable = new BitmapDrawable(source.getContext().getResources(), result);
                    drawable.setBounds(0, 0, 50, 50);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    source.setCompoundDrawablesRelative(drawable, null, null, null);
                } else {
                    source.setCompoundDrawables(drawable, null, null, null);
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(event);
            }
        });

        return view;
    }

    public void addEvents(Event... events) {
        if(listener == null) {
            throw new IllegalStateException("Set a listener before adding events");
        }

        Collections.addAll(eventList, events);
        notifyDataSetChanged();
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void clear() {
        eventList.clear();
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onClick(Event event);
    }


}
