package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.algorithms.EventMatchingAlgorithm;

/**
 * Created by anton on 11.04.16.
 */
public class EventMatcher extends UntypedActor {

    private final EventMatchingAlgorithm algorithm;
    private final ActorRef broadcaster;

    /**
     * Create a new EventMatcher
     *
     * @param broadcaster Broadcaster that sends all matched events to the specific user notifiers
     * @param algorithm Algorithm to determine matched events from events
     */
    public EventMatcher(ActorRef broadcaster, EventMatchingAlgorithm algorithm) {
        this.broadcaster = broadcaster;
        this.algorithm = algorithm;
    }

    /**
     * Static method to return the props for an EventMatcher
     *
     * @param broadcaster The NotificationBroadcaster this eventMatcher should send matched events to
     * @param algorithm   The algorithm this eventMatcher should use to determine whether events match
     * @return The props for an EventMatcher
     */
    public static Props props(ActorRef broadcaster, EventMatchingAlgorithm algorithm) {
        return Props.create(EventMatcher.class, broadcaster, algorithm);
    }

    /**
     * The EventMatcher transforms an EventList into a NotificationList and sends it to the broadcaster
     *
     * @param message Expected to be of type Message.EventList
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof Message.EventList) {
            broadcaster.tell(algorithm.matchEvents((Message.EventList) message), getSelf());
        } else {
            unhandled(message);
        }
    }

}