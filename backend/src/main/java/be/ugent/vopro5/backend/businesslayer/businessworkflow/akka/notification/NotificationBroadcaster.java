package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;

/**
 * Created by anton on 11.04.16.
 */
public class NotificationBroadcaster extends UntypedActor {

    private final ActorRef mailNotifier;
    private final ActorRef wsNotifier;
    private final ActorRef gcmNotifier;
    private final ActorRef facebookNotifier;

    /**
     * Create a new NotificationBroadcaster
     *
     * @param wsNotifier A WebSocketNotifier
     * @param mailNotifier A MailNotifier
     * @param gcmNotifier A GCMNotifier
     * @param facebookNotifier A FacebookNotifier
     */
    public NotificationBroadcaster(ActorRef wsNotifier, ActorRef mailNotifier, ActorRef gcmNotifier, ActorRef facebookNotifier) {
        this.wsNotifier = wsNotifier;
        this.mailNotifier = mailNotifier;
        this.gcmNotifier = gcmNotifier;
        this.facebookNotifier = facebookNotifier;
    }

    /**
     * Static method to return the props for a NotificationBroadcaster
     *
     * @param wsNotifier       A WebSocketNotifier
     * @param mailNotifier     A MailNotifier
     * @param gcmNotifier      A GCMNotifier
     * @param facebookNotifier A FacebookNotifier
     * @return The props for a NotificationBroadcaster
     */
    public static Props props(ActorRef wsNotifier, ActorRef mailNotifier, ActorRef gcmNotifier, ActorRef facebookNotifier) {
        return Props.create(NotificationBroadcaster.class, wsNotifier, mailNotifier, gcmNotifier, facebookNotifier);
    }

    /**
     * Broadcast the notification list to each notifier.
     *
     * @param message Expected to be of type Message.NotificationList, should contain the notifications to be sent
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof Message.NotificationList) {
            wsNotifier.tell(message, getSelf());
            mailNotifier.tell(message, getSelf());
            gcmNotifier.tell(message, getSelf());
            facebookNotifier.tell(message, getSelf());
        } else {
            unhandled(message);
        }
    }
}
