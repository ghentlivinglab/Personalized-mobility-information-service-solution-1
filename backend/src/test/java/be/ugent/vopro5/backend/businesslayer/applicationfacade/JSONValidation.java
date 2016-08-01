package be.ugent.vopro5.backend.businesslayer.applicationfacade;

import be.ugent.vopro5.backend.businesslayer.util.constants.ControllerConstants;
import com.fasterxml.jackson.databind.JsonNode;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Fields.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by anton on 3/27/16.
 */
public class JSONValidation {

    /**
     * Asserts that the representation has all required fields and they are all valid
     * @param node The user in JSON representation
     */
    public static void assertIsValidUser(JsonNode node) {
        assertTrue(node.get(ID_FIELD).isTextual());
        assertTrue(node.get(EMAIL_FIELD).isTextual());
        if (node.has(CELL_NUMBER_FIELD)) {
            assertTrue(node.get(CELL_NUMBER_FIELD).isTextual());
            assertNotEquals(node.get(CELL_NUMBER_FIELD).asText(), "");
        }
        assertTrue(node.get(MUTE_NOTIFICATIONS_FIELD).isBoolean());
        assertNotEquals(node.get(ID_FIELD).asText(), "");
        assertNotEquals(node.get(EMAIL_FIELD).asText(), "");
        assertTrue(node.get(EMAIL_FIELD).asText().contains(AT_SYMBOL));
    }

    /**
     * Asserts that the error representation has all required fields and they are all valid
     * @param node The error in JSON representation
     */
    public static void assertIsValidError(JsonNode node) {
        assertTrue(node.get(ControllerConstants.STATUS).isInt());
        assertNotEquals(node.get(ControllerConstants.STATUS).asInt(), 0);

        assertTrue(node.get(MESSAGE_FIELD).isTextual());
        assertNotEquals(node.get(MESSAGE_FIELD).asText(), "");

        assertTrue(node.get(ControllerConstants.FIELDS).isArray());
    }

    /**
     * Asserts that the travel representation has all required fields and they are all valid
     * @param node The travel in JSON representation
     */
    public static void assertIsValidTravel(JsonNode node) {
        assertTrue(node.get(ID_FIELD).isTextual());
        assertTrue(node.get(NAME_FIELD).isTextual());
        assertTrue(node.get(TIME_INTERVAL).isArray());
        assertTrue(node.get(IS_ARRIVAL_TIME).isBoolean());
        assertTrue(node.get(RECURRING).isArray());
        assertTrue(node.get(STARTPOINT).isObject());
        assertTrue(node.get(ENDPOINT).isObject());
    }
    /**
     * Asserts that the event representation has all required fields and they are all valid
     * @param node The event in JSON representation
     */
    public static void assertIsValidEvent(JsonNode node) {
        assertTrue(node.get(ID_FIELD).isTextual());
        assertTrue(node.get(COORDINATES).isObject());
        assertTrue(node.get(DESCRIPTION).isTextual());
        assertTrue(node.get(ACTIVE).isBoolean());
        assertTrue(node.get(PUBLICATION_TIME).isTextual());
        assertTrue(node.get(TYPE).isObject());
        assertTrue(node.get(LAST_EDIT_TIME).isTextual());
        assertTrue(node.get(JAMS).isArray());
        assertTrue(node.get(SOURCE).isObject());
        assertTrue(node.get(RELEVANT_FOR_TRANSPORTATION_TYPES).isArray());
    }
    /**
     * Asserts that the pointOfInterest representation has all required fields and they are all valid
     * @param node The travel in JSON representation
     */
    public static void assertIsValidPOI(JsonNode node) {
        assertTrue(node.get(ID_FIELD).isTextual());
        assertTrue(node.get(NAME_FIELD).isTextual());
        assertTrue(node.get(ADDRESS).isObject());
        assertTrue(node.get(ACTIVE).isBoolean());
        assertTrue(node.get(RADIUS).isNumber());
        assertTrue(node.get(NOTIFY_FOR_EVENT_TYPES).isArray());
        assertTrue(node.get(NOTIFY).isObject());
    }

    public static void assertIsValidRoute(JsonNode node) {
        assertTrue(node.get(ID_FIELD).isTextual());
        assertTrue(node.get(WAYPOINTS).isArray());
        assertTrue(node.get(TRANSPORTATION_TYPE).isTextual());
        assertTrue(node.get(NOTIFY_FOR_EVENT_TYPES).isArray());
        assertTrue(node.get(NOTIFY).isObject());
        assertTrue(node.get(ACTIVE).isBoolean());
    }

    public static void assertIsValidOperator(JsonNode node) {
        assertTrue(node.get(ID_FIELD).isTextual());
        assertTrue(node.get(EMAIL_FIELD).isTextual());
        assertTrue(node.get(PASSWORD_FIELD).isTextual());

    }
}
