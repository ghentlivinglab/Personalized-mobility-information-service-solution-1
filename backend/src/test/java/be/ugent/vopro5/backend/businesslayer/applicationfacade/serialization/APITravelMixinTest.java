package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPITravelJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateTravel;
import static org.junit.Assert.*;

/**
 * Created by Michael Weyns on 4/4/16.
 */
public class APITravelMixinTest extends APIMixinTest<Travel> {

    @Override
    public void testSerialization() throws Exception {
        Travel travel = generateTravel();
        ObjectNode node = testSerializationIdable(travel);

        assertNotNull(node.get("name"));
        assertEquals(travel.getName(), standardObjectMapper.treeToValue(node.get("name"), String.class));

        assertNotNull(node.get("is_arrival_time"));
        assertEquals(travel.isArrivalTime(), node.get("is_arrival_time").asBoolean());

        assertNotNull(node.get("startpoint"));
        assertEquals(travel.getStartPoint(), parseAddress(node.get("startpoint")));

        assertNotNull(node.get("endpoint"));
        assertEquals(travel.getEndPoint(), parseAddress(node.get("endpoint")));

        assertNotNull(node.get("time_interval"));
        TimeIntervalConstraint timeIntervalConstraint = null;
        for (TimeConstraint constraint : travel.getTimeConstraints()) {
            if (constraint instanceof TimeIntervalConstraint) {
                timeIntervalConstraint = (TimeIntervalConstraint) constraint;
                break;
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("nl", "be"));
        assertEquals(formatter.format(timeIntervalConstraint.getStarttime()).toString(), standardObjectMapper.treeToValue(node.get("time_interval").get(0), String.class));
        assertEquals(formatter.format(timeIntervalConstraint.getEndtime()).toString(), standardObjectMapper.treeToValue(node.get("time_interval").get(1), String.class));

        WeekDayConstraint weekDayConstraint = null;
        for (TimeConstraint constraint : travel.getTimeConstraints()) {
            if (constraint instanceof WeekDayConstraint) {
                weekDayConstraint = (WeekDayConstraint) constraint;
                break;
            }
        }

        assertNotNull(node.get("recurring"));
        for (int i = 0; i < node.get("recurring").size(); i++) {
            assertEquals(weekDayConstraint.getRecurring()[i], node.get("recurring").get(i).asBoolean());
        }
    }

    @Override
    public void testDeserialization() throws Exception {
        ObjectNode node = generateAPITravelJsonNode();
        Travel travel = testDeserializationIdable(node, Travel.class);

        assertNotNull(travel.getName());
        assertEquals(node.get("name"), standardObjectMapper.valueToTree(travel.getName()));

        assertNotNull(travel.getStartPoint());
        assertEquals(node.get("startpoint"), convertAddress(travel.getStartPoint()));

        assertNotNull(travel.getEndPoint());
        assertEquals(node.get("endpoint"), convertAddress(travel.getEndPoint()));

        assertNotNull(travel.getTimeConstraints());
        int size1 = travel.getTimeConstraints().stream().filter(c -> c instanceof TimeIntervalConstraint).collect(Collectors.toList()).size();
        assertEquals(1, size1);

        int size2 = travel.getTimeConstraints().stream().filter(c -> c instanceof WeekDayConstraint).collect(Collectors.toList()).size();
        assertEquals(1, size2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("nl", "be"));
        for (TimeConstraint constraint : travel.getTimeConstraints()) {
            if (constraint instanceof TimeIntervalConstraint) {
                TimeIntervalConstraint timeIntervalConstraint = (TimeIntervalConstraint) constraint;
                assertEquals(node.get("time_interval").get(0), standardObjectMapper.valueToTree(formatter.format(timeIntervalConstraint.getStarttime())));
                assertEquals(node.get("time_interval").get(1), standardObjectMapper.valueToTree(formatter.format(timeIntervalConstraint.getStarttime().plusNanos(timeIntervalConstraint.getDuration().toNanos()))));
            } else if (constraint instanceof  WeekDayConstraint) {
                WeekDayConstraint weekDayConstraint = (WeekDayConstraint) constraint;
                for (int i = 0; i < weekDayConstraint.getRecurring().length; i++) {
                    assertEquals(node.get("recurring").get(i), standardObjectMapper.valueToTree(weekDayConstraint.getRecurring()[i]));
                }
            } else {
                fail();
            }
        }
    }

    @Override
    public void testDeserializationWithMissingFields() throws Exception {
        String[] fields = new String[] {
                "name",
                "is_arrival_time",
                "startpoint",
                "endpoint",
                "time_interval",
                "recurring"
        };

        ObjectNode node = generateAPITravelJsonNode();
        deserializationWithMissingFields(fields, node, Travel.class);
    }
}
