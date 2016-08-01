package be.ugent.vopro5.verkeersevents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by evert on 4/29/16.
 */
public class EventListFragment extends Fragment {

    private EventsAdapter eventsAdapter;
    private EventsAdapter.OnClickListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        eventsAdapter = new EventsAdapter();
        ListView eventList = (ListView) view.findViewById(R.id.event_list);
        eventList.setAdapter(eventsAdapter);

        return view;
    }

    public void setOnClickListener(EventsAdapter.OnClickListener listener) {
        this.listener = listener;
        eventsAdapter.setListener(listener);
    }

    public void addEvents(Event... events) {
        if(listener == null) {
            throw new IllegalStateException("Set a listener before adding events");
        }

        eventsAdapter.addEvents(events);
    }

    public void clear() {
        eventsAdapter.clear();
    }
}
