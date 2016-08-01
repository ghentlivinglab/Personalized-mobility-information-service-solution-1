package be.ugent.vopro5.backend.businesslayer.applicationfacade.mock;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Identifiable;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessObject;
import be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization.DAOObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 29/02/16.
 */

/**
 *
 * The generic parameter T can be any Identifiable type.
 *
 * @param <T>
 */
public class MockDAO<T extends Identifiable> implements DataAccessObject<T> {
    private final Class<T> typeClass;
    protected List<String> db;
    private ObjectMapper mapper;

    /**
     *
     * The constructor for the MockDAO, which functions as the mocked
     * version of the MongoDAO. Initialize the Class for the type to be used,
     * a surrogate for the database in the form of List of Strings, and an ObjectMapper.
     *
     * @param typeClass
     */
    public MockDAO(Class<T> typeClass) {
        this.typeClass = typeClass;
        db = new ArrayList<>();
        mapper = new DAOObjectMapper();
    }

    @Override
    public void insert(T obj) {
        db.add(writeValue(obj));
    }

    @Override
    public T find(String id) {
        return db.stream().map(this::readValue).filter(t -> t.getIdentifier().toString().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<T> listAll() {
        return db.stream().map(this::readValue).collect(Collectors.toList());
    }
    @Override
    public void delete(T obj) {
        db = db.stream().map(this::readValue).filter(t -> !t.getIdentifier().equals(obj.getIdentifier())).map(this::writeValue).collect(Collectors.toList());
    }

    @Override
    public void update(T obj) {
        db = db.stream().map(this::readValue).filter(t -> !t.getIdentifier().equals(obj.getIdentifier())).map(this::writeValue).collect(Collectors.toList());
        db.add(writeValue(obj));
    }

    protected ObjectMapper getMapper() {
        return mapper;
    }

    protected T readValue(String t) {
        try {
            return mapper.readValue(t, typeClass);
        } catch (IOException e) {
            throw new RuntimeException("Error during reading of object", e);
        }
    }

    protected String writeValue(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during writing of object", e);
        }
    }
}
