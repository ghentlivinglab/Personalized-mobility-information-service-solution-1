package be.ugent.vopro5.backend.businesslayer.applicationfacade.operator;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Operator;
import be.ugent.vopro5.backend.util.ObjectGeneration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.OPERATOR_INDEX_ENDPOINT;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.USER_INDEX_ENDPOINT;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Fields.*;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Fields.PASSWORD_FIELD;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidError;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidOperator;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.JSONValidation.assertIsValidUser;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIUserJsonNode;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by maarten on 13.04.16.
 */
public class OperatorIndexControllerTest extends ControllerTest {

    /**
     * Test the method createUser in a successful scenarios.
     * Check whether necessary fields are present and correct.
     *
     * @throws Exception
     */
    @Test
    public void testCreateOperator() throws Exception {
        MvcResult result = performRequestWithHeader(HttpMethod.POST,
                Endpoints.OPERATOR_INDEX_ENDPOINT,
                generateAPIOperatorJsonNode().toString(),
                null, generateAuthorizationHeader())
                .andExpect(status().isCreated())
                .andReturn();
        assertIsValidOperator(toJsonNode(result));
        verify(operatorDAO, times(1)).insert(any());
    }

    @Test
    public void testCreateOperatorDuplicateEmail() throws Exception {
        JsonNode operator = generateAPIOperatorJsonNode();
        MvcResult result = performRequestWithHeader(HttpMethod.POST,
                Endpoints.OPERATOR_INDEX_ENDPOINT,
                operator.toString(),
                null, generateAuthorizationHeader())
                .andExpect(status().isCreated())
                .andReturn();
        assertIsValidOperator(toJsonNode(result));
        verify(operatorDAO, times(1)).insert(any());

        ObjectNode operator2 = generateAPIOperatorJsonNode();
        operator2.put(EMAIL_FIELD,operator.get(EMAIL_FIELD).asText());

        MvcResult result2 = performRequestWithHeader(HttpMethod.POST,
                Endpoints.OPERATOR_INDEX_ENDPOINT,
                operator.toString(),
                null, generateAuthorizationHeader())
                .andExpect(status().isConflict())
                .andReturn();
        verify(operatorDAO, times(1)).insert(any());

        JsonNode node = toJsonNode(result2);
        assertIsValidError(node);

        assertEquals(HttpStatus.CONFLICT.value(),node.get(STATUS_FIELD).asInt());
        assertEquals(1,node.get(FIELDS_FIELD).size());
        assertEquals("email", node.get(FIELDS_FIELD).get(0).asText());
    }

    /**
     * Test the method createOperator when no (or an invalid) password is passed.
     * This should result in a 400 error code.
     *
     * @throws Exception
     */
    @Test
    public void testCreateOperatorNoPassword() throws Exception {
        ObjectNode operator = generateAPIOperatorJsonNode();
        operator.remove(PASSWORD_FIELD);
        // No password given
        performRequest(HttpMethod.POST, OPERATOR_INDEX_ENDPOINT, operator.toString(), null)
                .andExpect(status().isBadRequest());
        verify(operatorDAO, times(0)).insert(any());

        // Empty password field
        operator.put(PASSWORD_FIELD, "");
        performRequest(HttpMethod.POST, OPERATOR_INDEX_ENDPOINT, operator.toString(), null)
                .andExpect(status().isBadRequest());
        verify(operatorDAO, times(0)).insert(any());

        operator.remove(PASSWORD_FIELD);
        operator.set(PASSWORD_FIELD,null);
        performRequest(HttpMethod.POST, OPERATOR_INDEX_ENDPOINT, operator.toString(), null)
                .andExpect(status().isBadRequest());
        verify(operatorDAO, times(0)).insert(any());
    }

    /**
     * Test the method createOperator when no request body.
     * This should result in a 400 error code.
     *
     * @throws Exception
     */
    @Test
    public void testCreateUserNoBody() throws Exception {
        // Empty JSON
        performRequest(HttpMethod.POST, OPERATOR_INDEX_ENDPOINT, "{}", null)
                .andExpect(status().isBadRequest());
        // No body
        performRequest(HttpMethod.POST, OPERATOR_INDEX_ENDPOINT, "", null)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetOperators() throws Exception {
        List<Operator> operators = new ArrayList<>();
        for(int i= 0; i < 10; i++) {
            Operator operator = generateOperator();
            operators.add(operator);
        }
        when(operatorDAO.listAll()).thenReturn(operators);
        MvcResult mvcResult = performRequest(HttpMethod.GET, Endpoints.OPERATOR_INDEX_ENDPOINT, null, null)
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = toJsonNode(mvcResult);
        assertTrue(node.isArray());
        ArrayNode arrayNode = (ArrayNode) node;
        assertEquals(operators.size(), arrayNode.size());

        for (JsonNode operatorNode : arrayNode) {
            assertIsValidOperator(operatorNode);
        }
    }
}
