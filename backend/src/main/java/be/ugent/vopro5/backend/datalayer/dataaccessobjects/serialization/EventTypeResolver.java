package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.GenericEvent;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WazeEvent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class EventTypeResolver extends TypeIdResolverBase {
    private static final String GENERIC_EVENT = "GENERIC_EVENT";
    private static final String WAZE_EVENT = "WAZE_EVENT";
    private static final String UNHANDLED_CASE = "Unhandled case";

    @Override
    public String idFromValue(Object value) {
        if (GenericEvent.class.equals(value.getClass())) {
            return GENERIC_EVENT;
        } else if (WazeEvent.class.equals(value.getClass())) {
            return WAZE_EVENT;
        } else {
            throw new UnsupportedOperationException(UNHANDLED_CASE);
        }
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        if (GenericEvent.class.equals(suggestedType)) {
            return GENERIC_EVENT;
        } else if (WazeEvent.class.equals(suggestedType)) {
            return WAZE_EVENT;
        } else {
            throw new UnsupportedOperationException(UNHANDLED_CASE);
        }
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        if (GENERIC_EVENT.equals(id)) {
            return TypeFactory.defaultInstance().constructSimpleType(GenericEvent.class, null);
        } else if (WAZE_EVENT.equals(id)) {
            return TypeFactory.defaultInstance().constructSimpleType(WazeEvent.class, null);
        } else {
            throw new UnsupportedOperationException(UNHANDLED_CASE);
        }
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
