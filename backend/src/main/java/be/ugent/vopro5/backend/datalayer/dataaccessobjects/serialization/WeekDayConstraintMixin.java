package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class WeekDayConstraintMixin {
    WeekDayConstraintMixin(@JsonProperty("recurring") boolean[] recurring) {
    }

    @JsonProperty("recurring")
    public abstract boolean[] getRecurring();
}
