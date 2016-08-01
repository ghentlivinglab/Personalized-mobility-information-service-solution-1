package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by thibault on 3/1/16.
 */

/**
 *
 * The generic parameter T stands for any business entity.
 *
 * @param <T>
 */
public abstract class AbstractBusinessEntityTest<T> {


    /**
     * Set up the testing environment for the EntityTests by initializing the Validator.
     */
    @Before
    public void setUp() throws ValidationException {
    }

    /**
     * Test the creation of a business entity.
     */
    @Test
    public abstract void testConstructor() throws ValidationException;

    protected void threeWayNotEquals(Object obj, Object obj2){
        assertNotEquals(obj, obj2);
        assertNotEquals(obj2, obj);
        assertNotEquals(obj.hashCode(), obj2.hashCode());
    }

    protected void twoWayEquals(Object obj, Object obj2){
        assertEquals(obj, obj2);
        assertEquals(obj.hashCode(), obj2.hashCode());
    }

    protected void notNullNotBlankString(Object obj){
        assertNotNull(obj);
        assertNotEquals(obj,"");
    }
}
