package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DAOMixinModule extends SimpleModule {
    public DAOMixinModule() {
        setMixInAnnotation(Address.class, AddressMixin.class);
        setMixInAnnotation(City.class, CityMixin.class);
        setMixInAnnotation(GenericEvent.class, GenericEventMixin.class);
        setMixInAnnotation(Event.class, EventMixin.class);
        setMixInAnnotation(EventPublisher.class, EventPublisherMixin.class);
        setMixInAnnotation(Jam.class, JamMixin.class);
        setMixInAnnotation(LatLon.class, LatLonMixin.class);
        setMixInAnnotation(NotificationMedium.class, NotificationMediumMixin.class);
        setMixInAnnotation(PointOfInterest.class, PointOfInterestMixin.class);
        setMixInAnnotation(Route.class, RouteMixin.class);
        setMixInAnnotation(TimeConstraint.class, TimeConstraintMixin.class);
        setMixInAnnotation(TimeIntervalConstraint.class, TimeIntervalConstraintMixin.class);
        setMixInAnnotation(Travel.class, TravelMixin.class);
        setMixInAnnotation(User.class, UserMixin.class);
        setMixInAnnotation(WazeEvent.class, WazeEventMixin.class);
        setMixInAnnotation(WeekDayConstraint.class, WeekDayConstraintMixin.class);
        setMixInAnnotation(EventPublisher.ExternalEventPublisher.class, ExternalEventPublisherMixin.class);
        setMixInAnnotation(Operator.class, OperatorMixin.class);
    }
}
