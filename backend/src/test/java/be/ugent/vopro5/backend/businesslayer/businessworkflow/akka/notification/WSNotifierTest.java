package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.AkkaTest;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;

import static be.ugent.vopro5.backend.util.ObjectGeneration.generateGenericEvent;
import static be.ugent.vopro5.backend.util.ObjectGeneration.generateUser;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WSNotifierTest extends AkkaTest {
    @Test
    public void onReceiveUnhandled() throws Exception {
        SimpMessagingTemplate template = Mockito.mock(SimpMessagingTemplate.class);

        final Props props = WSNotifier.props(template);
        final TestActorRef<WSNotifier> subject = TestActorRef.create(system, props, "testA");
        subject.tell(new Object(), ActorRef.noSender());

        verify(template, never()).convertAndSendToUser(any(), any(), any());
    }

    @Test
    public void onReceive() throws Exception {
        SimpMessagingTemplate template = Mockito.mock(SimpMessagingTemplate.class);

        User user = generateUser();
        Event event = generateGenericEvent();

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, new ArrayList<>(), new ArrayList<>()));

        final Props props = WSNotifier.props(template);
        final TestActorRef<WSNotifier> subject = TestActorRef.create(system, props, "testB");

        subject.tell(list, ActorRef.noSender());

        verify(template, times(1)).convertAndSendToUser(user.getIdentifier().toString(), "/events", event);
    }

}