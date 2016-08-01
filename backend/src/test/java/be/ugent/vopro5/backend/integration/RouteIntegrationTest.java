package be.ugent.vopro5.backend.integration;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints;
import be.ugent.vopro5.backend.util.ObjectComparison;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.*;
import static be.ugent.vopro5.backend.util.ObjectComparison.assertUpdatedGenericEventIsValid;
import static be.ugent.vopro5.backend.util.ObjectComparison.assertUpdatedRouteIsValid;
import static be.ugent.vopro5.backend.util.ObjectComparison.assertUpdatedTravelIsValid;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by evert on 4/1/16.
 */
public class RouteIntegrationTest extends IntegrationTest {

    @Test
    public void createRouteTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);

        JsonNode createdTravel = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        generateAPITravelJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );

        JsonNode createdRoute = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                        generateAPIRouteJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );

        JsonNode getRoute = toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getRouteEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText(),createdRoute.get("id").asText()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()
        );
        assertEquals(createdRoute, getRoute);
    }

    @Test
    public void deleteRouteTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);

        JsonNode createdTravel = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        generateAPITravelJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );

        JsonNode createdRoute = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                        generateAPIRouteJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );

        performRequest(
                HttpMethod.DELETE,
                getRouteEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText(),createdRoute.get("id").asText()),
                null,
                null,
                userAccessDetails.getAccessToken()
        ).andExpect(status().isNoContent());

        performRequest(
                HttpMethod.GET,
                getRouteEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText(),createdRoute.get("id").asText()),
                null,
                null,
                userAccessDetails.getAccessToken()
        ).andExpect(status().isNotFound());

        assertEquals(0,toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size()
        );
    }

    @Test
    public void updateRouteTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);

        JsonNode createdTravel = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        generateAPITravelJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );

        JsonNode createdRoute = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                        generateAPIRouteJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );

        ObjectNode routeNode = generateAPIRouteJsonNode();

        JsonNode updatedRoute = toJsonNode(performRequest(
                HttpMethod.PUT,
                getRouteEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText(),createdRoute.get("id").asText()),
                routeNode.toString(),
                null,
                userAccessDetails.getAccessToken()).andExpect(status().isOk()).andReturn()
        );

        assertUpdatedRouteIsValid(updatedRoute,mapper.readTree(routeNode.toString()));
    }

    @Test
    public void viewAllRoutesTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);

        JsonNode createdTravel = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        generateAPITravelJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );

        int numRoutes = random.nextInt(15) + 5;
        for (int i = 0; i < numRoutes; i++) {
            performRequest(
                    HttpMethod.POST,
                    getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                    generateAPIRouteJsonNode().toString(),
                    null,
                    userAccessDetails.getAccessToken()
            ).andExpect(status().isCreated());
        }

        assertEquals(numRoutes, toJsonNode(performRequest(
                HttpMethod.GET,
                getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                null,
                null,
                userAccessDetails.getAccessToken()
        ).andExpect(status().isOk()).andReturn()).size());
    }


    @Test
    public void addRoutesToTravelMultithreadedTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
        JsonNode createdTravel = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        generateAPITravelJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );


        int numRoutes = random.nextInt(13) + 3;
        final boolean[] exceptions = {false};
        List<Thread> creatorThreads = new ArrayList<>();
        List<Thread> updaterThreads = new ArrayList<>();
        for (int i = 0; i < numRoutes; i++) {
            creatorThreads.add(new Thread(() -> {
                try {
                    performRequest(
                            HttpMethod.POST,
                            getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                            generateAPIRouteJsonNode().toString(),
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isCreated());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            // this should not interfere with overall result!
            updaterThreads.add(new Thread(() -> {
                try {
                    JsonNode other = generateAPITravelJsonNode();
                    JsonNode travel = toJsonNode(
                            performRequest(
                                    HttpMethod.GET,
                                    getTravelEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                                    null,
                                    null,
                                    userAccessDetails.getAccessToken()
                            ).andExpect(status().isOk()).andReturn()
                    );
                    ((ObjectNode) travel).set("name", other.get("name"));
                    performRequest(
                            HttpMethod.PUT,
                            getTravelEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                            other.toString(),
                            null,
                            userAccessDetails.getAccessToken()).andExpect(status().isOk());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            creatorThreads.get(i).start();
            updaterThreads.get(i).start();
        }

        List<Thread> threads = new ArrayList<>(creatorThreads);
        threads.addAll(updaterThreads);

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(exceptions[0]);

        assertEquals(numRoutes, toJsonNode(performRequest(
                HttpMethod.GET,
                getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                null,
                null,
                userAccessDetails.getAccessToken()
        ).andExpect(status().isOk()).andReturn()).size());
    }

    @Test
    public void removeRoutesFromTravelMultiThreadedTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
        JsonNode createdTravel = toJsonNode(
                performRequest(
                        HttpMethod.POST,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        generateAPITravelJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()
        );

        int numRoutes = random.nextInt(13) + 3;
        final boolean[] exceptions = {false};
        List<JsonNode> routes = new ArrayList<>();
        for (int i = 0; i < numRoutes; i++) {
            try {
                routes.add(toJsonNode(performRequest(
                        HttpMethod.POST,
                        getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                        generateAPIRouteJsonNode().toString(),
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isCreated()).andReturn()));
            } catch (Exception e) {
                exceptions[0] = true;
            }
        }

        assertFalse(exceptions[0]);
        assertEquals(numRoutes, toJsonNode(performRequest(
                HttpMethod.GET,
                getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                null,
                null,
                userAccessDetails.getAccessToken()
        ).andExpect(status().isOk()).andReturn()).size());

        List<Thread> removerThreads = new ArrayList<>();
        List<Thread> updaterThreads = new ArrayList<>();
        for (int i = 0; i < numRoutes; i++) {
            JsonNode route = routes.get(i);
            removerThreads.add(new Thread(() -> {
                try {
                    String routeID = route.get("id").asText();
                    performRequest(
                            HttpMethod.DELETE,
                            getRouteEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText(),routeID),
                            null,
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isNoContent());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            updaterThreads.add(new Thread(() -> {
                try {
                    JsonNode other = generateAPITravelJsonNode();
                    JsonNode travel = toJsonNode(
                            performRequest(
                                    HttpMethod.GET,
                                    getTravelEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                                    null,
                                    null,
                                    userAccessDetails.getAccessToken()
                            ).andExpect(status().isOk()).andReturn()
                    );
                    ((ObjectNode) travel).set("name", other.get("name"));
                    performRequest(
                            HttpMethod.PUT,
                            getTravelEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                            other.toString(),
                            null,
                            userAccessDetails.getAccessToken()).andExpect(status().isOk());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            removerThreads.get(i).start();
            updaterThreads.get(i).start();
        }

        List<Thread> threads = new ArrayList<>(removerThreads);
        threads.addAll(updaterThreads);

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(exceptions[0]);
        assertEquals(0, toJsonNode(performRequest(
                HttpMethod.GET,
                getRouteIndexEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                null,
                null,
                userAccessDetails.getAccessToken()
        ).andExpect(status().isOk()).andReturn()).size());
    }

    @Test
    public void addAndRemoveRoutesForMultipleUserTravelPairsMultithreadedTest() throws Exception {
        List<UserTravelPair> pairs = new ArrayList<>();
        for (int i = 0; i < random.nextInt(5) + 3; i++) {
            AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
            JsonNode createdTravel = toJsonNode(
                    performRequest(
                            HttpMethod.POST,
                            getTravelIndexEndpoint(userAccessDetails.getUserId()),
                            generateAPITravelJsonNode().toString(),
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isCreated()).andReturn()
            );
            pairs.add(new UserTravelPair(userAccessDetails, createdTravel.get("id").asText()));
        }

        int numRoutes = random.nextInt(13) + 3;
        final boolean[] exceptions = {false};
        List<Thread> creatorThreads = new ArrayList<>();
        List<Thread> updaterThreads = new ArrayList<>();
        for (int i = 0; i < numRoutes; i++) {
            for (UserTravelPair pair : pairs) {
                Thread creatorThread = new Thread(() -> {
                    try {
                        performRequest(
                                HttpMethod.POST,
                                getRouteIndexEndpoint(pair.userAccessDetails.getUserId(),pair.travelId),
                                generateAPIRouteJsonNode().toString(),
                                null,
                                pair.userAccessDetails.getAccessToken()
                        ).andExpect(status().isCreated());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                // this should not interfere with overall result!
                Thread updaterThread = new Thread(() -> {
                    try {
                        JsonNode other = generateAPITravelJsonNode();
                        JsonNode travel = toJsonNode(
                                performRequest(
                                        HttpMethod.GET,
                                        getTravelEntityEndpoint(pair.userAccessDetails.getUserId(),pair.travelId),
                                        null,
                                        null,
                                        pair.userAccessDetails.getAccessToken()
                                ).andExpect(status().isOk()).andReturn()
                        );
                        ((ObjectNode) travel).set("name", other.get("name"));
                        performRequest(
                                HttpMethod.PUT,
                                getTravelEntityEndpoint(pair.userAccessDetails.getUserId(),pair.travelId),
                                other.toString(),
                                null,
                                pair.userAccessDetails.getAccessToken()).andExpect(status().isOk());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                creatorThreads.add(creatorThread);
                updaterThreads.add(updaterThread);
                creatorThread.start();
                updaterThread.start();
            }
        }

        List<Thread> threads = new ArrayList<>(creatorThreads);
        threads.addAll(updaterThreads);

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(exceptions[0]);

        Map<String, JsonNode> nodeMap = new HashMap<>();
        for (UserTravelPair pair : pairs) {
            JsonNode node = toJsonNode(performRequest(
                    HttpMethod.GET,
                    getRouteIndexEndpoint(pair.userAccessDetails.getUserId(),pair.travelId),
                    null,
                    null,
                    pair.userAccessDetails.getAccessToken()
            ).andExpect(status().isOk()).andReturn());
            assertEquals(numRoutes, node.size());
            nodeMap.put(pair.userAccessDetails.getUserId(), node);
        }

        List<Thread> removerThreads = new ArrayList<>();
        updaterThreads.clear();
        for (int i = 0; i < numRoutes; i++) {
            for (UserTravelPair pair : pairs) {
                JsonNode node = nodeMap.get(pair.userAccessDetails.getUserId());
                JsonNode route = node.get(i);
                Thread removerThread = new Thread(() -> {
                    try {
                        String routeID = route.get("id").asText();
                        performRequest(
                                HttpMethod.DELETE,
                                getRouteEntityEndpoint(pair.userAccessDetails.getUserId(),pair.travelId,routeID),
                                null,
                                null,
                                pair.userAccessDetails.getAccessToken()
                        ).andExpect(status().isNoContent());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                // this should not interfere with overall result!
                Thread updaterThread = new Thread(() -> {
                    try {
                        JsonNode other = generateAPITravelJsonNode();
                        JsonNode travel = toJsonNode(
                                performRequest(
                                        HttpMethod.GET,
                                        getTravelEntityEndpoint(pair.userAccessDetails.getUserId(),pair.travelId),
                                        null,
                                        null,
                                        pair.userAccessDetails.getAccessToken()
                                ).andExpect(status().isOk()).andReturn()
                        );
                        ((ObjectNode) travel).set("name", other.get("name"));
                        performRequest(
                                HttpMethod.PUT,
                                getTravelEntityEndpoint(pair.userAccessDetails.getUserId(),pair.travelId),
                                other.toString(),
                                null,
                                pair.userAccessDetails.getAccessToken()).andExpect(status().isOk());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                removerThreads.add(removerThread);
                updaterThreads.add(updaterThread);
                removerThread.start();
                updaterThread.start();
            }
        }

        threads.clear();
        threads.addAll(removerThreads);
        threads.addAll(updaterThreads);

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(exceptions[0]);

        for (UserTravelPair pair : pairs) {
            JsonNode node = toJsonNode(performRequest(
                    HttpMethod.GET,
                    getRouteIndexEndpoint(pair.userAccessDetails.getUserId(),pair.travelId),
                    null,
                    null,
                    pair.userAccessDetails.getAccessToken()
            ).andExpect(status().isOk()).andReturn());
            assertEquals(0, node.size());
        }
    }

    private class UserTravelPair {

        private AccessDetails userAccessDetails;
        private String travelId;

        private UserTravelPair(AccessDetails userAccessDetails, String travelId) {
            this.userAccessDetails = userAccessDetails;
            this.travelId = travelId;
        }
    }
}
