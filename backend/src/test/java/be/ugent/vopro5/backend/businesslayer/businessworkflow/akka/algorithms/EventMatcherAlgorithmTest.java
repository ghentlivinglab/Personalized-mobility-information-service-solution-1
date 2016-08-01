package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.algorithms;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.mock.MockDataAccessContext;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessContext;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.util.ObjectComparison.*;
import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by Michael on 4/12/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/WEB-INF/test-servlet.xml")
public class EventMatcherAlgorithmTest {

    private static final Random random = new Random();
    private TransportationType transportationType = TransportationType.CAR;
    @Autowired
    private DataAccessProvider dataAccessProvider;

    private DataAccessContext dac;

    @Before
    public void setUp() {
        dac = spy(new MockDataAccessContext());
        when(dataAccessProvider.getDataAccessContext()).thenReturn(dac);
    }

    @Test
    public void testMatchEvent() {
        EventMatchingAlgorithm algorithm = new EventMatchingAlgorithm(dataAccessProvider);

        User user = generateUsersForMatching(transportationType).iterator().next();
        user.removePointOfInterest(user.getPointsOfInterest().iterator().next());

        dac.getUserDAO().insert(user);
        Travel travel = user.getTravels().iterator().next();
        dac.getTravelDAO().insert(user.getTravels().iterator().next());
        Route route = travel.getRoutes().iterator().next();
        dac.getRouteDAO().insert(route);


        List<Event> travelEvents = generateEventsForEventMatching(transportationType);

        /*eventOnRoute1, eventOnRoute2, eventOnRoute3, eventOnCorner should be valid*/
        List<Event> validTravelEvents = new ArrayList<>(travelEvents.subList(0, 3));
        validTravelEvents.add(travelEvents.get(6));

        Message.NotificationList travelNotificationList = algorithm.matchEvents(new Message.EventList(travelEvents));
        assertNotNull(travelNotificationList);
        assertEquals(4, travelNotificationList.size());

        for (Message.Notification notification : travelNotificationList) {
            assertRoutesEqual(route, notification.getRoutes().get(0));
            assertUsersEqual(user, notification.getUser());
        }

        Comparator<Event> comparator = (e1, e2) -> e1.getIdentifier().compareTo(e2.getIdentifier());
        List<Event> results = travelNotificationList.stream().
                map(Message.Notification::getEvent).collect(Collectors.toList());

        Collections.sort(validTravelEvents, comparator);
        Collections.sort(results, comparator);

        for (int i = 0; i < results.size(); i++) {
            assertEventsEqual(results.get(i), (validTravelEvents.get(i)));
        }
    }

    @Test
    public void testMatchPointOfInterest() {
        EventMatchingAlgorithm algorithm = new EventMatchingAlgorithm(dataAccessProvider);

        User user = generateUsersForMatching(transportationType).iterator().next();
        user.removeTravel(user.getTravels().iterator().next());

        PointOfInterest pointOfInterest = user.getPointsOfInterest().iterator().next();
        dac.getUserDAO().insert(user);
        dac.getPointOfInterestDAO().insert(pointOfInterest);

        Map<String, Event> eventsMap = generateEventsForPOIMatching();
        Set<Event> poiEvents = new HashSet<>(eventsMap.values());

        Message.NotificationList poiNotificationList = algorithm.matchEvents(new Message.EventList(poiEvents));
        assertNotNull(poiNotificationList);
        assertEquals(1, poiNotificationList.size());

        assertPoiEqual(pointOfInterest, poiNotificationList.get(0).getPointsOfInterest().get(0));
        assertUsersEqual(user, poiNotificationList.get(0).getUser());
        assertEventsEqual(eventsMap.get("inPOI"), poiNotificationList.get(0).getEvent());

    }

    @Test
    public void testMatchPointOfInterestRelevantEventType() {
        EventMatchingAlgorithm algorithm = new EventMatchingAlgorithm(dataAccessProvider);

        User user = generateUsersForMatching(transportationType).iterator().next();

        PointOfInterest pointOfInterest = user.getPointsOfInterest().iterator().next();
        user.removeTravel(user.getTravels().iterator().next());
        dac.getUserDAO().insert(user);
        pointOfInterest.addToNotifyForEventTypes(EventType.OTHER);
        dac.getPointOfInterestDAO().insert(pointOfInterest);

        LatLon pointInPOI = new LatLon(51.024411, 3.710930);

        Event eventInPoiOTHER = new GenericEvent(
                pointInPOI,
                random.nextInt() + "",
                Collections.singleton(generateJam()),
                EventType.OTHER,
                new WazeEventPublisher(),
                Collections.singleton(generateTransportationType())
        );

        Set<Event> poiEvents = new HashSet<>();
        poiEvents.add(eventInPoiOTHER);

        Message.NotificationList poiNotificationList = algorithm.matchEvents(new Message.EventList(poiEvents));
        assertNotNull(poiNotificationList);
        assertEquals(1, poiNotificationList.size());

        assertPoiEqual(pointOfInterest, poiNotificationList.get(0).getPointsOfInterest().get(0));
        assertUsersEqual(user, poiNotificationList.get(0).getUser());
        assertEventsEqual(eventInPoiOTHER, poiNotificationList.get(0).getEvent());

        pointOfInterest.removeFromNotifyFromEventTypes(EventType.OTHER);
        dac.getPointOfInterestDAO().update(pointOfInterest);

        poiNotificationList = algorithm.matchEvents(new Message.EventList(poiEvents));
        assertNotNull(poiNotificationList);
        assertEquals(0, poiNotificationList.size());
    }


}
