package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.LatLon;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public abstract class JamMixin {

    JamMixin(@JsonProperty("points") List<LatLon> points,
             @JsonProperty("speed") float speed,
             @JsonProperty("delay") int delay
    ) {
    }

    @JsonProperty("points")
    public abstract List<LatLon> getPoints();

    @JsonProperty("speed")
    public abstract float getSpeed();

    @JsonProperty("delay")
    public abstract int getDelay();
}
