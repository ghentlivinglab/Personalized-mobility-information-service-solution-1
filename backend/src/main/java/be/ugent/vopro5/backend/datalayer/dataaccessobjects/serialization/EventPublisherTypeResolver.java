package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventPublisher;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WazeEventPublisher;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Class for resolving the types of EventPublishers
 */
public class EventPublisherTypeResolver extends TypeIdResolverBase {
    private static final String WAZE_EVENT_PUBLISHER = "WAZE_EVENT_PUBLISHER";
    private static final String EXTERNAL_EVENT_PUBLISHER = "EXTERNAL_EVENT_PUBLISHER";
    private static final String UNHANDLED_CASE = "Unhandled case: ";

    /**
     * Find out what type of eventpublisher the value is
     * @param value
     * @return
     */
    @Override
    public String idFromValue(Object value) {
        if (WazeEventPublisher.class.equals(value.getClass())) {
            return WAZE_EVENT_PUBLISHER;
        } else if (EventPublisher.ExternalEventPublisher.class.equals(value.getClass())) {
            return EXTERNAL_EVENT_PUBLISHER;
        } else {
            throw new UnsupportedOperationException(UNHANDLED_CASE + value.getClass().toString());
        }
    }

    /**
     * Find out what type the suggested type is
     * @param value
     * @param suggestedType
     * @return
     */
    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        if (WazeEventPublisher.class.equals(suggestedType)) {
            return WAZE_EVENT_PUBLISHER;
        } else if (EventPublisher.ExternalEventPublisher.class.equals(suggestedType)) {
            return EXTERNAL_EVENT_PUBLISHER;
        } else {
            throw new UnsupportedOperationException(UNHANDLED_CASE + suggestedType.toString());
        }
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        if (WAZE_EVENT_PUBLISHER.equals(id)) {
            return TypeFactory.defaultInstance().constructSimpleType(WazeEventPublisher.class, null);
        } else if (EXTERNAL_EVENT_PUBLISHER.equals(id)) {
            return TypeFactory.defaultInstance().constructSimpleType(EventPublisher.ExternalEventPublisher.class, null);
        } else {
            throw new UnsupportedOperationException(UNHANDLED_CASE + id);
        }
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
