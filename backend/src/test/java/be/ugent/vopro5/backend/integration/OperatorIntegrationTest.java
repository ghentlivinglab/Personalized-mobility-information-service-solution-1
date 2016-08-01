package be.ugent.vopro5.backend.integration;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.*;
import static be.ugent.vopro5.backend.util.ObjectComparison.assertUpdatedUserIsValid;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by maarten on 28.04.16.
 */
public class OperatorIntegrationTest extends IntegrationTest {

    @Test
    public void createOperatorTest() throws Exception {
         AccessDetails operatorAccessDetails = createPersonAndGetAccessDetails(generateAPIOperatorJsonNode(),OPERATOR_INDEX_ENDPOINT,true);
         MvcResult mvcGetOperatorResult = performRequest(HttpMethod.GET, getOperatorEntityEndpoint(operatorAccessDetails.getUserId()), null, null, adminAccessToken)
                .andExpect(status().isOk()).andReturn();

         assertEquals(operatorAccessDetails.getUser(),toJsonNode(mvcGetOperatorResult));
    }

    @Test
    public void deleteOperatorTest() throws Exception {
        AccessDetails operatorAccessDetails = createPersonAndGetAccessDetails(generateAPIOperatorJsonNode(),OPERATOR_INDEX_ENDPOINT,true);
        performRequest(HttpMethod.DELETE,getOperatorEntityEndpoint(operatorAccessDetails.getUserId()),null,null,
                adminAccessToken).andExpect(status().isNoContent());

        performRequest(HttpMethod.GET,getOperatorEntityEndpoint(operatorAccessDetails.getUserId()),null,null, adminAccessToken)
                .andExpect(status().isNotFound());

        assertEquals(0,toJsonNode(performRequest(HttpMethod.GET,OPERATOR_INDEX_ENDPOINT,null,null,adminAccessToken)
                        .andExpect(status().isOk()).andReturn()).size()
        );
    }

    @Test
    public void updateOperatorTest() throws Exception {
        AccessDetails accessDetails = createPersonAndGetAccessDetails(generateAPIOperatorJsonNode(),OPERATOR_INDEX_ENDPOINT,true);
        ObjectNode operatorNode = generateAPIOperatorJsonNode();
        JsonNode updatedOperator = toJsonNode(performRequest(HttpMethod.PUT,getOperatorEntityEndpoint(accessDetails.getUserId()),operatorNode.toString(),null,adminAccessToken)
                .andExpect(status().isOk()).andReturn());
        assertEquals(updatedOperator.get("email"),operatorNode.get("email"));
    }

    @Test
    public void viewAllOperatorsTest() throws Exception {
        int numOperators = random.nextInt(15) + 6;
        for (int i = 0; i < numOperators; i++) {
            performRequest(HttpMethod.POST, OPERATOR_INDEX_ENDPOINT, generateAPIOperatorJsonNode().toString(), null, adminAccessToken)
                    .andExpect(status().isCreated());
        }

        //we need admin privileges to view all users
        assertEquals(numOperators,
                toJsonNode(performRequest(HttpMethod.GET,OPERATOR_INDEX_ENDPOINT,null,null, adminAccessToken)
                        .andExpect(status().isOk()).andReturn())
                        .size()
        );
    }
}
