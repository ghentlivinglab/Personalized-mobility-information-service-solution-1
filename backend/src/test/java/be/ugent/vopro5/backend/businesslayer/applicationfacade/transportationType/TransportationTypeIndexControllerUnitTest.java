package be.ugent.vopro5.backend.businesslayer.applicationfacade.transportationType;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.ControllerTest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.TRANSPORTATIONTYPE_INDEX_ENDPOINT;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by maarten on 30.03.16.
 */
public class TransportationTypeIndexControllerUnitTest extends ControllerTest {

    @Test
    public void getTransportationTypes() throws Exception{
        MvcResult mvcResult = performRequest(
                HttpMethod.GET,
                TRANSPORTATIONTYPE_INDEX_ENDPOINT,
                null,
                null
        ).andExpect(status().isOk()).andReturn();

        JsonNode node = toJsonNode(mvcResult);

        assertTrue(node.isArray());
    }

}
