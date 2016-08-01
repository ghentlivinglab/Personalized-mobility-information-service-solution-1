package be.ugent.vopro5.verkeersevents;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evert on 5/1/16.
 */
public class Route {
    private List<LatLng> waypoints;
    private boolean active;

    public Route(List<LatLng> waypoints, boolean active) {
        this.waypoints = waypoints;
        this.active = active;
    }

    public static Route fromJSONObject(LatLng startpoint, LatLng endpoint, JSONObject jsonObject) throws JSONException {
        List<LatLng> waypoints = new ArrayList<>();
        waypoints.add(startpoint);
        JSONArray waypointsArray = jsonObject.getJSONArray("waypoints");
        for (int i = 0; i < waypointsArray.length(); i++) {
            waypoints.add(new LatLng(
                    waypointsArray.getJSONObject(i).getDouble("lat"),
                    waypointsArray.getJSONObject(i).getDouble("lon")));
        }
        waypoints.add(endpoint);
        return new Route(waypoints, jsonObject.getBoolean("active"));
    }

    public List<LatLng> getWaypoints() {
        return waypoints;
    }

    public boolean isActive() {
        return active;
    }
}
