package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.AkkaTest;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.FacebookType;
import org.junit.Test;

import java.util.ArrayList;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateGenericEvent;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;


public class FacebookNotifierTest extends AkkaTest {

    @Test
    public void onReceiveUnhandled() throws Exception {
        FacebookClientProvider provider = mock(FacebookClientProvider.class);

        final Props props = FacebookNotifier.props(provider);
        final TestActorRef<FacebookNotifier> subject = TestActorRef.create(system, props);

        subject.tell(new Object(), ActorRef.noSender());

        verify(provider, never()).getFacebookClient();
    }

    @Test
    public void onReceiveNoFacebookID() throws Exception {
        FacebookClientProvider provider = mock(FacebookClientProvider.class);

        User user = generateUser();
        Event event = generateGenericEvent();

        final Props props = FacebookNotifier.props(provider);
        final TestActorRef<FacebookNotifier> subject = TestActorRef.create(system, props);

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, new ArrayList<>(), new ArrayList<>()));

        subject.tell(list, ActorRef.noSender());

        verify(provider, never()).getFacebookClient();
    }

    @Test
    public void onReceiveOAuthException() throws Exception {
        FacebookClientProvider provider = mock(FacebookClientProvider.class);
        FacebookClient client = mock(FacebookClient.class);

        User user = generateUser();
        Event event = generateGenericEvent();

        NotificationMedium notifactionMedium = new NotificationMedium(NotificationMedium.NotificationMediumType.FACEBOOK, "value", true);
        user.addToNotificationMedia(notifactionMedium);

        final Props props = FacebookNotifier.props(provider);
        final TestActorRef<FacebookNotifier> subject = TestActorRef.create(system, props);

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, new ArrayList<>(), new ArrayList<>()));

        when(provider.getFacebookClient()).thenReturn(client);
        when(client.publish(eq("value/notifications"), eq(FacebookType.class), any(Parameter.class))).thenThrow(mock(FacebookOAuthException.class));

        subject.tell(list, ActorRef.noSender());

        verify(provider, times(1)).getFacebookClient();
        assertFalse(user.getNotificationMedia().contains(notifactionMedium));
    }

    @Test
    public void onReceive() throws Exception {
        FacebookClientProvider provider = mock(FacebookClientProvider.class);
        FacebookClient client = mock(FacebookClient.class);

        User user = generateUser();
        Event event = generateGenericEvent();

        user.addToNotificationMedia(new NotificationMedium(NotificationMedium.NotificationMediumType.FACEBOOK, "value", true));

        final Props props = FacebookNotifier.props(provider);
        final TestActorRef<FacebookNotifier> subject = TestActorRef.create(system, props);

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, new ArrayList<>(), new ArrayList<>()));

        when(provider.getFacebookClient()).thenReturn(client);

        subject.tell(list, ActorRef.noSender());

        verify(provider, times(1)).getFacebookClient();
        verify(client, times(1)).publish(eq("value/notifications"), eq(FacebookType.class), any(Parameter.class));
    }


}