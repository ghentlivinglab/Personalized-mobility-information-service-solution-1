package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.WazeEvent;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.EventDAO;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * Created on 1/03/16.
 */
public class MongoEventDAO extends MongoDAO<Event> implements EventDAO {
    private static final String EVENTS = "events";
    private static final String ACTIVE = "active";
    private final MongoCollection<Document> collection;

    /**
     *
     * Inherit functionality from superclass.
     *
     * @param db The database
     */
    public MongoEventDAO(MongoDatabase db) {
        super(db, EVENTS, Event.class);
        this.collection = db.getCollection(EVENTS);
    }

    @Override
    public List<WazeEvent> listAllActiveWaze() {
        FindIterable<Document> wazeEvents = collection.find(and(eq("class", "WAZE_EVENT"), eq(ACTIVE, true)));
        List<WazeEvent> events = new ArrayList<>();
        wazeEvents.map(this::readValue).forEach((Consumer<? super Event>) e -> events.add(((WazeEvent) e)));
        return events;
    }

    @Override
    public List<Event> listAllActive() {
        FindIterable<Document> eventDocs = collection.find(eq("active", true)).sort(new Document("last_edit_time", -1));
        List<Event> events = new ArrayList<>();
        eventDocs.map(this::readValue).forEach((Consumer<? super Event>) events::add);
        return events;
    }
}