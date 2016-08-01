package be.ugent.vopro5.backend.businesslayer.businessentities;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;
import com.lambdaworks.crypto.SCryptUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by Michael Weyns on 4/6/16.
 */
public class OperatorTest extends AbstractBusinessEntityTest<Operator> {

    private static Random random = new Random();
    private String password;

    @Test
    @Override
    public void testConstructor() throws ValidationException {
        Operator operator = generateOperator();
        notNull(operator);
    }

    @Before
    public void setUp() {
        password = SCryptUtil.scrypt(random.nextInt() + "", 16384, 8, 1);
    }

    @Test
    public void testConstructorWithoutUUID() {
        Operator operator = new Operator(
                "test@example.com",
                password
        );
        notNull(operator);
    }

    @Test(expected = ValidationException.class)
    public void testNoId() {
        new Operator(null, generateEmailNotificationMediumObject(), password);
    }

    @Test
    public void testTransferProperties() {
        Operator operator = generateOperator();
        Operator other = generateOperator();

        operator.transferProperties(other);
        assertEquals(other.getEmail(), operator.getEmail());
        assertEquals(other.getPassword(), operator.getPassword());
    }
}
