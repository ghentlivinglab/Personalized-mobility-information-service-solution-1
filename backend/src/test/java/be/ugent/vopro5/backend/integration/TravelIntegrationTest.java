package be.ugent.vopro5.backend.integration;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.getTravelIndexEndpoint;
import static be.ugent.vopro5.backend.businesslayer.applicationfacade.Endpoints.*;
import static be.ugent.vopro5.backend.util.ObjectComparison.assertUpdatedTravelIsValid;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by evert on 4/1/16.
 */
public class TravelIntegrationTest extends IntegrationTest {

    @Test
    public void createTravelTest() throws Exception {
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
        JsonNode getTravel = toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getTravelEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()
        );
        assertEquals(createdTravel,getTravel);
    }

    @Test
    public void viewAllTravelsTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
        int numTravels = random.nextInt(15) + 5;
        for (int i = 0; i < numTravels; i++) {
            performRequest(
                    HttpMethod.POST,
                    getTravelIndexEndpoint(userAccessDetails.getUserId()),
                    generateAPITravelJsonNode().toString(),
                    null,
                    userAccessDetails.getAccessToken()
            ).andExpect(status().isCreated());
        }
        assertEquals(numTravels, toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size());
    }


    @Test
    public void updateTravelTest() throws Exception {
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
        ObjectNode travelNode = generateAPITravelJsonNode();

        JsonNode updatedTravel = toJsonNode(performRequest(
                HttpMethod.PUT,
                getTravelEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                travelNode.toString(),
                null,
                userAccessDetails.getAccessToken()).andExpect(status().isOk()).andReturn()
        );

        assertUpdatedTravelIsValid(updatedTravel,mapper.readTree(travelNode.toString()));
    }

    @Test
    public void deleteTravelTest() throws Exception {
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

        performRequest(
                HttpMethod.DELETE,
                getTravelEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                null,
                null,
                userAccessDetails.getAccessToken()
        ).andExpect(status().isNoContent());

        performRequest(
                HttpMethod.GET,
                getTravelEntityEndpoint(userAccessDetails.getUserId(),createdTravel.get("id").asText()),
                null,
                null,
                userAccessDetails.getAccessToken()
        ).andExpect(status().isNotFound());

        assertEquals(0,toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size()
        );
    }

    @Test
    public void addTravelToUserMultithreadedTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
        String pin = dataAccessProvider.getDataAccessContext().getUserDAO().find(userAccessDetails.getUserId()).getEmailVerification();

        int numTravels = random.nextInt(13) + 3;
        final boolean[] exceptions = {false};
        List<Thread> creatorThreads = new ArrayList<>();
        List<Thread> updaterThreads = new ArrayList<>();
        List<Thread> verifierThreads = new ArrayList<>();
        List<Thread> notificationMediumThreads = new ArrayList<>();
        for (int i = 0; i < numTravels; i++) {
            creatorThreads.add(new Thread(() -> {
                try {
                    performRequest(
                            HttpMethod.POST,
                            getTravelIndexEndpoint(userAccessDetails.getUserId()),
                            generateAPITravelJsonNode().toString(),
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isCreated());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            // this should not interfere with the overall result!
            updaterThreads.add(new Thread(() -> {
                try {
                    JsonNode other = generateAPIUserJsonNode(true);
                    ((ObjectNode) other).set("email", userAccessDetails.getUser().get("email"));
                    performRequest(
                            HttpMethod.PUT,
                            getUserEntityEndpoint(userAccessDetails.getUserId()),
                            other.toString(),
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isOk());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            // this should not interfere with the overall result!
            verifierThreads.add(new Thread(() -> {
                try {
                    ObjectNode verifyInputBody = JsonNodeFactory.instance.objectNode();
                    verifyInputBody.put("email_verification_pin", pin);
                    verifyInputBody.put("cell_number_verification_pin", "");
                    performRequest(
                            HttpMethod.PUT,
                            getVerifyEndpoint(userAccessDetails.getUserId()),
                            verifyInputBody.toString(),
                            null,
                            adminAccessToken
                    ).andExpect(status().isOk());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            // this should not interfere with the overall result!
            notificationMediumThreads.add(new Thread(() -> {
                try {
                    ObjectNode appBody = JsonNodeFactory.instance.objectNode();
                    appBody.put("app_id", random.nextInt());
                    performRequest(
                            HttpMethod.POST,
                            getNotificationMediumEndpoint(userAccessDetails.getUserId(), "app"),
                            appBody.toString(),
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isOk());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            creatorThreads.get(i).start();
            updaterThreads.get(i).start();
            verifierThreads.get(i).start();
            notificationMediumThreads.get(i).start();
        }

        List<Thread> threads = new ArrayList<>(creatorThreads);
        threads.addAll(updaterThreads);
        threads.addAll(verifierThreads);
        threads.addAll(notificationMediumThreads);

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(exceptions[0]);

        assertEquals(numTravels,toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size()
        );
    }

    @Test
    public void removeTravelFromUserMultiThreadedTest() throws Exception {
        AccessDetails userAccessDetails = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
        String pin = dataAccessProvider.getDataAccessContext().getUserDAO().find(userAccessDetails.getUserId()).getEmailVerification();

        int numTravels = random.nextInt(13) + 3;
        final boolean[] exceptions = {false};
        List<JsonNode> travels = new ArrayList<>();
        for (int i = 0; i < numTravels; i++) {
            try {
                travels.add(toJsonNode(
                                performRequest(
                                        HttpMethod.POST,
                                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                                        generateAPITravelJsonNode().toString(),
                                        null,
                                        userAccessDetails.getAccessToken()
                                ).andExpect(status().isCreated()).andReturn()
                        )
                    );
            } catch (Exception e) {
                exceptions[0] = true;
            }
        }

        assertFalse(exceptions[0]);
        assertEquals(numTravels,toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size()
        );


        List<Thread> removerThreads = new ArrayList<>();
        List<Thread> updaterThreads = new ArrayList<>();
        List<Thread> verifierThreads = new ArrayList<>();
        List<Thread> notificationMediumThreads = new ArrayList<>();
        for (int i = 0; i < numTravels; i++) {
            JsonNode travel = travels.get(i);
            removerThreads.add(new Thread(() -> {
                try {
                    String travelID = travel.get("id").asText();
                    performRequest(
                            HttpMethod.DELETE,
                            getTravelEntityEndpoint(userAccessDetails.getUserId(),travelID),
                            null,
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isNoContent());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            // this should not interfere with the overall result!
            updaterThreads.add(new Thread(() -> {
                try {
                    JsonNode other = generateAPIUserJsonNode(true);
                    ((ObjectNode) other).set("email", userAccessDetails.getUser().get("email"));
                    performRequest(
                            HttpMethod.PUT,
                            getUserEntityEndpoint(userAccessDetails.getUserId()),
                            other.toString(),
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isOk());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            // this should not interfere with the overall result!
            verifierThreads.add(new Thread(() -> {
                try {
                    ObjectNode verifyInputBody = JsonNodeFactory.instance.objectNode();
                    verifyInputBody.put("email_verification_pin", pin);
                    verifyInputBody.put("cell_number_verification_pin", "");
                    performRequest(
                            HttpMethod.PUT,
                            getVerifyEndpoint(userAccessDetails.getUserId()),
                            verifyInputBody.toString(),
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isOk());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            // this should not interfere with the overall result!
            notificationMediumThreads.add(new Thread(() -> {
                try {
                    ObjectNode appBody = JsonNodeFactory.instance.objectNode();
                    appBody.put("app_id", random.nextInt());
                    performRequest(
                            HttpMethod.POST,
                            getNotificationMediumEndpoint(userAccessDetails.getUserId(), "app"),
                            appBody.toString(),
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isOk());
                } catch (Exception e) {
                    exceptions[0] = true;
                }
            }));

            removerThreads.get(i).start();
            updaterThreads.get(i).start();
            verifierThreads.get(i).start();
            notificationMediumThreads.get(i).start();
        }

        List<Thread> threads = new ArrayList<>(removerThreads);
        threads.addAll(updaterThreads);
        threads.addAll(verifierThreads);
        threads.addAll(notificationMediumThreads);

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(exceptions[0]);
        assertEquals(0,toJsonNode(
                performRequest(
                        HttpMethod.GET,
                        getTravelIndexEndpoint(userAccessDetails.getUserId()),
                        null,
                        null,
                        userAccessDetails.getAccessToken()
                ).andExpect(status().isOk()).andReturn()).size()
        );
    }

    @Test
    public void addAndRemoveTravelsForMultipleUsersMultithreadedTest() throws Exception {
        List<AccessDetails> users = new ArrayList<>();
        for (int i = 0; i < random.nextInt(5) + 3; i++) {
            AccessDetails user = createPersonAndGetAccessDetails(generateAPIUserJsonNode(true),USER_INDEX_ENDPOINT,false);
            users.add(user);
        }

        int numTravels = random.nextInt(13) + 3;
        final boolean[] exceptions = {false};
        List<Thread> creatorThreads = new ArrayList<>();
        List<Thread> updaterThreads = new ArrayList<>();
        List<Thread> verifierThreads = new ArrayList<>();
        List<Thread> notificationMediumThreads = new ArrayList<>();
        for (int i = 0; i < numTravels; i++) {
            for (AccessDetails userAccessDetails : users) {
                JsonNode user = userAccessDetails.getUser();
                String userId = user.get("id").asText();
                String pin = dataAccessProvider.getDataAccessContext().getUserDAO().find(userId).getEmailVerification();
                Thread creatorThread = new Thread(() -> {
                    try {
                        performRequest(
                                HttpMethod.POST,
                                getTravelIndexEndpoint(userAccessDetails.getUserId()),
                                generateAPITravelJsonNode().toString(),
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isCreated());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                Thread updaterThread = new Thread(() -> {
                    try {
                        JsonNode other = generateAPIUserJsonNode(true);
                        ((ObjectNode) other).set("email", userAccessDetails.getUser().get("email"));
                        performRequest(
                                HttpMethod.PUT,
                                getUserEntityEndpoint(userAccessDetails.getUserId()),
                                other.toString(),
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isOk());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                // this should not interfere with the overall result!
                Thread verifierThread = new Thread(() -> {
                    try {
                        ObjectNode verifyInputBody = JsonNodeFactory.instance.objectNode();
                        verifyInputBody.put("email_verification_pin", pin);
                        verifyInputBody.put("cell_number_verification_pin", "");
                        performRequest(
                                HttpMethod.PUT,
                                getVerifyEndpoint(userAccessDetails.getUserId()),
                                verifyInputBody.toString(),
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isOk());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                // this should not interfere with the overall result!
                Thread notificationMediumThread = new Thread(() -> {
                    try {
                        ObjectNode appBody = JsonNodeFactory.instance.objectNode();
                        appBody.put("app_id", random.nextInt());
                        performRequest(
                                HttpMethod.POST,
                                getNotificationMediumEndpoint(userAccessDetails.getUserId(), "app"),
                                appBody.toString(),
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isOk());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                creatorThreads.add(creatorThread);
                updaterThreads.add(updaterThread);
                verifierThreads.add(verifierThread);
                notificationMediumThreads.add(notificationMediumThread);
                creatorThread.start();
                updaterThread.start();
                verifierThread.start();
                notificationMediumThread.start();
            }
        }

        List<Thread> threads = new ArrayList<>(creatorThreads);
        threads.addAll(updaterThreads);
        threads.addAll(verifierThreads);
        threads.addAll(notificationMediumThreads);

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(exceptions[0]);

        Map<String, JsonNode> nodeMap = new HashMap<>();
        for (AccessDetails userAccessDetails : users) {
            String userId = userAccessDetails.getUserId();
            JsonNode node = toJsonNode(
                    performRequest(
                            HttpMethod.GET,
                            getTravelIndexEndpoint(userAccessDetails.getUserId()),
                            null,
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isOk()).andReturn());
            assertEquals(numTravels, node.size());
            nodeMap.put(userId, node);
        }

        List<Thread> removerThreads = new ArrayList<>();
        for (int i = 0; i < numTravels; i++) {
            for (AccessDetails userAccessDetails : users) {
                String userId = userAccessDetails.getUserId();
                String pin = dataAccessProvider.getDataAccessContext().getUserDAO().find(userId).getEmailVerification();
                JsonNode node = nodeMap.get(userId);
                JsonNode travel = node.get(i);
                Thread removerThread = new Thread(() -> {
                    try {
                        String travelID = travel.get("id").asText();
                        performRequest(
                                HttpMethod.DELETE,
                                getTravelEntityEndpoint(userId,travelID),
                                null,
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isNoContent());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                Thread updaterThread = new Thread(() -> {
                    try {
                        JsonNode other = generateAPIUserJsonNode(true);
                        ((ObjectNode) other).set("email", userAccessDetails.getUser().get("email"));
                        performRequest(
                                HttpMethod.PUT,
                                getUserEntityEndpoint(userAccessDetails.getUserId()),
                                other.toString(),
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isOk());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                // this should not interfere with the overall result!
                Thread verifierThread = new Thread(() -> {
                    try {
                        ObjectNode verifyInputBody = JsonNodeFactory.instance.objectNode();
                        verifyInputBody.put("email_verification_pin", pin);
                        verifyInputBody.put("cell_number_verification_pin", "");
                        performRequest(
                                HttpMethod.PUT,
                                getVerifyEndpoint(userAccessDetails.getUserId()),
                                verifyInputBody.toString(),
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isOk());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                // this should not interfere with the overall result!
                Thread notificationMediumThread = new Thread(() -> {
                    try {
                        ObjectNode appBody = JsonNodeFactory.instance.objectNode();
                        appBody.put("app_id", random.nextInt());
                        performRequest(
                                HttpMethod.POST,
                                getNotificationMediumEndpoint(userAccessDetails.getUserId(), "app"),
                                appBody.toString(),
                                null,
                                userAccessDetails.getAccessToken()
                        ).andExpect(status().isOk());
                    } catch (Exception e) {
                        exceptions[0] = true;
                    }
                });

                removerThreads.add(removerThread);
                updaterThreads.add(updaterThread);
                verifierThreads.add(verifierThread);
                notificationMediumThreads.add(notificationMediumThread);
                removerThread.start();
                updaterThread.start();
                verifierThread.start();
                notificationMediumThread.start();
            }
        }

        threads.clear();
        threads.addAll(removerThreads);
        threads.addAll(updaterThreads);
        threads.addAll(verifierThreads);
        threads.addAll(notificationMediumThreads);

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(exceptions[0]);

        for (AccessDetails userAccessDetails : users) {
            String userId = userAccessDetails.getUserId();
            assertEquals(0,toJsonNode(
                    performRequest(
                            HttpMethod.GET,
                            getTravelIndexEndpoint(userAccessDetails.getUserId()),
                            null,
                            null,
                            userAccessDetails.getAccessToken()
                    ).andExpect(status().isOk()).andReturn()).size()
            );
        }
    }
}
