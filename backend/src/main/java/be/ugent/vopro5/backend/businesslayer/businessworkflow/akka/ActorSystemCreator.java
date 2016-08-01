package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.algorithms.EventMatchingAlgorithm;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification.*;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import com.typesafe.config.ConfigFactory;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Created by anton on 11.04.16.
 */
public class ActorSystemCreator {
    private final ActorSystem system;

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FacebookClientProvider facebookClientProvider;

    @Autowired
    private GCMConnectionProvider gcmConnectionProvider;

    @Autowired
    private VelocityEngine velocityEngine;

    /**
     * Create and set up the Akka system.
     */
    public ActorSystemCreator() {
        system = ActorSystem.create("LocalSystem", ConfigFactory.defaultApplication());
    }

    /**
     * Set up the actors and add them to akka system.
     *
     * @return The eventBroker
     */
    public ActorRef createLocalEventBroker() {
        final ActorRef wsNotifier = system.actorOf(WSNotifier.props(template));
        final ActorRef mailNotifier = system.actorOf(MailNotifier.props(mailSender, velocityEngine));
        final ActorRef gcmNotifier = system.actorOf(GCMNotifier.props(gcmConnectionProvider));
        final ActorRef facebookNotifier = system.actorOf(FacebookNotifier.props(facebookClientProvider));
        final ActorRef broadcaster = system.actorOf(NotificationBroadcaster.props(wsNotifier, mailNotifier, gcmNotifier, facebookNotifier));
        final EventMatchingAlgorithm algorithm = new EventMatchingAlgorithm(dataAccessProvider);
        final ActorRef eventMatcher = system.actorOf(EventMatcher.props(broadcaster, algorithm), "eventMatcher");
        return system.actorOf(EventBroker.props(eventMatcher), "eventBroker");
    }

    /**
     * Shut down the Akka system.
     */
    public void shutdown() {
        system.terminate();
    }
}
