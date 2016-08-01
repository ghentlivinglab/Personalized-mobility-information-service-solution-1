package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thibault on 3/21/16.
 */
public abstract class LatLonMixin {
    @JsonProperty(value = "lat")
    private double lat;
    @JsonProperty(value = "lon")
    private double lon;

    @JsonCreator
    public LatLonMixin(
            @JsonProperty("lat") double lat,
            @JsonProperty("lon") double lon
    ) {}
}
