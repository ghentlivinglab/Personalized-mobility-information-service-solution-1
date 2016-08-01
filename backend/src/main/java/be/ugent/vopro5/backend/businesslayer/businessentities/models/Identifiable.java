package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.util.UUID;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.notNull;

/**
 * abstract class which represents an identifiable
 * it gives inheriting classes a UUID and makes them
 * individually addressable from the database
 */
public abstract class Identifiable {

    private final UUID identifier;

    protected Identifiable(UUID identifier) throws ValidationException {
        notNull(identifier, "Identifier can not be null");
        this.identifier = identifier;
    }

    /**
     * @return The identifier of the resource.
     */
    public UUID getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Identifiable identifiable = (Identifiable) o;

        return identifiable.identifier.toString().equals(identifier.toString());
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return "Identifiable{" +
                "identifier=" + identifier +
                '}';
    }
}
