package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Identifiable;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessObject;
import be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization.DAOObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * The generic parameter B can be any Identifiable type.
 *
 * @param <B>
 */
abstract class MongoDAO<B extends Identifiable> implements DataAccessObject<B> {
    private static final Logger logger = LogManager.getLogger(MongoDAO.class.getName());
    protected final MongoCollection<Document> collection;
    private final Class<B> typeClass;
    private final ObjectMapper mapper;

    /**
     *
     * Initialize the Class for the type to be used,
     * the database, and a collection name.
     *
     * @param db The database
     * @param collectionName The name of the collection
     * @param typeClass the Class for the type to be used
     */
    MongoDAO(MongoDatabase db, String collectionName, Class<B> typeClass) {
        this.typeClass = typeClass;
        mapper = new DAOObjectMapper();
        collection = db.getCollection(collectionName);
    }

    @Override
    public void insert(B obj) {
        String jsonString = writeValue(obj);
        collection.insertOne(Document.parse(jsonString));
    }

    @Override
    public B find(String id) {
        Document doc = collection.find(new Document("_id", id)).first();
        B obj = null;
        if(doc!=null) {
            obj = readValue(doc);
        }
        return obj;
    }

    @Override
    public List<B> listAll() {
        List<B> list = new ArrayList<>();
        collection.find().map(this::readValue).forEach((Consumer<? super B>) list::add);
        return list;
    }

    @Override
    public void delete(B obj) {
        collection.deleteOne(new Document("_id", obj.getIdentifier().toString()));
    }

    @Override
    public void update(B obj){
        Document doc = Document.parse(writeValue(obj));
        doc.remove("_id", obj.getIdentifier().toString());
        collection.updateOne(new Document("_id", obj.getIdentifier().toString()), new Document("$set", doc));
    }

    protected ObjectMapper getMapper() {
        return mapper;
    }

    protected B readValue(Document doc) {
        String jsonString = doc.toJson();
        try {
            JsonNode node = mapper.readTree(jsonString);
            return mapper.treeToValue(node, typeClass);
        } catch (IOException e) {
            logger.error(e);
            throw new DAOException.DAOReadException("Error during reading of object", e);
        }
    }

    protected String writeValue(B b) {
        ObjectNode node = mapper.valueToTree(b);
        return node.toString();
    }
}
