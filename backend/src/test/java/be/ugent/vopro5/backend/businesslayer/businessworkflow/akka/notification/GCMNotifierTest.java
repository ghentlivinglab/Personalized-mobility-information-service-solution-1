package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.AkkaTest;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import org.junit.Test;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateGenericEvent;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.mockito.Mockito.*;

public class GCMNotifierTest extends AkkaTest {
    @Test
    public void onReceiveUnhandled() throws Exception {
        GCMConnectionProvider provider = mock(GCMConnectionProvider.class);

        final Props props = GCMNotifier.props(provider);
        final TestActorRef<GCMNotifier> subject = TestActorRef.create(system, props);

        subject.tell(new Object(), ActorRef.noSender());

        verify(provider, never()).getConnection();
    }

    @Test
    public void onReceiveNoApp() throws Exception {
        GCMConnectionProvider provider = mock(GCMConnectionProvider.class);

        User user = generateUser();
        Event event = generateGenericEvent();

        final Props props = GCMNotifier.props(provider);
        final TestActorRef<GCMNotifier> subject = TestActorRef.create(system, props);

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, new ArrayList<>(), new ArrayList<>()));

        subject.tell(list, ActorRef.noSender());

        verify(provider, never()).getConnection();
    }

    @Test
    public void onReceiveNotAccepted() throws Exception {
        GCMConnectionProvider provider = mock(GCMConnectionProvider.class);
        HttpURLConnection connection = mock(HttpURLConnection.class);

        User user = generateUser();
        Event event = generateGenericEvent();

        user.addToNotificationMedia(new NotificationMedium(NotificationMedium.NotificationMediumType.APP, "token", true));

        final Props props = GCMNotifier.props(provider);
        final TestActorRef<GCMNotifier> subject = TestActorRef.create(system, props);

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, new ArrayList<>(), new ArrayList<>()));

        when(provider.getConnection()).thenReturn(connection);
        when(connection.getOutputStream()).thenReturn(mock(OutputStream.class));
        when(connection.getResponseCode()).thenReturn(400);

        subject.tell(list, ActorRef.noSender());

        verify(provider, times(1)).getConnection();
    }

    @Test
    public void onReceive() throws Exception {
        GCMConnectionProvider provider = mock(GCMConnectionProvider.class);
        HttpURLConnection connection = mock(HttpURLConnection.class);

        User user = generateUser();
        Event event = generateGenericEvent();

        user.addToNotificationMedia(new NotificationMedium(NotificationMedium.NotificationMediumType.APP, "token", true));

        final Props props = GCMNotifier.props(provider);
        final TestActorRef<GCMNotifier> subject = TestActorRef.create(system, props);

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, new ArrayList<>(), new ArrayList<>()));

        when(provider.getConnection()).thenReturn(connection);
        when(connection.getOutputStream()).thenReturn(mock(OutputStream.class));
        when(connection.getResponseCode()).thenReturn(200);

        subject.tell(list, ActorRef.noSender());

        verify(provider, times(1)).getConnection();
    }

}