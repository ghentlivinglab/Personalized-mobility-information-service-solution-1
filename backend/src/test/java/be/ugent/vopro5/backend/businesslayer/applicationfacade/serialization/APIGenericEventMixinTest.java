package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.util.*;

import static be.ugent.vopro5.backend.util.ObjectComparison.assertJamsEqual;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIEventJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateGenericEvent;
import static org.junit.Assert.*;

/**
 * Created by Michael Weyns on 3/31/16.
 */
public class APIGenericEventMixinTest extends APIMixinTest<GenericEvent> {

    @Override
    public void testSerialization() throws Exception {
        GenericEvent event = generateGenericEvent();
        ObjectNode node = testSerializationIdable(event);

        assertNotNull(node.get("coordinates"));
        assertEquals(event.getLocation(), parseLatLon(node.get("coordinates")));

        assertNotNull(node.get("publication_time"));
        assertEquals(event.getPublicationTime(), LocalDateTime.parse(standardObjectMapper.treeToValue(node.get("publication_time"), String.class)));

        assertNotNull(node.get("last_edit_time"));
        assertEquals(event.getLastEditTime(), LocalDateTime.parse(standardObjectMapper.treeToValue(node.get("last_edit_time"), String.class)));

        assertNotNull(node.get("source"));
        assertEquals(event.getSource().getName(), standardObjectMapper.treeToValue(node.get("source").get("name"), String.class));
        assertEquals(event.getSource().getImage(), standardObjectMapper.treeToValue(node.get("source").get("icon_url"), String.class));
        assertEquals(event.getSource().getType(), EventPublisher.EventPublisherType.valueOf(standardObjectMapper.treeToValue(node.get("source").get("type"), String.class)));

        assertNotNull(node.get("type"));
        assertEquals(event.getEventType(), EventType.valueOf(standardObjectMapper.treeToValue(node.get("type").get("type"), String.class)));

        assertNotNullAndEqToValue(node,event.getDescription(),"description");

        assertNotNull(node.get("active"));
        assertEquals(event.isActive(), node.get("active").asBoolean());

        assertNotNull(node.get("jams"));

        Comparator<Jam> comparator = (j1, j2) -> Float.compare(j1.getSpeed(), j2.getSpeed());

        List<Jam> jams = new ArrayList<>();
        List<Jam> orJams = new ArrayList<>(event.getJams());

        for (int i = 0; i < node.get("jams").size(); i++) {
            jams.add(parseJam(node.get("jams").get(i)));
        }

        Collections.sort(jams, comparator);
        Collections.sort(orJams, comparator);

        for (int i = 0; i < orJams.size(); i++) {
            assertJamsEqual(jams.get(i), orJams.get(i));
        }

        for (int i = 0; i < node.get("relevant_for_transportation_types").size(); i++) {
            assertTrue(event.getRelevantForTransportationTypes().contains(TransportationType.valueOf(standardObjectMapper.treeToValue(node.get("relevant_for_transportation_types").get(i), String.class))));
        }
    }

    @Override
    public void testDeserialization() throws Exception {
        ObjectNode node = generateAPIEventJsonNode();
        GenericEvent event = testDeserializationIdable(node, GenericEvent.class);

        assertNotNull(event.getLocation());
        assertEquals(node.get("coordinates"), convertLatLon(event.getLocation()));

        assertNotNullAndEqToTree(node,event.getDescription(),"description");

        assertNotNull(event.getSource());
        assertEquals(node.get("source").get("name"), standardObjectMapper.valueToTree(event.getSource().getName()));
        assertEquals(node.get("source").get("type"), standardObjectMapper.valueToTree(event.getSource().getType()));
        assertEquals(node.get("source").get("icon_url"), standardObjectMapper.valueToTree(event.getSource().getImage()));

        assertNotNull(event.getEventType());
        assertEquals(node.get("type").get("type"), standardObjectMapper.valueToTree(event.getEventType()));

        assertNotNull(event.getJams());

        Set<String> jams = new HashSet<>();
        for (int i = 0; i < node.get("jams").size(); i++) {
            jams.add(node.get("jams").get(i).toString());
        }

        for (Jam jam : event.getJams()) {
            assertTrue(jams.contains(convertJam(jam).toString()));
        }

        Set<JsonNode> types = new HashSet<>();
        for (int i = 0; i < node.get("relevant_for_transportation_types").size(); i++) {
            types.add(node.get("relevant_for_transportation_types").get(i));
        }

        for (TransportationType type : event.getRelevantForTransportationTypes()) {
            assertTrue(types.contains(standardObjectMapper.valueToTree(type)));
        }
    }

    @Override
    public void testDeserializationWithMissingFields() throws Exception {
        String[] fields = new String[] {
                "coordinates",
                "description",
                "type",
                "source",
                "jams",
                "relevant_for_transportation_types"
        };

        ObjectNode node = generateAPIEventJsonNode();

        deserializationWithMissingFields(fields, node, GenericEvent.class);
    }
}
