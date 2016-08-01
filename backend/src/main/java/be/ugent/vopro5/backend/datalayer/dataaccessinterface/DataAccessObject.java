package be.ugent.vopro5.backend.datalayer.dataaccessinterface;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Identifiable;

import java.util.List;
import java.util.UUID;

/**
 *
 * The generic parameter B can be any Identifiable type.
 *
 * @param <B>
 */
public interface DataAccessObject<B extends Identifiable> {

    /**
     * Insert a new object of type B in the database. The identifier field has to be null.
     *
     * @param obj The object to insert
     */
    void insert(B obj);

    /**
     * Find the object of type B in the database with the given id. Returns null if the object could not be found.
     *
     * @param id        The sting representation of the id the object has to have
     * @return The object with the given id
     */
    B find(String id);

    /**
     * List all objects of type B in the database
     *
     * @return All objects of this type
     */
    List<B> listAll();

    /**
     * Delete an object from the database
     *
     * @param obj The object to delete
     */
    void delete(B obj);

    /**
     * Update an object in the database
     *
     * @param obj The object to update
     */
    void update(B obj);
}
