package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIUserJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.junit.Assert.*;

/**
 * Created by maarten on 31.03.16.
 */
public class APIUserMixinTest extends APIMixinTest<User> {

    @Override
    public void testSerialization() throws Exception {
        User user = generateUser();
        ObjectNode node = testSerializationIdable(user);

        assertNotNull(node.get("validated"));
        assertEquals(node.get("validated").get("email").asBoolean(),user.getEmailisValidated());

        assertNotNull(node.get("mute_notifications"));
        assertEquals(node.get("mute_notifications").asBoolean(),user.getMuteNotifications());

        assertNotNull(node.get("email"));
        assertEquals(standardObjectMapper.treeToValue(node.get("email"),String.class),user.getEmail());

        assertNotNull(node.get("cell_number"));
        assertEquals(standardObjectMapper.treeToValue(node.get("cell_number"),String.class),user.getCellNumber());

        assertNotNull(node.get("id"));
        assertEquals(standardObjectMapper.treeToValue(node.get("id"),String.class),user.getIdentifier().toString());
    }

    @Override
    public void testDeserialization() throws Exception {
        ObjectNode node = generateAPIUserJsonNode(true);
        User user = objectMapper.treeToValue(node,User.class);

        assertNotNull(user.getEmail());
        assertEquals(standardObjectMapper.treeToValue(node.get("email"),String.class),user.getEmail());

        //can be null actually, depends on generator
        assertNotNull(user.getCellNumber());
        assertEquals(standardObjectMapper.treeToValue(node.get("cell_number"),String.class),user.getCellNumber());

        assertNotNull(user.getMuteNotifications());
        assertEquals(node.get("mute_notifications").asBoolean(),user.getMuteNotifications());

        assertNotNull(user.getPassword());
        assertTrue(SCryptUtil.check(standardObjectMapper.treeToValue(node.get("password"), String.class), user.getPassword()));
    }

    @Override
    public void testDeserializationWithMissingFields() throws Exception {
        String[] fields = new String[] {
                "email",
                "cell_number",
                "mute_notifications",
                "password",
                "validated",
        };

        ObjectNode node = generateAPIUserJsonNode(true);
        deserializationWithMissingFields(fields, node, User.class);
    }
}
