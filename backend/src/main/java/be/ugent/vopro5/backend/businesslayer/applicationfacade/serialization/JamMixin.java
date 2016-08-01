package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.LatLon;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by thibault on 3/21/16.
 */
public abstract class JamMixin {

    @JsonProperty("points")
    private List<LatLon> points;
    @JsonProperty("speed")
    private float speed;
    @JsonProperty("delay")
    private int delay;

    public JamMixin(
            @JsonProperty("points") List<LatLon> points,
            @JsonProperty("speed") float speed,
            @JsonProperty("delay") int delay
    ) {}
}
