package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class LatLonMixin {
    LatLonMixin(@JsonProperty("lat") double lat, @JsonProperty("lon") double lon) {
    }

    @JsonProperty("lat")
    public abstract double getLat();

    @JsonProperty("lon")
    public abstract double getLon();
}
