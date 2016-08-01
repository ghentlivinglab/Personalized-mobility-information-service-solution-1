package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.EventType;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.util.ObjectGeneration;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashSet;
import java.util.Set;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIPOIJsonNode;
import static org.junit.Assert.*;

/**
 * Created by maarten on 07.04.16.
 */
public class APIPointOfInterestMixinTest extends APIMixinTest<PointOfInterest> {
    @Override
    public void testSerialization() throws Exception {
        PointOfInterest poi = ObjectGeneration.generatePointOfInterest();
        ObjectNode node = testSerializationIdable(poi);

        assertNotNull(node.get("active"));
        assertEquals(poi.isActive(), node.get("active").asBoolean());

        assertNotNull(node.get("notify"));

        if (poi.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.CELL_NUMBER)) {
            assertTrue(node.get("notify").get("cell_number").asBoolean());
        }

        if (poi.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.EMAIL)) {
            assertTrue(node.get("notify").get("email").asBoolean());
        }

        assertNotNull(node.get("address"));
        assertEquals(poi.getAddress(), parseAddress(node.get("address")));

        assertNotNull(node.get("name"));
        assertEquals(poi.getName(), standardObjectMapper.treeToValue(node.get("name"), String.class));

        assertNotNull(node.get("radius"));
        assertEquals(poi.getRadius(),node.get("radius").asInt());

        assertNotNull(node.get("notify_for_event_types"));
        for (int i = 0; i < node.get("notify_for_event_types").size(); i++) {
            assertTrue(poi.getNotifyForEventTypes().contains(EventType.valueOf(standardObjectMapper.treeToValue(node.get("notify_for_event_types").get(i).get("type"), String.class))));
        }
    }

    @Override
    public void testDeserialization() throws Exception {
        ObjectNode node = ObjectGeneration.generateAPIPOIJsonNode();
        PointOfInterest poi = testDeserializationIdable(node, PointOfInterest.class);

        assertNotNull(poi.getNotifyForEventTypes());

        Set<String> types = new HashSet<>();
        for (int i = 0; i < node.get("notify_for_event_types").size(); i++) {
            types.add(node.get("notify_for_event_types").get(i).get("type").toString());
        }

        for (EventType type : poi.getNotifyForEventTypes()) {
            assertTrue(types.contains(standardObjectMapper.valueToTree(type).toString()));
        }

        assertNotNull(poi.getNotificationMedia());

        if (node.get("notify").get("cell_number").asBoolean()) {
            assertTrue(poi.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.CELL_NUMBER));
        }

        if (node.get("notify").get("email").asBoolean()) {
            assertTrue(poi.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.EMAIL));
        }

        assertNotNull(poi.isActive());
        assertEquals(node.get("active"), standardObjectMapper.valueToTree(poi.isActive()));

        assertNotNull(poi.getRadius());
        assertEquals(node.get("radius").asInt(),poi.getRadius());

        assertNotNull(poi.getName());
        assertEquals(poi.getName(), standardObjectMapper.treeToValue(node.get("name"), String.class));

        assertNotNull(poi.getAddress());
        assertEquals(node.get("address"),convertAddress(poi.getAddress()));
    }

    @Override
    public void testDeserializationWithMissingFields() throws Exception {
        String[] fields = new String[] {
                "address",
                "name",
                "notify_for_event_types",
                "notify",
                "active",
                "radius"
        };

        ObjectNode node = generateAPIPOIJsonNode();
        deserializationWithMissingFields(fields, node, PointOfInterest.class);
    }
}
