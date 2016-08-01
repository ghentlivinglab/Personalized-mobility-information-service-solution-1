package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.Set;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIRouteJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateRoute;
import static org.junit.Assert.*;

/**
 * Created by Michael on 4/4/16.
 */
public class APIRouteMixinTest extends APIMixinTest<Route> {

    @Override
    public void testSerialization() throws Exception {
        Route route = generateRoute();
        ObjectNode node = testSerializationIdable(route);

        assertNotNull(node.get("waypoints"));
        for (int i = 0; i < node.get("waypoints").size(); i++) {
            assertTrue(route.getWaypoints().contains(parseLatLon(node.get("waypoints").get(i))));
        }

        assertNotNull(node.get("transportation_type"));
        assertEquals(route.getTransportationType(), TransportationType.valueOf(standardObjectMapper.treeToValue(node.get("transportation_type"), String.class)));

        assertNotNull(node.get("notify_for_event_types"));
        for (int i = 0; i < node.get("notify_for_event_types").size(); i++) {
            assertTrue(route.getNotifyForEventTypes().contains(EventType.valueOf(standardObjectMapper.treeToValue(node.get("notify_for_event_types").get(i).get("type"), String.class))));
        }

        assertNotNull(node.get("notify"));

        if (route.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.CELL_NUMBER)) {
            assertTrue(node.get("notify").get("cell_number").asBoolean());
        }

        if (route.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.EMAIL)) {
            assertTrue(node.get("notify").get("email").asBoolean());
        }

        assertNotNull(node.get("active"));
        assertEquals(route.isActive(), node.get("active").asBoolean());
    }

    @Override
    public void testDeserialization() throws Exception {
        ObjectNode node = generateAPIRouteJsonNode();
        Route route = testDeserializationIdable(node, Route.class);

        assertNotNull(route.getWaypoints());

        Set<String> wayPoints = new HashSet<>();
        for (int i = 0; i < node.get("waypoints").size(); i++) {
            wayPoints.add(node.get("waypoints").get(i).toString());
        }

        for (LatLon wayPoint : route.getWaypoints()) {
            assertTrue(wayPoints.contains(convertLatLon(wayPoint).toString()));
        }

        assertNotNull(route.getTransportationType());
        assertEquals(node.get("transportation_type"), standardObjectMapper.valueToTree(route.getTransportationType()));

        assertNotNull(route.getNotifyForEventTypes());

        Set<String> types = new HashSet<>();
        for (int i = 0; i < node.get("notify_for_event_types").size(); i++) {
            types.add(node.get("notify_for_event_types").get(i).get("type").toString());
        }

        for (EventType type : route.getNotifyForEventTypes()) {
            assertTrue(types.contains(standardObjectMapper.valueToTree(type).toString()));
        }

        assertNotNull(route.getNotificationMedia());

        if (node.get("notify").get("cell_number").asBoolean()) {
            assertTrue(route.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.CELL_NUMBER));
        }

        if (node.get("notify").get("email").asBoolean()) {
            assertTrue(route.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.EMAIL));
        }

        assertNotNull(route.isActive());
        assertEquals(node.get("active"), standardObjectMapper.valueToTree(route.isActive()));
    }

    @Override
    public void testDeserializationWithMissingFields() throws Exception {
        String[] fields = new String[] {
                "waypoints",
                "transportation_type",
                "notify_for_event_types",
                "notify",
                "active",
        };

        ObjectNode node = generateAPIRouteJsonNode();
        deserializationWithMissingFields(fields, node, Route.class);
    }
}
