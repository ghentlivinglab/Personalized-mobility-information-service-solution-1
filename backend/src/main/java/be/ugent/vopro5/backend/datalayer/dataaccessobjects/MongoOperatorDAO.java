package be.ugent.vopro5.backend.datalayer.dataaccessobjects;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.OperatorDAO;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by thibault on 4/4/16.
 */
public class MongoOperatorDAO extends MongoDAO<Operator> implements OperatorDAO {
    private static final String OPERATORS = "operators";

    private final MongoCollection<Document> collection;

    /**
     * Constructor for OperatorDAO.
     *
     * @param db
     */
    public MongoOperatorDAO(MongoDatabase db) {
        super(db, OPERATORS, Operator.class);
        collection = db.getCollection(OPERATORS);
    }

    @Override
    public Operator findByEmail(String email) {
        Document doc = collection.find(eq("email.value", email)).first();
        return doc != null ? readValue(doc) : null;
    }
}
