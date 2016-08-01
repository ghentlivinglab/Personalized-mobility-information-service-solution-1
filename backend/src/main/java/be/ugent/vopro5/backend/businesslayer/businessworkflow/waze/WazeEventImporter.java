package be.ugent.vopro5.backend.businesslayer.businessworkflow.waze;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.EventController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Created on 13/03/16.
 */
public class WazeEventImporter {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(WazeEventImporter.class);
    private static final String SUBTYPE = "subtype";
    private static final String CANT_FIND_EVENT_TYPE = "I can't find this event type I received from Waze: ";
    private static final String BLOCKING_ALERT_UUID = "blockingAlertUuid";
    private static final String LOCATION = "location";
    private static final String REPORT_DESCRIPTION = "reportDescription";

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private EventController eventController;

    @Autowired
    private WazeInputStreamProvider wazeInputStreamProvider;

    /**
     * Parse and import all the waze events
     */
    public void importEvents() {
        logger.info("[" + (new Date()).toString() + "] Waze event import: start");
        try {
            List<WazeEvent> wazeEvents;
            try (InputStream inputStream = wazeInputStreamProvider.getInputStream()) {
                wazeEvents = parseEvents(inputStream);
            }
            Map<UUID, WazeEvent> wazeEventsMap = new HashMap<>();
            wazeEvents.stream().forEach(t -> wazeEventsMap.put(t.getWazeUUID(), t));
            List<WazeEvent> oldEvents = dataAccessProvider.getDataAccessContext().getEventDAO().listAllActiveWaze();
            for (WazeEvent event : oldEvents) {
                Optional<WazeEvent> wazeEvent = Optional.ofNullable(wazeEventsMap.get(event.getWazeUUID()));
                wazeEvent.ifPresent(wazeEvents::remove);
                if (wazeEvent.isPresent()) {
                    boolean changed = !event.getDescription().equals(wazeEvent.get().getDescription())
                            || event.getJams().size() != wazeEvent.get().getJams().size();
                    if (!changed) {
                        List<Jam> origJams = new ArrayList<>(event.getJams());
                        List<Jam> newJams = new ArrayList<>(wazeEvent.get().getJams());
                        Collections.sort(origJams, (j1, j2) -> j1.toString().compareTo(j2.toString()));
                        Collections.sort(newJams, (j1, j2) -> j1.toString().compareTo(j2.toString()));
                        for (int i = 0; i < origJams.size(); i++) {
                            Jam origJam = origJams.get(i);
                            Jam newJam = newJams.get(i);
                            changed |= origJam.getSpeed() != newJam.getSpeed()
                                    || origJam.getDelay() != newJam.getDelay()
                                    || origJam.getPoints().size() != origJam.getPoints().size();
                            if (!changed) {
                                for (int j = 0; j < origJam.getPoints().size(); j++) {
                                    changed |= !origJam.getPoints().get(j).equals(newJam.getPoints().get(j));
                                }
                            }
                        }
                    }
                    if (changed) {
                        event.transferProperties(wazeEvent.get());
                        eventController.updateEvent(event);
                    }
                } else {
                    event.setActive(false);
                    eventController.updateEvent(event);
                }
            }
            eventController.createEvents(wazeEvents);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("[" + (new Date()).toString() + "] Waze event import: finished");
    }

    private List<WazeEvent> parseEvents(InputStream stream) throws IOException {
        JsonNode node = mapper.readTree(stream);
        ArrayNode jamNodes = (ArrayNode) node.get("jams");

        List<WazeEvent> events = new ArrayList<>();
        JsonNode eventNodes = node.get("alerts");
        for (JsonNode n : eventNodes) {
            try {
                events.add(parseEvent(n, jamNodes));
            } catch (RuntimeException e) {
                logger.error(e);
            }
        }

        for (JsonNode jamNode : jamNodes) {
            if (!jamNode.has(BLOCKING_ALERT_UUID) && jamNode.get("line").size() > 1) {
                events.add(createJamEvent(jamNode));
            }
        }

        return events;
    }

    private WazeEvent createJamEvent(JsonNode jamNode) {
        Jam jam = parseJam(jamNode);

        return new WazeEvent(UUID.randomUUID(), jam.getPoints().get(0), true, LocalDateTime.now(), LocalDateTime.now(), "", Collections.singleton(jam), EventType.JAM, Collections.singleton(TransportationType.CAR), UUID.fromString(jamNode.get("uuid").asText()));
    }

    private WazeEvent parseEvent(JsonNode eventNode, ArrayNode jamNodes) {
        UUID wazeUUID = UUID.fromString(eventNode.get("uuid").asText());

        EventType type;
        if (eventNode.get(SUBTYPE).asText().equals("")) {
            try {
                type = EventType.valueOf(eventNode.get("type").asText());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(CANT_FIND_EVENT_TYPE + eventNode.get("type").asText(), e);
            }
        } else {
            try {
                type = EventType.valueOf(eventNode.get(SUBTYPE).asText());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(CANT_FIND_EVENT_TYPE + eventNode.get(SUBTYPE).asText(), e);
            }
        }

        Set<Jam> jams = new HashSet<>();
        for (JsonNode node : jamNodes) {
            if (node.has(BLOCKING_ALERT_UUID) && node.get(BLOCKING_ALERT_UUID).equals(eventNode.get("uuid"))) {
                jams.add(parseJam(node));
            }
        }

        LatLon location = new LatLon(
                eventNode.get(LOCATION).get("y").asDouble(),
                eventNode.get(LOCATION).get("x").asDouble());

        String description = "";
        if (eventNode.has(REPORT_DESCRIPTION)) {
            description = eventNode.get(REPORT_DESCRIPTION).asText();
        }

        long pubMillis = eventNode.get("pubMillis").asLong();
        LocalDateTime publicationTime = LocalDateTime.ofEpochSecond(pubMillis / 1000, 0, ZoneOffset.UTC);

        Set<TransportationType> transportationTypes = new HashSet<>();
        Collections.addAll(transportationTypes, TransportationType.CAR, TransportationType.BIKE);

        return new WazeEvent(UUID.randomUUID(), location, true, publicationTime, LocalDateTime.now().plusMinutes(1), description, jams, type, transportationTypes, wazeUUID);
    }

    private Jam parseJam(JsonNode n) throws ValidationException {
        int delay = n.get("delay").asInt();
        if (delay == -1) {
            delay = 0; // -1 in Waze simply means no delay
        }
        float speed = (float) n.get("speed").asDouble();
        List<LatLon> points = new ArrayList<>();
        JsonNode lineNode = n.get("line");
        for (int i = 0; i < lineNode.size(); i++) {
            points.add(new LatLon(lineNode.get(i).get("y").asDouble(), lineNode.get(i).get("x").asDouble()));
        }
        return new Jam(points, speed, delay);
    }
}
