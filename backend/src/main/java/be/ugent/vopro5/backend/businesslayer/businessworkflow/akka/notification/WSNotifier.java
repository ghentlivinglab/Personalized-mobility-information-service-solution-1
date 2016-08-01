package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import akka.actor.Props;
import akka.actor.UntypedActor;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Created by anton on 11.04.16.
 */
public class WSNotifier extends UntypedActor {
    private SimpMessagingTemplate template;

    /**
     * Create a new WSNotifier
     *
     * @param template Template to use to send a Notification
     */
    public WSNotifier(SimpMessagingTemplate template) {
        this.template = template;
    }

    /**
     * Static method to return the props for a WSNotifier
     *
     * @param template Template to use to send a Notification
     * @return The props for a WSNotifier
     */
    public static Props props(SimpMessagingTemplate template) {
        return Props.create(WSNotifier.class, template);
    }

    /**
     * For each notification in the notificationList, a notification is sent to the user connected to that notification.
     * Message is always sent. WebSockets are fun, it's no problem if nobody is listening.
     *
     * @param message Expected to be of type Message.NotificationList, should contain the notifications to be sent
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof Message.NotificationList) {
            for (Message.Notification notification : (Message.NotificationList) message) {
                this.template.convertAndSendToUser(notification.getUser().getIdentifier().toString(), "/events", notification.getEvent());
            }
        } else {
            unhandled(message);
        }
    }
}
