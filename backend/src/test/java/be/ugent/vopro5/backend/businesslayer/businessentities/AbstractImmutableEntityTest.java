package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

/**
 * Created by anton on 21.03.16.
 */
public abstract class AbstractImmutableEntityTest<T> extends AbstractBusinessEntityTest<T> {

    public abstract void testEquals() throws ValidationException;

    public abstract void testHashCodeUniqueness() throws ValidationException;

    protected abstract void testNonEquality(T t, T t2) throws ValidationException;
}
