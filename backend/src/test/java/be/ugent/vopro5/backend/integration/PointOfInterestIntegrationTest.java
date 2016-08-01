package be.ugent.vopro5.backend.integration;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.USER_INDEX_ENDPOINT;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getPOIEntityEndpoint;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getPOIIndexEndpoint;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIPOIJsonNode;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateAPIUserJsonNode;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by maarten on 24.04.16.
 */
public class PointOfInterestIntegrationTest extends IntegrationTest {

    @Test
    public void createPointOfInterestTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);

        JsonNode createdPOI = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getPOIIndexEndpoint(userAccessDetails.getUserId()),
                        generateAPIPOIJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                        ).andExpect(status().isCreated()).andReturn()
        );
        JsonNode getPOI = toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getPOIEntityEndpoint(userAccessDetails.getUserId(),createdPOI.get("id").asText()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()
        );
        assertEquals(createdPOI,getPOI);
    }

    @Test
    public void deletePointOfInterestTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);

        JsonNode createdPOI = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getPOIIndexEndpoint(userAccessDetails.getUserId()),
                        generateAPIPOIJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );

        performRequest(
                HttpMethod.DELETE,
                getPOIEntityEndpoint(userAccessDetails.getUserId(),createdPOI.get("id").asText()),
                null,
                null,
                userAccessDetails.getAccessToken()
        ).andExpect(status().isNoContent());

        performRequest(
                HttpMethod.GET,
                getPOIEntityEndpoint(userAccessDetails.getUserId(),createdPOI.get("id").asText()),
                null,
                null,
                userAccessDetails.getAccessToken()
        ).andExpect(status().isNotFound());

        assertEquals(0,toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getPOIIndexEndpoint(userAccessDetails.getUserId()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size()
        );
    }

    @Test
    public void updatePointOfInterestTest() throws  Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);

        JsonNode createdPOI = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getPOIIndexEndpoint(userAccessDetails.getUserId()),
                        generateAPIPOIJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );
        ObjectNode poiNode = generateAPIPOIJsonNode();

        JsonNode updatedPOI = toJsonNode(performRequest(
                HttpMethod.PUT,
                getPOIEntityEndpoint(userAccessDetails.getUserId(),createdPOI.get("id").asText()),
                poiNode.toString(),
                null,
                userAccessDetails.getAccessToken()).andExpect(status().isOk()).andReturn()
        );

        assertEquals(updatedPOI.get("active").asBoolean(),poiNode.get("active").asBoolean());
        assertEquals(updatedPOI.get("name"),poiNode.get("name"));
        assertEquals(updatedPOI.get("radius").asInt(),poiNode.get("radius").asInt());
        assertEquals(updatedPOI.get("notify"),poiNode.get("notify"));
        assertEquals(updatedPOI.get("notify_for_event_types").size(),poiNode.get("notify_for_event_types").size());
    }


    @Test
    public void viewAllPointsOfInterestTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
        int numPointsOfInterest = random.nextInt(15) + 5;
        for (int i = 0; i < numPointsOfInterest; i++) {
            performRequest(
                    HttpMethod.POST,
                    getPOIIndexEndpoint(userAccessDetails.getUserId()),
                    generateAPIPOIJsonNode().toString(),
                    null,
                    userAccessDetails.getAccessToken()
            ).andExpect(status().isCreated());
        }
        assertEquals(numPointsOfInterest, toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getPOIIndexEndpoint(userAccessDetails.getUserId()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size());
    }

    @Test
    public void addPointsOfInterestToUserMultithreadedTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);

        int numPointsOfInterest = random.nextInt(13) + 3;
        final boolean[] exceptions = {false};
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numPointsOfInterest; i++) {
            threads.add(new Thread(() -> {
                try {
                    performRequest(
                            HttpMethod.POST,
                            getPOIIndexEndpoint(userAccessDetails.getUserId()),
                            generateAPIPOIJsonNode().toString(),
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isCreated());
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
        assertEquals(numPointsOfInterest, toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getPOIIndexEndpoint(userAccessDetails.getUserId()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size());
    }

    @Test
    public void removePointsOfInterestMultiThreadedTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);

        int numPointsOfInterest = random.nextInt(13) + 3;
        final boolean[] exceptions = {false};
        List<JsonNode> pointsOfInterest = new ArrayList<>();
        for (int i = 0; i < numPointsOfInterest; i++) {
            try {
                pointsOfInterest.add(toJsonNode(
                        performRequest(
                                HttpMethod.POST,
                                getPOIIndexEndpoint(userAccessDetails.getUserId()),
                                generateAPIPOIJsonNode().toString(),
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isCreated()).andReturn()
                ));
            } catch (Exception e) {
                exceptions[0] = true;
            }
        }

        assertFalse(exceptions[0]);
        assertEquals(numPointsOfInterest, toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getPOIIndexEndpoint(userAccessDetails.getUserId()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size());

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numPointsOfInterest; i++) {
            JsonNode poi = pointsOfInterest.get(i);
            threads.add(new Thread(() -> {
                try {
                    String poiID = poi.get("id").asText();
                    performRequest(
                            HttpMethod.DELETE,
                            getPOIEntityEndpoint(userAccessDetails.getUserId(),poiID),
                            null,
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isNoContent());
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
        assertEquals(0, toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getPOIIndexEndpoint(userAccessDetails.getUserId()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size());
    }

    @Test
    public void addAndRemovePointsOfInterestForMultipleUsersMultithreadedTest() throws Exception {
        List<AccessDetails> users = new ArrayList<>();
        for (int i = 0; i < random.nextInt(5) + 3; i++) {
            AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
            users.add(userAccessDetails);
        }

        int numPointsOfInterest = random.nextInt(13) + 3;
        final boolean[] exceptions = {false};
        List<Thread> creatorThreads = new ArrayList<>();
        for (int i = 0; i < numPointsOfInterest; i++) {
            for (AccessDetails userAccessDetails : users) {
                Thread creatorThread = new Thread(() -> {
                    try {
                        performRequest(
                                HttpMethod.POST,
                                getPOIIndexEndpoint(userAccessDetails.getUserId()),
                                generateAPIPOIJsonNode().toString(),
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isCreated());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });
                creatorThreads.add(creatorThread);
                creatorThread.start();
            }
        }
        for (Thread thread : creatorThreads) {
            thread.join();
        }

        assertFalse(exceptions[0]);

        Map<String, JsonNode> nodeMap = new HashMap<>();
        for (AccessDetails userAccessDetails : users) {
            JsonNode node = toJsonNode(
                    performRequest(
                            HttpMethod.GET,
                            getPOIIndexEndpoint(userAccessDetails.getUserId()),
                            null,
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isOk()).andReturn());
            assertEquals(numPointsOfInterest, node.size());
            nodeMap.put(userAccessDetails.getUserId(), node);
        }

        List<Thread> removerThreads = new ArrayList<>();
        for (int i = 0; i < numPointsOfInterest; i++) {
            for (AccessDetails userAccessDetails : users) {
                JsonNode node = nodeMap.get(userAccessDetails.getUserId());
                JsonNode poi = node.get(i);
                Thread removerThread = new Thread(() -> {
                    try {
                        String poiID = poi.get("id").asText();
                        performRequest(
                                HttpMethod.DELETE,
                                getPOIEntityEndpoint(userAccessDetails.getUserId(),poiID),
                                null,
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isNoContent());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });
                removerThreads.add(removerThread);
                removerThread.start();
            }
        }
        for (Thread thread : removerThreads) {
            thread.join();
        }

        for (AccessDetails userAccessDetails : users) {
            assertEquals(0, toJsonNode(
                    performRequest(
                            HttpMethod.GET,
                            getPOIIndexEndpoint(userAccessDetails.getUserId()),
                            null,
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isOk()).andReturn()).size());
        }
    }
}
