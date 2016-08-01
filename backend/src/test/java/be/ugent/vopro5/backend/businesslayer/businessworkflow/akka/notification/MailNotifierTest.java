package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.AkkaTest;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static be.ugent.vopro5.backend.util.ObjectGeneration.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by anton on 12.04.16.
 */
public class MailNotifierTest extends AkkaTest {
    @Test
    public void onReceiveUnhandled() throws Exception {
        JavaMailSender sender = Mockito.mock(JavaMailSender.class);
        VelocityEngine engine = Mockito.mock(VelocityEngine.class);

        final Props props = MailNotifier.props(sender, engine);
        final TestActorRef<MailNotifier> subject = TestActorRef.create(system, props);
        subject.tell(new Object(), ActorRef.noSender());

        verify(sender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    public void onReceiveUserNoPoiOrTravel() throws Exception {
        User user = generateUser();
        Event event = generateGenericEvent();

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, new ArrayList<>(), new ArrayList<>()));

        JavaMailSender sender = Mockito.mock(JavaMailSender.class);
        VelocityEngine engine = Mockito.mock(VelocityEngine.class);

        final Props props = MailNotifier.props(sender, engine);
        final TestActorRef<MailNotifier> subject = TestActorRef.create(system, props);
        subject.tell(list, ActorRef.noSender());

        verify(sender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    public void onReceiveUserNoWant() throws Exception {
        User user = generateUser();
        Event event = generateGenericEvent();

        PointOfInterest poi = generatePointOfInterest();
        new HashSet<>(poi.getNotificationMedia()).stream().filter(t -> t.equals(NotificationMedium.NotificationMediumType.EMAIL)).forEach(poi::removeFromNotificationMedia);
        poi.addToNotificationMedia(NotificationMedium.NotificationMediumType.CELL_NUMBER);

        Route route = generateRoute();
        new HashSet<>(route.getNotificationMedia()).stream().filter(t -> t.equals(NotificationMedium.NotificationMediumType.EMAIL)).forEach(route::removeFromNotificationMedia);
        route.addToNotificationMedia(NotificationMedium.NotificationMediumType.CELL_NUMBER);

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, Collections.singletonList(poi), Collections.singletonList(route)));

        JavaMailSender sender = Mockito.mock(JavaMailSender.class);
        VelocityEngine engine = Mockito.mock(VelocityEngine.class);

        final Props props = MailNotifier.props(sender, engine);
        final TestActorRef<MailNotifier> subject = TestActorRef.create(system, props);
        subject.tell(list, ActorRef.noSender());

        verify(sender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    public void onReceiveUserNotVerified() throws Exception {
        User user = generateUser();
        user.setEmail("new@email.com");
        Event event = generateGenericEvent();

        PointOfInterest poi = generatePointOfInterest();
        new HashSet<>(poi.getNotificationMedia()).stream().filter(t -> t.equals(NotificationMedium.NotificationMediumType.EMAIL)).forEach(poi::removeFromNotificationMedia);
        poi.addToNotificationMedia(NotificationMedium.NotificationMediumType.EMAIL);

        Route route = generateRoute();
        new HashSet<>(route.getNotificationMedia()).stream().filter(t -> t.equals(NotificationMedium.NotificationMediumType.EMAIL)).forEach(route::removeFromNotificationMedia);
        route.addToNotificationMedia(NotificationMedium.NotificationMediumType.CELL_NUMBER);

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, Collections.singletonList(poi), Collections.singletonList(route)));

        JavaMailSender sender = Mockito.mock(JavaMailSender.class);
        VelocityEngine engine = Mockito.mock(VelocityEngine.class);

        final Props props = MailNotifier.props(sender, engine);
        final TestActorRef<MailNotifier> subject = TestActorRef.create(system, props);
        subject.tell(list, ActorRef.noSender());

        verify(sender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    public void onReceiveUser() throws Exception {
        User user = generateUser();
        user.setEmailisValidated();
        Event event = generateGenericEvent();

        PointOfInterest poi = generatePointOfInterest();
        new HashSet<>(poi.getNotificationMedia()).stream().filter(t -> t.equals(NotificationMedium.NotificationMediumType.EMAIL)).forEach(poi::removeFromNotificationMedia);
        poi.addToNotificationMedia(NotificationMedium.NotificationMediumType.EMAIL);

        Route route = generateRoute();
        new HashSet<>(route.getNotificationMedia()).stream().filter(t -> t.equals(NotificationMedium.NotificationMediumType.EMAIL)).forEach(route::removeFromNotificationMedia);
        route.addToNotificationMedia(NotificationMedium.NotificationMediumType.CELL_NUMBER);

        Message.NotificationList list = new Message.NotificationList();
        list.add(new Message.Notification(event, user, Collections.singletonList(poi), Collections.singletonList(route)));

        JavaMailSender sender = Mockito.mock(JavaMailSender.class);
        VelocityEngine engine = Mockito.mock(VelocityEngine.class);

        final Props props = MailNotifier.props(sender, engine);
        final TestActorRef<MailNotifier> subject = TestActorRef.create(system, props);
        subject.tell(list, ActorRef.noSender());

        verify(sender, times(1)).send(any(MimeMessagePreparator.class));
    }
}
