package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIOperatorJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateOperator;
import static org.junit.Assert.*;

/**
 * Created by Michael Weyns on 4/6/16.
 */
public class APIOperatorMixinTest extends APIMixinTest<Operator> {

    @Override
    public void testSerialization() throws Exception {
        Operator operator = generateOperator();
        ObjectNode node = testSerializationIdable(operator);

        assertNotNull(node.get("email"));
        assertEquals(operator.getEmail(), standardObjectMapper.treeToValue(node.get("email"), String.class));
    }

    @Override
    public void testDeserialization() throws Exception {
        ObjectNode node = generateAPIOperatorJsonNode();
        Operator operator = testDeserializationIdable(node, Operator.class);

        assertNotNull(operator.getEmail());
        assertEquals(node.get("email"), standardObjectMapper.valueToTree(operator.getEmail()));

        assertNotNull(operator.getPassword());
        assertTrue(SCryptUtil.check(standardObjectMapper.treeToValue(node.get("password"), String.class), operator.getPassword()));
    }

    @Override
    public void testDeserializationWithMissingFields() throws Exception {
        String[] fields = new String[] {
                "email",
                "password"
        };

        ObjectNode node = generateAPIOperatorJsonNode();
        deserializationWithMissingFields(fields, node, Operator.class);
    }
}
