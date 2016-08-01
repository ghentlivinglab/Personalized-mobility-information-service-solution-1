package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.TimeIntervalConstraint;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WeekDayConstraint;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * Created by evert on 21/03/16.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "class")
@JsonSubTypes({@JsonSubTypes.Type(TimeIntervalConstraint.class), @JsonSubTypes.Type(WeekDayConstraint.class)})
@JsonTypeIdResolver(TimeConstraintTypeResolver.class)
public abstract class TimeConstraintMixin {
}
