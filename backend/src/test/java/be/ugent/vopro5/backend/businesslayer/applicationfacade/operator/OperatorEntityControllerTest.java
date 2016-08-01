package be.ugent.vopro5.backend.businesslayer.applicationfacade.operator;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.OPERATOR_INDEX_ENDPOINT;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Fields.EMAIL_FIELD;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidError;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidOperator;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by maarten on 13.04.16.
 */
public class OperatorEntityControllerTest extends ControllerTest {

    private static final String IMAGINARY_OPERATOR = "imaginary_operator";

    private String getOperatorEntityEndpoint(String id) {
        return OPERATOR_INDEX_ENDPOINT + id + "/";
    }

    @Test
    public void testGetOperatorNotExist() throws Exception {
        when(operatorDAO.find(IMAGINARY_OPERATOR)).thenReturn(null);
        // This user does not exist
        JsonNode node = performGetRequestNotFound(getOperatorEntityEndpoint(IMAGINARY_OPERATOR));
        assertIsValidError(node);
    }

    /**
     * Test the method getUser in both successful and unsuccessful scenarios.
     * In successful case check whether necessary fields are present and correct.
     *
     * @throws Exception
     */
    @Test
    public void testGetOperator() throws Exception {
        Operator operator = generateOperator();
        when(operatorDAO.find(operator.getIdentifier().toString())).thenReturn(operator);

        JsonNode node = performGetRequestNoContent(getOperatorEntityEndpoint(
                operator.getIdentifier().toString())
        );
        assertIsValidOperator(node);
    }

    /**
     * Test the method deleteUser in both successful and unsuccessful scenarios.
     *
     * @throws Exception
     */
    @Test
    public void testDeleteOperator() throws Exception {
        Operator operator = generateOperator();
        when(operatorDAO.find(operator.getIdentifier().toString())).thenReturn(operator).thenReturn(null);

        // Delete it
        performDeleteNoContent(getOperatorEntityEndpoint(operator.getIdentifier().toString()));
        // Can't exist anymore now
        performDeleteNotFound(
                getOperatorEntityEndpoint(operator.getIdentifier().toString())
        );
    }

    @Test
    public void testOperatorUpdateNotExist() throws Exception {
        when(operatorDAO.find(IMAGINARY_OPERATOR)).thenReturn(null);
        // This operator does not exist
        JsonNode node = toJsonNode(performRequest(
                HttpMethod.PUT,
                getOperatorEntityEndpoint(IMAGINARY_OPERATOR),
                generateAPIOperatorJsonNode().toString(), null)
                .andExpect(status().isNotFound())
                .andReturn());
        assertIsValidError(node);
    }

    /**
     * Test the method updateUser in both successful, unsuccessful, and conflicting scenarios.
     *
     * @throws Exception
     */

    @Test
    public void testUpdateOperator() throws Exception {
        Operator oper = generateOperator();
        when(operatorDAO.find(oper.getIdentifier().toString())).thenReturn(oper);
        doNothing().when(operatorDAO).update(any());

        // Update the operator with a new email
        JsonNode node = toJsonNode(
                performRequest(HttpMethod.PUT,
                        getOperatorEntityEndpoint(oper.getIdentifier().toString()),
                        generateAPIOperatorJsonNode().toString(),
                        null).andExpect(status().isOk()).andReturn()
        );

        assertIsValidOperator(node);
    }

    @Test
    public void testUpdateUserDuplicateEmail() throws Exception {
        Operator operator = generateOperator();
        when(operatorDAO.findByEmail(operator.getEmail())).thenReturn(operator);

        // Update the user with an email already in use by another user
        ObjectNode userJsonNode = generateAPIUserJsonNode(true);
        userJsonNode.put(EMAIL_FIELD, operator.getEmail());
        Operator updatingOperator = generateOperator();
        when(operatorDAO.find(updatingOperator.getIdentifier().toString())).thenReturn(updatingOperator);

        JsonNode node = toJsonNode(performRequest(
                HttpMethod.PUT,
                getOperatorEntityEndpoint(updatingOperator.getIdentifier().toString()),
                userJsonNode.toString(),
                null)
                .andExpect(status().isConflict())
                .andReturn());
        assertIsValidError(node);
    }
}
