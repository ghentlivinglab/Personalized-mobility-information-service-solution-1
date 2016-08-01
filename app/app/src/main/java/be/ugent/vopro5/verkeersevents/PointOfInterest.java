package be.ugent.vopro5.verkeersevents;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by evert on 5/1/16.
 */
public class PointOfInterest {
    private LatLng location;
    private int radius;
    private boolean active;

    public PointOfInterest(LatLng location, int radius, boolean active) {
        this.location = location;
        this.radius = radius;
        this.active = active;
    }

    public static PointOfInterest fromJSONObject(JSONObject jsonObject) throws JSONException {
        return new PointOfInterest(
                new LatLng(jsonObject.getJSONObject("address").getJSONObject("coordinates").getDouble("lat"),
                        jsonObject.getJSONObject("address").getJSONObject("coordinates").getDouble("lon")),
                jsonObject.getInt("radius"),
                jsonObject.getBoolean("active")
        );
    }

    public LatLng getLocation() {
        return location;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isActive() {
        return active;
    }
}
