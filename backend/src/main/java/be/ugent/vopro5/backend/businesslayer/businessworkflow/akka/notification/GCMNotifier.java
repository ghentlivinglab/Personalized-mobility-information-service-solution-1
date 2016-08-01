package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import akka.actor.Props;
import akka.actor.UntypedActor;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GCMNotifier extends UntypedActor {

    private final JsonNodeFactory factory = JsonNodeFactory.instance;
    private final GCMConnectionProvider provider;
    private Logger logger = LogManager.getLogger(GCMNotifier.class);

    /**
     * Create a new GCMNotifier
     *
     * @param provider The provider this Notifier should use
     */
    public GCMNotifier(GCMConnectionProvider provider) {
        this.provider = provider;
    }

    /**
     * Returns the props to create a new GCMNotifier
     *
     * @param connectionProvider The connectionProvider this class should use to get it's connections
     * @return The props for a GCMNotifier
     */
    public static Props props(GCMConnectionProvider connectionProvider) {
        return Props.create(GCMNotifier.class, connectionProvider);
    }

    /**
     * For each notification in the notificationList, a notification is sent to the user connected to that notification.
     * This only happens if the user has an APP notificationMedium.
     *
     * @param message Expected to be of type Message.NotificationList, should contain the notifications to be sent
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof Message.NotificationList) {
            Message.NotificationList notifications = (Message.NotificationList) message;
            Set<User> uniqueUsers = notifications.stream().map(Message.Notification::getUser).collect(Collectors.toSet());
            for (User user : uniqueUsers) {
                List<Event> events = notifications.stream().filter(n -> n.getUser().equals(user)).map(Message.Notification::getEvent).collect(Collectors.toList());
                Set<NotificationMedium> appNotificationMedia = user.getNotificationMedia().stream().filter(notificationMedium -> notificationMedium.getType().equals(NotificationMedium.NotificationMediumType.APP)).collect(Collectors.toSet());
                for (NotificationMedium appNotificationMedium : appNotificationMedia) {
                    sendGCMNotification(appNotificationMedium.getValue(), events);
                }
            }
        } else {
            unhandled(message);
        }
    }

    private void sendGCMNotification(String value, List<Event> events) {
        try {
            HttpURLConnection connection = provider.getConnection();

            ObjectNode root = factory.objectNode();
            root.put("to", value);
            ObjectNode data = root.putObject("data");

            data.put("num", events.size());

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
                writer.write(root.toString());
            }

            if (connection.getResponseCode() != 200) {
                throw new IOException("Failed to send gcm message");
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
