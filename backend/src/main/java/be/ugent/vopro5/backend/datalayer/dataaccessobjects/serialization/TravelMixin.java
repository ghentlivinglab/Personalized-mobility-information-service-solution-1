package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Address;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.TimeConstraint;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.UUID;

/**
 * Created by evert on 21/03/16.
 */
public abstract class TravelMixin {
    TravelMixin(
            @JsonProperty("_id") UUID identifier,
            @JsonProperty("name") String name,
            @JsonProperty("is_arrival_time") boolean isArrivalTime,
            @JsonProperty("start_point") Address startPoint,
            @JsonProperty("end_point") Address endPoint,
            @JsonProperty("routes") Set<Route> routes,
            @JsonProperty("time_constraints") Set<TimeConstraint> timeConstraints
    ) {
    }

    @JsonProperty("_id")
    public abstract UUID getIdentifier();

    @JsonProperty("name")
    public abstract String getName();

    @JsonProperty("is_arrival_time")
    public abstract boolean isArrivalTime();

    @JsonProperty("start_point")
    public abstract Address getStartPoint();

    @JsonProperty("end_point")
    public abstract Address getEndPoint();

    @JsonProperty("routes")
    public abstract Set<Route> getRoutes();

    @JsonProperty("time_constraints")
    public abstract Set<TimeConstraint> getTimeConstraints();
}
