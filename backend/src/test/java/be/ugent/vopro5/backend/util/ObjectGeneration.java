package be.ugent.vopro5.backend.util;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 27/02/16.
 */
public class ObjectGeneration {
    private static final int SECONDS_IN_DAY = 60 * 60 * 24;
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private static Random random = new Random();

    /**
     * Generate a random Address.
     *
     * @return The random Address
     */
    public static Address generateAddress() throws ValidationException {
        return new Address(
                random.nextLong() + "",
                random.nextInt() + "",
                generateCity(),
                (random.nextBoolean() + "").substring(0, 2),
                generateLatLon()
        );
    }

    /**
     * Generate a random City
     *
     * @return The random City
     */
    public static City generateCity() throws ValidationException {
        return new City(
                random.nextInt() + "",
                random.nextInt() + ""
        );
    }

    /**
     * Generate random LatLon.
     *
     * @return The random LatLon
     */
    public static LatLon generateLatLon() throws ValidationException {
        return new LatLon(
                ThreadLocalRandom.current().nextDouble(-90, 90),
                ThreadLocalRandom.current().nextDouble(-180, 180)
        );
    }

    /**
     * Generate a random Event.
     *
     * @return The random Event
     */

    public static GenericEvent generateGenericEvent() throws ValidationException {
        Set<Jam> jams = new HashSet<>();
        for (int i = 0; i < random.nextInt(3); i++) {
            jams.add(generateJam());
        }
        return new GenericEvent(
                UUID.randomUUID(),
                generateLatLon(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(10),
                random.nextLong() + "",
                jams,
                generateEventType(),
                generateEventPublisher(),
                new HashSet<>(Collections.singleton(generateTransportationType()))
        );
    }

    public static Event generateWazeEvent() throws ValidationException {
        Set<Jam> jams = new HashSet<>();
        for (int i = 0; i < random.nextInt(3); i++) {
            jams.add(generateJam());
        }
        return new WazeEvent(
                UUID.randomUUID(),
                generateLatLon(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),
                random.nextLong() + "",
                jams,
                generateEventType(),
                new HashSet<>(Collections.singleton(generateTransportationType())),
                UUID.randomUUID()
        );
    }

    public static TransportationType generateTransportationType() {
        return TransportationType.values()[random.nextInt(TransportationType.values().length)];
    }

    public static EventPublisher generateEventPublisher() {
        return new WazeEventPublisher();
    }

    /**
     * Generate a random EventType.
     *
     * @return The random EventType
     */
    public static EventType generateEventType() {
        return EventType.values()[random.nextInt(EventType.values().length)];
    }

    public static NotificationMedium generateCellNumberNotificationMedium() throws ValidationException {
        return new NotificationMedium(
                NotificationMedium.NotificationMediumType.CELL_NUMBER,
                random.nextInt() + "", random.nextBoolean()
        );
    }

    /**
     * Generate a random Jam.
     *
     * @return The random Jam
     */
    public static Jam generateJam() throws ValidationException {
        int numPoints = random.nextInt(12) + 3;
        List<LatLon> points = new ArrayList<>();
        for (int i = 0; i < numPoints; i++) {
            points.add(generateLatLon());
        }
        return new Jam(
                points,
                Math.abs(random.nextFloat()),
                Math.abs(random.nextInt())
        );
    }

    public static Set<PointOfInterest> generatePointOfInterestSet() {
        Set<PointOfInterest> pointOfInterestSet = new HashSet<>();
        for (int i = 0; i < random.nextInt(11) + 1; i++) {
            pointOfInterestSet.add(generatePointOfInterest());
        }
        return pointOfInterestSet;
    }

    /**
     * Generate a random PointOfInterest.
     *
     * @return The random PointOfInterest
     */

    public static Set<NotificationMedium.NotificationMediumType> generateNotificationMediaTypes() throws ValidationException {
        Set<NotificationMedium.NotificationMediumType> notificationMedia = new HashSet<>();
        notificationMedia.add(NotificationMedium.NotificationMediumType.CELL_NUMBER);
        notificationMedia.add(NotificationMedium.NotificationMediumType.EMAIL);
        return notificationMedia;
    }

    public static Set<TransportationType> generateTransportationTypes() throws ValidationException {
        Set<TransportationType> transportationTypes = new HashSet<>();
        for (int i = 0; i < random.nextInt(5) + 1; i++) {
            transportationTypes.add(generateTransportationType());
        }

        return transportationTypes;
    }

    public static Set<EventType> generateEventTypes() throws ValidationException {
        Set<EventType> eventTypes = new HashSet<>();
        for (int i = 0; i < random.nextInt(5) + 1; i++) {
            eventTypes.add(generateEventType());
        }

        return eventTypes;
    }

    public static PointOfInterest generatePointOfInterest() throws ValidationException {
        Set<EventType> eventTypes = new HashSet<>();
        EventType eventType = generateEventType();
        eventTypes.add(eventType);
        EventType secondEventType = generateEventType();
        while (secondEventType == eventType) {
            secondEventType = generateEventType();
        }
        eventTypes.add(secondEventType);
        return new PointOfInterest(
                UUID.randomUUID(),
                generateAddress(),
                random.nextInt() + "",
                Math.abs(random.nextInt()),
                eventTypes, //generateEventTypes()
                generateNotificationMediaTypes(),
                random.nextBoolean()
        );
    }

    /**
     * Generate a random Route.
     *
     * @return The random Route
     */

    public static Route generateRoute() throws ValidationException {
        List<LatLon> waypoints = new ArrayList<>();
        Set<EventType> notifyForEventTypes = new HashSet<>();
        for (int i = 0; i < random.nextInt(3) + 1; i++) {
            waypoints.add(generateLatLon());
            notifyForEventTypes.add(generateEventType());
        }

        return new Route(UUID.randomUUID(),
                waypoints,
                generateTransportationType(),
                notifyForEventTypes,
                generateNotificationMediaTypes(),
                random.nextBoolean());
    }

    /**
     * Generate a random TimeIntervalConstraint.
     *
     * @return The random TimeIntervalConstraint
     */
    public static TimeIntervalConstraint generateTimeIntervalConstraint() throws ValidationException {
        return new TimeIntervalConstraint(LocalTime.now(),
                Duration.ofSeconds(random.nextInt(SECONDS_IN_DAY)));
    }

    public static WeekDayConstraint generateWeekDayConstraint() throws ValidationException {
        return new WeekDayConstraint(new boolean[]{
                random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(),
                random.nextBoolean(), random.nextBoolean(), random.nextBoolean()
        });
    }

    /**
     * Generate a random Travel.
     *
     * @return The random Travel
     */
    public static Travel generateTravel() throws ValidationException {
        Set<TimeConstraint> timeConstraints = new HashSet<>();
        timeConstraints.add(generateTimeIntervalConstraint());
        timeConstraints.add(generateWeekDayConstraint());
        Set<Route> routes = new HashSet<>();
        int numRoutes = random.nextInt(15) + 1;
        for (int i = 0; i < numRoutes; i++) {
            routes.add(generateRoute());
        }
        return new Travel(
                UUID.randomUUID(),
                random.nextInt() + "",
                random.nextBoolean(),
                generateAddress(),
                generateAddress(),
                routes,
                timeConstraints
        );
    }

    /**
     * Generate a random Person
     *
     * @return The random Person
     * @throws ValidationException
     */
    public static Person generatePerson() throws ValidationException {
        return new Person(
                UUID.randomUUID(),
                new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL, random.nextInt() + "@ugent.be", random.nextBoolean()),
                SCryptUtil.scrypt(random.nextInt() + "", 16384, 8, 1));
    }

    /**
     * Generate a random User.
     *
     * @return The random User
     */
    public static User generateUser() throws ValidationException {
        Set<Travel> travels = new HashSet<>();
        Set<NotificationMedium> notificationMedia = new HashSet<>();
        notificationMedia.add(generateCellNumberNotificationMediumObject());

        int numTravels = random.nextInt(14) + 1;
        for (int i = 0; i < numTravels; i++) {
            travels.add(generateTravel());
        }

        Set<PointOfInterest> pointsOfInterest = new HashSet<>();
        int numPointsOfInterest = random.nextInt(14) + 1;
        for (int i = 0; i < numPointsOfInterest; i++) {
            pointsOfInterest.add(generatePointOfInterest());
        }

        return new User(
                UUID.randomUUID(),
                new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL, random.nextInt() + "@ugent.be", random.nextBoolean()),
                notificationMedia,
                travels,
                pointsOfInterest,
                random.nextBoolean(),
                SCryptUtil.scrypt(random.nextInt() + "", 16384, 8, 1));
    }

    /**
     * Generate a random User.
     *
     * @return The Random User
     * @throws ValidationException
     */
    public static Operator generateOperator() throws ValidationException {
        return new Operator(
                UUID.randomUUID(),
                new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL, random.nextInt() + "@ugent.be", random.nextBoolean()),
                SCryptUtil.scrypt(random.nextInt() + "", 16384, 8, 1));
    }

    /**
     *
     * Generate a random Message.Notification.
     *
     * @return The random Message.Notification
     */
    public static Message.Notification generateNotification() {
        return generateNotificationForUser(generateUser());
    }

    /**
     *
     * Generate a random Message.Notification for a specific User.
     *
     * @param user The User
     * @return The random Message.Notification.
     */
    public static Message.Notification generateNotificationForUser(User user) {
        if (random.nextBoolean()) {
            return new Message.Notification(generateGenericEvent(), user, Collections.singletonList(generatePointOfInterest()), new ArrayList<>());
        } else {
            return new Message.Notification(generateGenericEvent(), user, new ArrayList<>(), Collections.singletonList(generateRoute()));
        }
    }

    public static Set<NotificationMedium> generateNotificationMediaObjects() throws ValidationException {
        Set<NotificationMedium> notificationMedia = new HashSet<>();
        notificationMedia.add(generateCellNumberNotificationMediumObject());
        notificationMedia.add(generateEmailNotificationMediumObject());
        return notificationMedia;
    }

    public static NotificationMedium generateEmailNotificationMediumObject() throws ValidationException {
        return new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL,
                random.nextInt() + "@ugent.be", random.nextBoolean()
        );
    }

    public static NotificationMedium generateCellNumberNotificationMediumObject() throws ValidationException {
        return new NotificationMedium(NotificationMedium.NotificationMediumType.CELL_NUMBER, random.nextInt() + "", random.nextBoolean());
    }

    public static HashMap<String, String> generateAuthorizationHeader() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "test");

        return headerMap;
    }

    /**
     * Generate a random User.
     *
     * @return The User as a JsonNode object
     */
    public static ObjectNode generateAPIUserJsonNode(boolean cellNumberPresent) {
        ObjectNode user = factory.objectNode();
        user.put("email", "jan" + random.nextInt() + "@janssens.be");
        if (cellNumberPresent) {
            user.put("cell_number", "+32412345678");
        } else {
            user.put("cell_number", (byte[]) null);
        }
        user.put("password", "hunter2");
        user.put("mute_notifications", random.nextBoolean());
        ObjectNode validated = factory.objectNode();
        validated.put("cell_number", random.nextBoolean());
        validated.put("email", random.nextBoolean());
        user.set("validated", validated);
        return user;
    }

    /**
     * Generate a random Operator.
     *
     * @return The Operator as a JsonNode object
     */
    public static ObjectNode generateAPIOperatorJsonNode() {
        ObjectNode operator = factory.objectNode();
        operator.put("id", UUID.randomUUID().toString());
        operator.put("email", "jan" + random.nextInt() + "@jansssens.be");
        operator.put("password", "unBR3AKable");
        return operator;
    }

    public static ObjectNode generateAPICoordinateJsonNode() {
        ObjectNode coord = factory.objectNode();
        coord.put("lat", ThreadLocalRandom.current().nextDouble(-90, 90));
        coord.put("lon", ThreadLocalRandom.current().nextDouble(-180, 180));
        return coord;
    }

    public static ObjectNode generateAPIAddressJsonNode() {
        ObjectNode address = factory.objectNode();
        address.put("street", "Dorpstraat");
        address.put("housenumber", "" + random.nextInt(1000));
        address.put("city", "Gent");
        address.put("country", "be");
        address.put("postal_code", "9000");
        address.set("coordinates", generateAPICoordinateJsonNode());
        return address;
    }

    public static ObjectNode generateAddressJsonNode() {
        ObjectNode address = factory.objectNode();
        address.put("street", "" + random.nextLong());
        address.put("house_number", "" + random.nextLong());
        address.set("city", generateCityJsonNode());
        address.put("country", ("" + random.nextInt()).substring(0, 2));
        address.set("coordinates", generateLatLonJsonNode());
        return address;
    }

    private static ObjectNode generateLatLonJsonNode() {
        return generateAPICoordinateJsonNode();
    }

    public static ObjectNode generateCityJsonNode() {
        ObjectNode city = factory.objectNode();
        city.put("name", "" + random.nextLong());
        city.put("postal_code", "" + random.nextLong());
        return city;
    }

    public static ObjectNode generateAPIRouteJsonNode() {
        ObjectNode route = factory.objectNode();
        route.put("id", UUID.randomUUID().toString());
        ArrayNode waypoints = factory.arrayNode();
        for (int i = 0; i < random.nextInt(100) + 1; i++) {
            waypoints.add(generateAPICoordinateJsonNode());
        }
        route.set("waypoints", waypoints);
        route.put("transportation_type", generateTransportationType().toString());
        ArrayNode notifyForEventTypes = factory.arrayNode();
        Set<ObjectNode> notifyForEventTypesSet = new HashSet<>();
        for (int i = 0; i < random.nextInt(10) + 1; i++) {
            notifyForEventTypesSet.add(generateAPIEventTypeJSONNode());
        }
        notifyForEventTypesSet.forEach(notifyForEventTypes::add);
        route.set("notify_for_event_types", notifyForEventTypes);
        ObjectNode notify = factory.objectNode();
        notify.put("email", random.nextBoolean());
        notify.put("cell_number", random.nextBoolean());
        route.set("notify", notify);
        route.put("active", true);

        return route;
    }

    public static ObjectNode generateAPIEventTypeJSONNode() {
        ObjectNode node = factory.objectNode();
        node.put("type", generateEventType().toString());
        return node;
    }

    public static ObjectNode generateAPIPOIJsonNode() {
        ObjectNode poi = factory.objectNode();
        poi.put("id", UUID.randomUUID().toString());
        ArrayNode notifyForEventTypes = factory.arrayNode();
        Set<ObjectNode> notifyForEventTypesSet = new HashSet<>();
        for (int i = 0; i < random.nextInt(10) + 30; i++) {
            notifyForEventTypesSet.add(generateAPIEventTypeJSONNode());
        }
        notifyForEventTypesSet.forEach(notifyForEventTypes::add);

        poi.set("notify_for_event_types", notifyForEventTypes);
        ObjectNode notify = factory.objectNode();
        notify.put("email", random.nextBoolean());
        notify.put("cell_number", random.nextBoolean());
        poi.set("notify", notify);
        poi.put("active", true);
        poi.put("name", random.nextInt() + "");
        poi.put("radius", Math.abs(random.nextInt()) + "");
        poi.set("address", generateAPIAddressJsonNode());
        return poi;
    }


    public static ObjectNode generateAPITravelJsonNode() {
        ObjectNode travel = factory.objectNode();
        travel.put("id", UUID.randomUUID().toString());
        travel.set("route", generateAPIRouteJsonNode());
        travel.put("is_arrival_time", random.nextBoolean());
        travel.put("date", LocalDate.now().toString());
        ArrayNode timeInterval = factory.arrayNode();
        timeInterval.add("08:00");
        timeInterval.add("09:00");
        travel.set("time_interval", timeInterval);
        ArrayNode recurring = factory.arrayNode();
        for (int i = 0; i < 7; i++) {
            recurring.add(random.nextBoolean());
        }
        travel.set("recurring", recurring);

        travel.put("name", "Unittest Travel" + random.nextInt());

        travel.set("startpoint", generateAPIAddressJsonNode());
        travel.set("endpoint", generateAPIAddressJsonNode());
        return travel;
    }

    public static ObjectNode generateAPIJamJsonNode() {
        ObjectNode jam = factory.objectNode();
        ArrayNode points = factory.arrayNode();
        int numPoints = random.nextInt(12) + 3;
        for (int i = 0; i < numPoints; i++) {
            points.add(generateAPICoordinateJsonNode());
        }
        jam.set("points", points);
        jam.put("speed", random.nextFloat());
        jam.put("delay", Math.abs(random.nextInt(3600)));
        return jam;
    }

    public static ObjectNode generateAPIEventTypeJsonNode() {
        ObjectNode type = factory.objectNode();
        type.put("type", EventType.values()[random.nextInt(EventType.values().length)].toString());
        return type;
    }

    public static ObjectNode generateAPIEventSourceJsonNode() {
        ObjectNode source = factory.objectNode();
        source.put("name", "Unittest " + random.nextInt());
        source.put("type", "EXTERNAL");
        source.put("icon_url", "" + random.nextInt());
        return source;
    }

    public static ObjectNode generateAPIEventJsonNode() {
        ObjectNode event = factory.objectNode();
        event.put("id", UUID.randomUUID().toString());
        event.set("coordinates", generateAPICoordinateJsonNode());
        event.put("description", "Event generated by unittests." + random.nextInt());
        ArrayNode jams = factory.arrayNode();
        for (int i = 0; i < random.nextInt(10); i++) {
            jams.add(generateAPIJamJsonNode());
        }
        event.set("jams", jams);
        event.set("type", generateAPIEventTypeJsonNode());
        event.set("source", generateAPIEventSourceJsonNode());
        ArrayNode transportationTypes = factory.arrayNode();
        Set<String> transportationTypesSet = new HashSet<>();
        for (int i = 0; i < 1 + random.nextInt(10); i++) {
            transportationTypesSet.add(generateTransportationType().toString());
        }
        transportationTypesSet.forEach(transportationTypes::add);
        event.set("relevant_for_transportation_types", transportationTypes);
        return event;
    }

    public static ObjectNode generateGenericEventJsonNode() {
        ObjectNode event = factory.objectNode();
        event.put("class", "GENERIC_EVENT");
        event.put("_id", UUID.randomUUID().toString());
        event.set("location", generateLatLonJsonNode());
        event.put("publication_time", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        event.put("last_edit_time", LocalDateTime.now().plusHours(2).format(DateTimeFormatter.ISO_DATE_TIME));
        event.put("description", "" + random.nextLong());
        ArrayNode jams = factory.arrayNode();
        int numJams = random.nextInt(15) + 1;
        for (int i = 0; i < numJams; i++) {
            jams.add(generateJamJsonNode());
        }
        event.set("jams", jams);
        event.put("event_type", generateEventType().toString());
        event.set("source", generateEventPublisherJsonNode());
        ArrayNode transportationTypes = factory.arrayNode();
        Set<String> transportationTypesSet = new HashSet<>();
        int numTransportationTypes = random.nextInt(15) + 1;
        for (int i = 0; i < numTransportationTypes; i++) {
            transportationTypesSet.add(generateTransportationType().toString());
        }
        transportationTypesSet.forEach(transportationTypes::add);
        event.set("relevant_for_transportation_types", transportationTypes);
        return event;
    }

    public static ObjectNode generateEventPublisherJsonNode() {
        ObjectNode eventPublisher = factory.objectNode();
        eventPublisher.put("class", "WAZE_EVENT_PUBLISHER");
        return eventPublisher;
    }

    public static ObjectNode generateJamJsonNode() {
        ObjectNode jam = factory.objectNode();
        jam.set("start_node", generateLatLonJsonNode());
        jam.set("end_node", generateLatLonJsonNode());
        jam.put("speed", Math.abs(random.nextFloat()));
        jam.put("delay", Math.abs(random.nextInt()));
        return jam;
    }

    public static List<EventType> generateAllEventTypes() {
        return new ArrayList<>(Arrays.asList(EventType.values()));
    }

    public static Map<String,Event> generateEventsForPOIMatching() {
        Map<String, Event> eventMap = new HashMap<>();

        LatLon pointInPOI = new LatLon(51.024411, 3.710930);
        LatLon pointOutOfPOI = new LatLon(51.024267, 3.712553);

        Event eventInPOI = new GenericEvent(
                pointInPOI,
                random.nextInt() + "",
                Collections.singleton(generateJam()),
                generateEventType(),
                new WazeEventPublisher(),
                Collections.singleton(generateTransportationType())
        );


        Event eventOutOfPOI = new GenericEvent(
                pointOutOfPOI,
                random.nextInt() + "",
                Collections.singleton(generateJam()),
                generateEventType(),
                new WazeEventPublisher(),
                Collections.singleton(generateTransportationType())
        );

        eventMap.put("inPOI",eventInPOI);
        eventMap.put("outOfPOI",eventOutOfPOI);

        return eventMap;
    }

    public static Set<User> generateUsersForMatching(TransportationType transportationType) {
        Set<User> users = new HashSet<>();

        User user = new User(
                UUID.randomUUID(),
                new NotificationMedium(NotificationMedium.NotificationMediumType.EMAIL, random.nextInt() + "@ugent.be", random.nextBoolean()),
                generateNotificationMediaObjects(),
                generateTravelsForEventMatching(transportationType),
                generatePointsOfInterestForPOIMatching(),
                random.nextBoolean(),
                SCryptUtil.scrypt(random.nextInt() + "", 16384, 8, 1));

        users.add(user);
        return users;
    }

    public static Set<Travel> generateTravelsForEventMatching(TransportationType transportationType) {
        Set<Travel> travels = new HashSet<>();

        LatLon startpoint = new LatLon(51.064105, 3.709544);
        LatLon endpoint = new LatLon(51.064517, 3.712667);

        Address startAddress = new Address(
                "Wondelgemstraat",
                "1",
                new City("Gent", "9000"),
                "BE",
                startpoint);

        Address endAddress = new Address("Elsstraat",
                "1",
                new City("Gent", "9000"),
                "BE",
                endpoint);

        Travel travel = new Travel(
                UUID.randomUUID(),
                random.nextInt() + "",
                random.nextBoolean(),
                startAddress,
                endAddress,
                generateRoutesForEventMatching(transportationType),
                generateTimeConstraintsForEventMatching()
        );
        travels.add(travel);
        return travels;
    }

    public static Set<Route> generateRoutesForEventMatching(TransportationType t) {
        Set<Route> routes = new HashSet<>();

        List<LatLon> waypoints = new ArrayList<>();
        waypoints.add(new LatLon(51.064648, 3.709348));
        waypoints.add(new LatLon(51.065154, 3.712396));

        Set<EventType> notifyForEventTypes = new HashSet<>(generateAllEventTypes());

        Route route = new Route(
                waypoints,
                t,
                notifyForEventTypes,
                generateNotificationMediaTypes(),
                true);

        routes.add(route);

        return routes;
    }

    public static Set<PointOfInterest> generatePointsOfInterestForPOIMatching() {
        Set<PointOfInterest> pointsOfInterest = new HashSet<>();
        LatLon center = new LatLon(51.024211, 3.710645);

        Set<EventType> notifyForEventTypes = new HashSet<>(generateAllEventTypes());

        Address address = new Address("Sterre", "1", new City("Gent", "9000"), "BE", center);
        PointOfInterest pointOfInterest = new PointOfInterest (
                UUID.randomUUID(),
                address,
                random.nextInt() + "",
                60,
                notifyForEventTypes,
                generateNotificationMediaTypes(),
                true
        );

        pointsOfInterest.add(pointOfInterest);

        return pointsOfInterest;
    }

    public static Set<TimeConstraint> generateTimeConstraintsForEventMatching() {
        Set<TimeConstraint> timeConstraints = new HashSet<>();
        TimeIntervalConstraint timeIntervalConstraint = new TimeIntervalConstraint(LocalTime.now(), Duration.ofSeconds(3600 * 24));
        WeekDayConstraint weekDayConstraint = new WeekDayConstraint(new boolean[] {true, true, true, true, true, true, true});
        timeConstraints.add(timeIntervalConstraint);
        timeConstraints.add(weekDayConstraint);
        return timeConstraints;
    }

    public static List<Event> generateEventsForEventMatching(TransportationType transportationType) {
        LatLon pointOnRoute1 = new LatLon(51.064346, 3.709457);
        LatLon pointOnRoute2 = new LatLon(51.064902, 3.710883);
        LatLon pointOnRoute3 = new LatLon(51.064802, 3.712567);

        LatLon pointOnCorner = new LatLon(51.064625, 3.709433);

        LatLon pointOffRouteInSquare = new LatLon(51.064518, 3.710660);
        LatLon pointOnLineNotInCircle = new LatLon(51.064362, 3.712747);
        LatLon pointOffRouteNotInSquare = new LatLon(51.062766, 3.708945);

        Event eventOnRoute1 = generateEvent(pointOnRoute1,transportationType);
        Event eventOnRoute2 = generateEvent(pointOnRoute2,transportationType);
        Event eventOnRoute3 = generateEvent(pointOnRoute3,transportationType);
        Event eventOffRouteInSquare = generateEvent(pointOffRouteInSquare,transportationType);
        Event eventOnLineNotInCircle = generateEvent(pointOnLineNotInCircle,transportationType);
        Event eventOffRouteNotInSquare = generateEvent(pointOffRouteNotInSquare,transportationType);
        Event eventOnCorner = generateEvent(pointOnCorner,transportationType);

        return  new ArrayList<>(Arrays.asList(
                eventOnRoute1,
                eventOnRoute2,
                eventOnRoute3,
                eventOffRouteInSquare,
                eventOnLineNotInCircle,
                eventOffRouteNotInSquare,
                eventOnCorner));
    }

    private static Event generateEvent(LatLon latLon,TransportationType transportationType) {
        return new GenericEvent(
                latLon,
                random.nextInt() + "",
                Collections.singleton(generateJam()),
                generateEventType(),
                new WazeEventPublisher(),
                Collections.singleton(transportationType));
    }


}
