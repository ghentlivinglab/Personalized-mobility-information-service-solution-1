package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Created by evert on 21/03/16.
 */
public abstract class TimeIntervalConstraintMixin {
    TimeIntervalConstraintMixin(@JsonProperty("start_time") LocalTime starttime,
                                @JsonProperty("duration") Duration duration
    ) {
    }

    @JsonProperty("start_time")
    public abstract LocalTime getStarttime();

    @JsonProperty("duration")
    public abstract Duration getDuration();
}
