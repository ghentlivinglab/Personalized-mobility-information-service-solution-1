package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by anton on 11.04.16.
 */
public abstract class Message {

    public static class EventList extends ArrayList<Event> {
        public EventList(Collection<Event> events) {
            super(events);
        }
    }

    public static class NotificationList extends ArrayList<Notification> {
        public NotificationList() {
        }
    }

    /**
     * Class representing a Notification
     */
    public static class Notification {
        private Event event;
        private User user;
        private List<PointOfInterest> pointsOfInterest;
        private List<Route> routes;

        /**
         * Create a new notification for an event destined for a user
         *
         * @param event The event relevant to the user.
         * @param user The user to whom the notification is sent.
         * @param pointsOfInterest The points of interest from the user that are relevant to the event.
         * @param routes The routes from the user that are relevant to the event.
         */
        public Notification(Event event, User user, List<PointOfInterest> pointsOfInterest, List<Route> routes) {
            this.event = event;
            this.user = user;
            this.pointsOfInterest = pointsOfInterest;
            this.routes = routes;
        }

        /**
         * @return
         */
        public Event getEvent() {
            return event;
        }

        /**
         * @return
         */
        public User getUser() {
            return user;
        }

        /**
         * @return
         */
        public List<PointOfInterest> getPointsOfInterest() {
            return pointsOfInterest;
        }

        /**
         * @return
         */
        public List<Route> getRoutes() {
            return routes;
        }
    }
}
