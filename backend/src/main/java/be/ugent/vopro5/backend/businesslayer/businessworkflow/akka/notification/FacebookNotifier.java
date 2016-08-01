package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import akka.actor.Props;
import akka.actor.UntypedActor;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Event;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.FacebookType;

/**
 * Created by thibault on 5/8/16.
 */
public class FacebookNotifier extends UntypedActor {

    private final FacebookClientProvider provider;

    /**
     * Create a new FacebookNotifier
     *
     * @param provider The facebookclientprovider this notifier should use
     */
    public FacebookNotifier(FacebookClientProvider provider) {
        this.provider = provider;
    }

    /**
     * Static method to return the props for a FaceBookNotifier
     *
     * @param provider The facebookclientprovider this notifier should use
     * @return The props for a FacebookNotifier
     */
    public static Props props(FacebookClientProvider provider) {
        return Props.create(FacebookNotifier.class, provider);
    }

    /**
     * For each notification in the notificationList, a notification is sent to the user connected to that notification.
     * This only happens if the user has a FACEBOOK notificationMedium.
     *
     * @param message Expected to be of type Message.NotificationList, should contain the notifications to be sent
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof Message.NotificationList) {
            for (Message.Notification notification : (Message.NotificationList) message) {
                User user = notification.getUser();
                Event event = notification.getEvent();
                String facebookUserId = null;
                for (NotificationMedium medium : user.getNotificationMedia()) {
                    if (medium.getType() == NotificationMedium.NotificationMediumType.FACEBOOK) {
                        facebookUserId = medium.getValue();
                    }
                }
                if (facebookUserId != null) {
                    FacebookClient facebookClient = provider.getFacebookClient();
                    try {
                        facebookClient.publish(facebookUserId
                                        + "/notifications", FacebookType.class,
                                Parameter.with("template", "Nieuw incident: " + event.getEventType()));
                    } catch (FacebookOAuthException e) {
                        // Remove all facebook notifications from this user
                        user.getNotificationMedia().stream()
                                .filter(medium -> medium.getType() == NotificationMedium.NotificationMediumType.FACEBOOK)
                                .forEach(user::removeFromNotificationMedia);
                    }
                }
            }
        } else {
            unhandled(message);
        }
    }
}
