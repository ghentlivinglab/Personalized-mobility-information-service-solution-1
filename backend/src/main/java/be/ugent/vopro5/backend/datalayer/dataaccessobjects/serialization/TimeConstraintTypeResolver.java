package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.TimeIntervalConstraint;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WeekDayConstraint;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Created by evert on 23/03/16.
 */
public class TimeConstraintTypeResolver extends TypeIdResolverBase {
    private static final String TIME_INTERVAL_CONSTRAINT = "TIME_INTERVAL_CONSTRAINT";
    private static final String WEEK_DAY_CONSTRAINT = "WEEK_DAY_CONSTRAINT";
    private static final String UNHANDLED_CASE = "Unhandled case";

    @Override
    public String idFromValue(Object value) {
        if (value.getClass().equals(TimeIntervalConstraint.class)) {
            return TIME_INTERVAL_CONSTRAINT;
        } else if (value.getClass().equals(WeekDayConstraint.class)) {
            return WEEK_DAY_CONSTRAINT;
        } else {
            throw new UnsupportedOperationException(UNHANDLED_CASE);
        }
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        if (suggestedType.equals(TimeIntervalConstraint.class)) {
            return TIME_INTERVAL_CONSTRAINT;
        } else if (suggestedType.equals(WeekDayConstraint.class)) {
            return WEEK_DAY_CONSTRAINT;
        } else {
            throw new UnsupportedOperationException(UNHANDLED_CASE);
        }
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        if (TIME_INTERVAL_CONSTRAINT.equals(id)) {
            return TypeFactory.defaultInstance().constructSimpleType(TimeIntervalConstraint.class, null);
        } else if (WEEK_DAY_CONSTRAINT.equals(id)) {
            return TypeFactory.defaultInstance().constructSimpleType(WeekDayConstraint.class, null);
        } else {
            throw new UnsupportedOperationException(UNHANDLED_CASE);
        }
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
