package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.LatLon;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import org.junit.Before;
import org.junit.Test;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateLatLon;
import static org.junit.Assert.assertEquals;

/**
 * Created by evert on 27/03/16.
 */
public abstract class TypeResolverTest {

    protected TypeIdResolver typeResolver;

    @Before
    public abstract void setUp() throws Exception;

    @Test
    public abstract void idFromValue() throws Exception;

    @Test(expected = UnsupportedOperationException.class)
    public void idFromUnknownValue() throws Exception {
        LatLon latLon = generateLatLon();
        typeResolver.idFromValue(latLon);
    }

    @Test
    public abstract void idFromValueAndType() throws Exception;

    @Test(expected = UnsupportedOperationException.class)
    public void idFromUnknownValueAndType() throws Exception {
        typeResolver.idFromValueAndType(null, LatLon.class);
    }

    @Test
    public abstract void typeFromId() throws Exception;

    @Test
    public void typeFromUnknownId() {
        int i = 0;

        try {
            typeResolver.typeFromId(null, null);
        } catch (UnsupportedOperationException e) {
            i++;
        }

        try {
            typeResolver.typeFromId(null, "");
        } catch (UnsupportedOperationException e) {
            i++;
        }

        try {
            typeResolver.typeFromId(null, "LJDKJFL");
        } catch (UnsupportedOperationException e) {
            i++;
        }

        assertEquals(3, i);
    }

    @Test
    public void getMechanism() throws Exception {
        assertEquals(JsonTypeInfo.Id.CUSTOM, typeResolver.getMechanism());
    }
}
