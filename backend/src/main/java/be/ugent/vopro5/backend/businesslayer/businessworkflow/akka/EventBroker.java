package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Created by anton on 11.04.16.
 */
public class EventBroker extends UntypedActor{

    private ActorRef eventMatcher;

    /**
     * Create a new eventBroker
     * @param eventMatcher the eventmatcher this broker should use
     */
    public EventBroker(ActorRef eventMatcher) {
        this.eventMatcher = eventMatcher;
    }

    /**
     * Static method to return the props for an eventbroker
     *
     * @param eventMatcher The eventMatcher this broker should use
     * @return The props for an eventbroker
     */
    public static Props props(ActorRef eventMatcher) {
        return Props.create(EventBroker.class, eventMatcher);
    }

    /**
     * The eventBroker sends an EventList to the eventMatcher
     *
     * @param message Expected to be of type Message.EventList, this will be sent to the eventMatcher
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof Message.EventList) {
            eventMatcher.tell(message, getSelf());
        } else {
            unhandled(message);
        }
    }
}
