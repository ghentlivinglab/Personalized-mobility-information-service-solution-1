package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Created by maarten on 18.03.16.
 */
public class RESTModule extends SimpleModule {

    /**
     * This module can be registered to an ObjectMapper. The ObjectMapper object uses the module to serialize
     * the models to their corresponding API JSON representations and to deserialize the JSON objects to the correct
     * java models.
     */
    public RESTModule() {
        super("RESTModule");
        //ADD SERIALIZERS HERE
        addSerializer(Travel.class, new TravelMixin.TravelSerializer());
        addSerializer(EventType.class, new EventMixin.EventTypeSerializer());
        addDeserializer(EventType.class, new EventMixin.EventTypeDeserializer());
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        //ADD MIXINS HERE
        setMixInAnnotation(PointOfInterest.class, PointOfInterestMixIn.class);
        setMixInAnnotation(Address.class, AddressMixin.class);
        setMixInAnnotation(City.class, CityMixin.class);
        setMixInAnnotation(Travel.class, TravelMixin.class);
        setMixInAnnotation(User.class, UserMixin.class);
        setMixInAnnotation(LatLon.class, LatLonMixin.class);
        setMixInAnnotation(Route.class, RouteMixin.class);
        setMixInAnnotation(EventPublisher.class, EventPublisherMixin.class);
        setMixInAnnotation(GenericEvent.class, EventMixin.class);
        setMixInAnnotation(Event.class, EventMixin.class);
        setMixInAnnotation(Jam.class, JamMixin.class);
        setMixInAnnotation(User.class, UserMixin.class);
        setMixInAnnotation(Identifiable.class, IdentifiableMixin.class);
        setMixInAnnotation(Operator.class, OperatorMixin.class);
    }
}
