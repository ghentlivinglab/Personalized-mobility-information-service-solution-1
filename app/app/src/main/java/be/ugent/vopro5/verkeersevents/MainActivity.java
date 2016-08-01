package be.ugent.vopro5.verkeersevents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, EventsAdapter.OnClickListener, GetEventsTask.EventProgressListener, GetRoutesTask.RouteProgressListener, GetPointsOfInterestTask.PointOfInterestProgressListener {

    public static final LatLng GHENT = new LatLng(51.055821, 3.715305);
    private EventListFragment listFragment;
    private GoogleMap mMap;
    private Map<Event, Marker> markerMap;
    private SharedPreferences preferences;
    private boolean paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listFragment = (EventListFragment) getSupportFragmentManager().findFragmentById(R.id.event_list_fragment);
        listFragment.setOnClickListener(this);

        markerMap = new HashMap<>();
    }

    private void checkRegistered() {
        if (preferences.getString(Constants.TOKEN, null) == null
                || preferences.getString(Constants.APP_ID, null) == null) {
            // Register with gcm
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkLogin() {
        if (preferences.getString(Constants.TOKEN, null) == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
        boolean loggedIn = checkLogin();
        checkRegistered();
        if (mMap != null && loggedIn) {
            mMap.clear();
            markerMap.clear();
            listFragment.clear();
            updateMap();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(GHENT, 11));
        updateMap();
    }

    private void updateMap() {
        new GetEventsTask(
                preferences.getString(Constants.TOKEN, ""),
                preferences.getString(Constants.USER_ID, ""),
                this, this
        ).execute();
        new GetPointsOfInterestTask(preferences.getString(Constants.TOKEN, ""),
                preferences.getString(Constants.USER_ID, ""),
                this
        ).execute();
        new GetRoutesTask(preferences.getString(Constants.TOKEN, ""),
                preferences.getString(Constants.USER_ID, ""),
                this
        ).execute();
    }


    @Override
    public void onClick(Event event) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.getLatLng(), 15));
        markerMap.get(event).showInfoWindow();
    }

    @Override
    public void connectionFailed() {
        preferences.edit()
                .remove(Constants.TOKEN)
                .remove(Constants.USER_ID).apply();
        if (!paused) {
            checkLogin();
        }
    }

    @Override
    public void progress(Event... events) {
        for (Event event : events) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(event.getLatLng()).title(event.getDescription()));
            markerMap.put(event, marker);
        }
        listFragment.addEvents(events);
    }

    @Override
    public void progress(Route... routes) {
        for (Route route : routes) {
            if (route.isActive()) {
                mMap.addPolyline(new PolylineOptions().addAll(route.getWaypoints()).color(Color.rgb(96, 96, 251)));
            }
        }
    }

    @Override
    public void progress(PointOfInterest... pointsOfInterest) {
        for (PointOfInterest pointOfInterest : pointsOfInterest) {
            if (pointOfInterest.isActive()) {
                mMap.addCircle(new CircleOptions().center(pointOfInterest.getLocation()).radius(pointOfInterest.getRadius()).fillColor(Color.argb(128, 0, 102, 255)).strokeColor(Color.TRANSPARENT));
            }
        }
    }
}
