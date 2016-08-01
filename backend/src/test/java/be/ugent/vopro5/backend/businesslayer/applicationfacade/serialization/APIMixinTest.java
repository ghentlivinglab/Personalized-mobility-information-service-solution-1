package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.businesslayer.util.mapping.MyObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by maarten on 31.03.16.
 */
public abstract class APIMixinTest<B extends Identifiable> {
    ObjectMapper objectMapper;
    ObjectMapper standardObjectMapper;


    @Before
    public void setUp() throws Exception {
        objectMapper = new MyObjectMapper();
        standardObjectMapper = new ObjectMapper();
        objectMapper.registerModule(new RESTModule());
    }

    @Test
    public abstract void testSerialization() throws Exception;

    public ObjectNode testSerializationIdable(B b) throws Exception{

        ObjectNode node = objectMapper.valueToTree(b);
        assertNotNull(node);
        assertNotNullAndEqToValue(node, b.getIdentifier().toString(), "id");

        return node;
    }

    @Test
    public abstract void testDeserialization() throws Exception;

    public B testDeserializationIdable(ObjectNode node, Class<B> typeClass) throws Exception{
        B b = objectMapper.treeToValue(node, typeClass);

        assertNotNullAndEqToTree(node,b.getIdentifier(),"id");

        return b;
    }

    @Test
    public abstract void testDeserializationWithMissingFields() throws Exception;

    public void deserializationWithMissingFields(String[] fields, ObjectNode node, Class<B> typeClass) throws Exception{
        for (String field : fields) {
            node.remove(field);
            try {
                objectMapper.treeToValue(node, typeClass);
                fail();
            } catch (Exception e) {
                assertTrue(true);
            }
        }
    }

    protected LatLon parseLatLon(JsonNode latLonNode) {
        double lat = latLonNode.get("lat").asDouble();
        double lon = latLonNode.get("lon").asDouble();

        return new LatLon(lat, lon);
    }

    protected Jam parseJam(JsonNode jamNode) {
        JsonNode pointsNode = jamNode.get("points");
        List<LatLon> points = new ArrayList<>();
        for (int i = 0; i < pointsNode.size(); i++) {
            points.add(parseLatLon(pointsNode.get(i)));
        }

        float speed = jamNode.get("speed").floatValue();
        int delay = jamNode.get("delay").asInt();

        return new Jam(points, speed, delay);
    }

    protected Address parseAddress(JsonNode addressNode) throws Exception {
        String street = standardObjectMapper.treeToValue(addressNode.get("street"), String.class);
        String houseNumber = standardObjectMapper.treeToValue(addressNode.get("housenumber"), String.class);
        String cityName = standardObjectMapper.treeToValue(addressNode.get("city"), String.class);
        String postalCode = standardObjectMapper.treeToValue(addressNode.get("postal_code"), String.class);
        String country = standardObjectMapper.treeToValue(addressNode.get("country"), String.class);
        LatLon coordinates = parseLatLon(addressNode.get("coordinates"));

        return new Address(street, houseNumber, new City(cityName, postalCode), country, coordinates);
    }

    protected JsonNode convertLatLon(LatLon latLon) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();

        node.put("lat", latLon.getLat());
        node.put("lon", latLon.getLon());

        return node;
    }

    protected JsonNode convertJam(Jam jam) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        ArrayNode pointsNode = JsonNodeFactory.instance.arrayNode();
        for (LatLon point : jam.getPoints()) {
            pointsNode.add(convertLatLon(point));
        }

        node.set("points", pointsNode);
        node.put("speed", jam.getSpeed());
        node.put("delay", jam.getDelay());

        return node;
    }

    protected JsonNode convertAddress(Address address) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();

        node.put("street", address.getStreet());
        node.put("housenumber", address.getHouseNumber());
        node.put("city", address.getCity().getName());
        node.put("postal_code", address.getCity().getPostalCode());
        node.put("country", address.getCountry());
        node.set("coordinates", convertLatLon(address.getCoordinates()));

        return node;
    }

    protected void assertNotNullAndEqToValue(ObjectNode node, Object correct, String nodeFieldName)  throws Exception{
        assertNotNull(node.get(nodeFieldName));
        assertEquals(correct, standardObjectMapper.treeToValue(node.get(nodeFieldName), String.class));
    }

    protected void assertNotNullAndEqToTree(ObjectNode node, Object o, String nodeFieldName)  throws Exception{
        assertNotNull(o);
        assertEquals(node.get(nodeFieldName), standardObjectMapper.valueToTree(o));
    }

}
