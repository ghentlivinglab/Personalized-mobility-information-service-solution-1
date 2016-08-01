package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.algorithms;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Michael on 4/12/16.
 */
public class EventMatchingAlgorithm {

    private static final int BUFFER_IN_METERS = 10;
    private static final double ORIGIN_LAT = 51.054511;
    private static final double ORIGIN_LON = 3.721985;
    private static final double M_LAT = 111132.92 - 559.82 * Math.cos(2 * ORIGIN_LAT) + 1.175 * Math.cos(4 * ORIGIN_LAT) - 0.0023 * Math.cos(6 * ORIGIN_LAT);
    private static final double M_LON = 111412.84 * Math.cos(ORIGIN_LAT) - 93.5 * Math.cos(3 * ORIGIN_LAT) - 0.118 * Math.cos(5 * ORIGIN_LAT);

    private static final double MINIMAL_LATITUDE = -90;
    private static final double MAXIMAL_LATITUDE = 90;
    private static final double MINIMAL_LONGITUDE = -180;
    private static final double MAXIMAL_LONGITUDE = 180;
    private static final int SQUARE = 2;

    private final DataAccessProvider dap;

    /**
     * Create a new EventMatching Algorithm
     *
     * @param dap: the DataAccessProvider this class should use
     */
    public EventMatchingAlgorithm(DataAccessProvider dap) {
        this.dap = dap;
    }

    private boolean testTimeConstraint(Travel travel) {
        return !travel.getTimeConstraints().stream()
                .filter(timeConstraint -> !timeConstraint.valid(LocalDateTime.now()))
                .findAny().isPresent();
    }

    private boolean testRouteConstraints(Route route, Event event) {
        if (route.isActive()) {
            if (event.getRelevantForTransportationTypes().stream()
                    .filter(transportationType -> transportationType.equals(route.getTransportationType()))
                    .findAny().isPresent() && route.getNotifyForEventTypes().stream()
                    .filter(eventType -> eventType.equals(event.getEventType()))
                    .findAny().isPresent()) {
                return true;
            }
        }
        return false;
    }

    private boolean testEventInWayPoints(Travel travel, Route route, Event event) {
        return eventInSquare(travel.getStartPoint().getCoordinates(), route, travel.getEndPoint().getCoordinates(), event) &&
                eventOnRoute(travel.getStartPoint().getCoordinates(), route, travel.getEndPoint().getCoordinates(), event);
    }

    /**
     * Checks whether an event lies inside the circumscribing square around the route
     *
     * @param start: the coordinates of the start point of the route
     * @param route: the route itself
     * @param end:   the coordinates of the endpoint of the route
     * @param event: the event
     * @return
     */
    public boolean eventInSquare(LatLon start, Route route, LatLon end, Event event) {
        double minLat = MAXIMAL_LATITUDE;
        double minLon = MAXIMAL_LONGITUDE;
        double maxLat = MINIMAL_LATITUDE;
        double maxLon = MINIMAL_LONGITUDE;

        List<LatLon> waypoints = new ArrayList<>();
        waypoints.add(start);
        waypoints.addAll(route.getWaypoints());
        waypoints.add(end);

        AxisPoint eventLocation = convertLatLon(event.getLocation());
        for (LatLon latlon : waypoints) {
            if (minLat > latlon.getLat()) {
                minLat = latlon.getLat();
            }
            if (minLon > latlon.getLon()) {
                minLon = latlon.getLon();
            }
            if (maxLat < latlon.getLat()) {
                maxLat = latlon.getLat();
            }
            if (maxLon < latlon.getLon()) {
                maxLon = latlon.getLon();
            }
        }

        AxisPoint maxConvertedLatLon = convertLatLon(new LatLon(minLat, minLon));
        AxisPoint minConvertedLatLon = convertLatLon(new LatLon(maxLat, maxLon));
        minConvertedLatLon = addToConvertedLatLon(minConvertedLatLon, -BUFFER_IN_METERS, -BUFFER_IN_METERS);
        maxConvertedLatLon = addToConvertedLatLon(maxConvertedLatLon, BUFFER_IN_METERS, BUFFER_IN_METERS);


        return eventLocation.getX() > minConvertedLatLon.getX() && (eventLocation.getX() < maxConvertedLatLon.getX())
                && (eventLocation.getY() > minConvertedLatLon.getY()) && (eventLocation.getY() < maxConvertedLatLon.getY());
    }

    private boolean eventOnRoute(LatLon start, Route route, LatLon end, Event event) {
        LatLon loc = event.getLocation();
        List<LatLon> waypoints = new ArrayList<>();

        waypoints.add(start);
        waypoints.addAll(route.getWaypoints());
        waypoints.add(end);

        for (int i = 0; i + 1 < waypoints.size(); i++) {
            if (checkPointCloseToLine(waypoints.get(i), waypoints.get(i + 1), loc)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPointCloseToLine(LatLon p1, LatLon p2, LatLon p) {
        AxisPoint convertedP1 = convertLatLon(p1);
        AxisPoint convertedP2 = convertLatLon(p2);
        AxisPoint convertedP = convertLatLon(p);
        double dx = convertedP2.getX() - convertedP1.getX();
        double dy = convertedP2.getY() - convertedP1.getY();
        double distance = Math.sqrt(Math.pow(dy, SQUARE) + Math.pow(dx, SQUARE));

        return getDistanceToMiddle(convertedP1, convertedP2, convertedP) <= (distance / (double) SQUARE) + BUFFER_IN_METERS &&
                (getDistanceToLine(convertedP1, convertedP2, convertedP, dx, dy, distance) <= BUFFER_IN_METERS);
    }

    private double getDistanceToMiddle(AxisPoint cp1, AxisPoint cp2, AxisPoint cp) {
        double x = (cp1.getX() + cp2.getX()) / (double) SQUARE;
        double y = (cp1.getY() + cp2.getY()) / (double) SQUARE;
        AxisPoint middle = new AxisPoint(x, y);

        return Math.sqrt(Math.pow(cp.getX() - middle.getX(), SQUARE) + Math.pow(cp.getY() - middle.getY(), SQUARE));
    }

    private double getDistanceToLine(AxisPoint cp1, AxisPoint cp2, AxisPoint cp, double dx, double dy, double distance) {
        double numerator = Math.abs(dy * cp.getX() - dx * cp.getY() + cp2.getX() * cp1.getY() - cp2.getY() * cp1.getX());
        return numerator / distance;
    }

    private AxisPoint addToConvertedLatLon(AxisPoint or, double dx, double dy) {
        return new AxisPoint(or.getX() + dx, or.getY() + dy);
    }

    private boolean POIContainsEventType(PointOfInterest poi, EventType eventType) {
        return poi.getNotifyForEventTypes().contains(eventType);
    }

    private boolean testEventRelevantForPOI(PointOfInterest poi, Event event) {
        AxisPoint convertedPoi = convertLatLon(poi.getAddress().getCoordinates());
        AxisPoint convertedEvent = convertLatLon(event.getLocation());
        double radius = Math.sqrt(Math.pow(convertedEvent.getX() - convertedPoi.getX(), SQUARE) + Math.pow(convertedEvent.getY() - convertedPoi.getY(), SQUARE));
        return radius < poi.getRadius();
    }


    private AxisPoint convertLatLon(LatLon ll) {
        double x = (ORIGIN_LON - ll.getLon()) * M_LON;
        double y = (ORIGIN_LAT - ll.getLat()) * M_LAT;
        return new AxisPoint(x, y);
    }

    /**
     * @param events
     * @return
     */
    public Message.NotificationList matchEvents(Message.EventList events) {
        return matchEvents(events, dap.getDataAccessContext().getUserDAO().listAll());
    }

    /**
     * @param events The events to be matched.
     * @param users The users to match the events to
     * @return
     */
    public Message.NotificationList matchEvents(Message.EventList events, List<User> users) {
        Message.NotificationList notifications = new Message.NotificationList();
        events.stream().forEach(event -> users.stream().forEach(user -> {
            List<PointOfInterest> pois = user.getPointsOfInterest().stream().filter(PointOfInterest::isActive)
                    .filter(poi -> POIContainsEventType(poi, event.getEventType()))
                    .filter(poi -> testEventRelevantForPOI(poi, event)).collect(Collectors.toList());
            List<Route> routes = new ArrayList<>();
            user.getTravels().stream().filter(this::testTimeConstraint)
                    .forEach(travel -> travel.getRoutes().stream()
                            .filter(route -> testRouteConstraints(route, event))
                            .filter(route -> testEventInWayPoints(travel, route, event)).forEach(routes::add)
                    );
            if (!pois.isEmpty() || !routes.isEmpty()) {
                notifications.add(new Message.Notification(event, user, pois, routes));
            }
        }));
        return notifications;
    }
}
