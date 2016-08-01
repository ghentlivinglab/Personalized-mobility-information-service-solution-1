package be.ugent.vopro5.verkeersevents;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by evert on 4/29/16.
 */
public class Event implements Serializable {

    private String description;
    private String source;
    private String sourceImage;
    private LatLng latLng;

    public Event(String description, String source, String sourceImage, LatLng latLng) {
        this.description = description;
        this.source = source;
        this.sourceImage = sourceImage;
        this.latLng = latLng;
    }

    public static Event fromJSONObject(JSONObject jsonObject, Context context) throws JSONException {
        String description = jsonObject.getString("description");
        if(description.isEmpty()) {
            String eventType = jsonObject.getJSONObject("type").getString("type");
            description = context.getString(EventTypeTranslations.valueOf(eventType).getStringResource());
        }

        return new Event(
                description,
                jsonObject.getJSONObject("source").getString("name"),
                jsonObject.getJSONObject("source").getString("icon_url"),
                new LatLng(jsonObject.getJSONObject("coordinates").getDouble("lat"),
                        jsonObject.getJSONObject("coordinates").getDouble("lon"))
        );
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public String getSourceImage() {
        return sourceImage;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
