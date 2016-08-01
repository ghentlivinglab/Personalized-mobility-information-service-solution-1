package be.ugent.vopro5.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.USER_INDEX_ENDPOINT;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getUserEntityEndpoint;
import static be.ugent.vopro5.backend.util.ObjectComparison.assertUpdatedUserIsValid;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIUserJsonNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by evert on 4/3/16.
 */
public class UserIntegrationTest extends IntegrationTest {

    @Test
    public void createMultipleUsersMultiThreadedTest() throws Exception {
        List<Thread> threads = new ArrayList<>();
        boolean[] exceptions = new boolean[]{false};

        int numUsers = random.nextInt(15) + 6;
        for (int i = 0; i < numUsers; i++) {
            threads.add(new Thread(() -> {
                try {
                    performRequest(HttpMethod.POST, USER_INDEX_ENDPOINT, generateAPIUserJsonNode(true).toString(), null, null)
                            .andExpect(status().isCreated());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));
            threads.get(i).start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(exceptions[0]);
        assertEquals(numUsers,
                toJsonNode(performRequest(HttpMethod.GET,USER_INDEX_ENDPOINT,null,null, adminAccessToken)
                        .andExpect(status().isOk()).andReturn())
                        .size()
        );
    }

    @Test
    public void createUserTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);

        MvcResult mvcGetUserResult = performRequest(HttpMethod.GET, getUserEntityEndpoint(userAccessDetails.getUserId()), null, null, userAccessDetails.getAccessToken())
                .andExpect(status().isOk()).andReturn();

        assertEquals(userAccessDetails.getUser(),toJsonNode(mvcGetUserResult));
    }

    @Test
    public void deleteUserTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
        performRequest(HttpMethod.DELETE,getUserEntityEndpoint(userAccessDetails.getUserId()),null,null,
                userAccessDetails.getAccessToken()).andExpect(status().isNoContent());

        performRequest(HttpMethod.GET,getUserEntityEndpoint(userAccessDetails.getUserId()),null,null, adminAccessToken)
                .andExpect(status().isNotFound());

        assertEquals(0,toJsonNode(performRequest(HttpMethod.GET,USER_INDEX_ENDPOINT,null,null,adminAccessToken)
                        .andExpect(status().isOk()).andReturn()).size()
        );
    }

    @Test
    public void updateUserTest() throws Exception {
        AccessDetails accessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
        JsonNode createdUser = accessDetails.getUser();
        ObjectNode userNode = generateAPIUserJsonNode(true);
        /*
        If the email-address has changed then a new verification email will be sent to that new address. Need to find a
        way to test this without mocking.
         */
        userNode.set("email",createdUser.get("email"));
        JsonNode updatedUser = toJsonNode(performRequest(HttpMethod.PUT,getUserEntityEndpoint(accessDetails.getUserId()),userNode.toString(),null,accessDetails.getAccessToken())
                .andExpect(status().isOk()).andReturn());

        assertUpdatedUserIsValid(updatedUser,mapper.readTree(userNode.toString()));
    }

    @Test
    public void viewAllUsersTest() throws Exception {
        int numUsers = random.nextInt(15) + 6;
        for (int i = 0; i < numUsers; i++) {
            performRequest(HttpMethod.POST, USER_INDEX_ENDPOINT, generateAPIUserJsonNode(true).toString(), null, null)
                    .andExpect(status().isCreated());
        }

        //we need admin privileges to view all users
        assertEquals(numUsers,
                toJsonNode(performRequest(HttpMethod.GET,USER_INDEX_ENDPOINT,null,null, adminAccessToken)
                .andExpect(status().isOk()).andReturn())
                .size()
        );
    }

}
