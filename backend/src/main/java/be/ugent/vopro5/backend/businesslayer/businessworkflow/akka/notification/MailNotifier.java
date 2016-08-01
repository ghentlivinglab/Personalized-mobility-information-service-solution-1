package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import akka.actor.Props;
import akka.actor.UntypedActor;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.PointOfInterest;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.Route;
import be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.Message;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 11.04.16.
 */
public class MailNotifier extends UntypedActor {
    private JavaMailSender mailSender;
    private VelocityEngine velocityEngine;

    /**
     * Create a new MailNotifier
     *
     * @param mailSender     The mailsender this MailNotifier should use
     * @param velocityEngine The velocityEngine this MailNotifier should use
     */
    public MailNotifier(JavaMailSender mailSender, VelocityEngine velocityEngine) {
        this.mailSender = mailSender;
        this.velocityEngine = velocityEngine;
    }

    /**
     * Static method to return the props for a MailNotifier
     *
     * @param mailSender The mailsender this MailNotifier should use
     * @param velocityEngine The velocityEngine this MailNotifier should use
     * @return The props for a MailNotifier
     */
    public static Props props(JavaMailSender mailSender, VelocityEngine velocityEngine) {
        return Props.create(MailNotifier.class, mailSender, velocityEngine);
    }

    /**
     * For each notification in the notificationList, a notification is sent to the user connected to that notification.
     * This only happens if mail notifications are enabled in at least one of ther reasons the notification was matched.
     *
     * @param message Expected to be of type Message.NotificationList, should contain the notifications to be sent
     */
    @Override
    public void onReceive(Object message) {
        if (message instanceof Message.NotificationList) {
            ((Message.NotificationList) message).forEach(notification -> {
                boolean userWantsMail = false;
                for (PointOfInterest pointOfInterest : notification.getPointsOfInterest()) {
                    userWantsMail |= pointOfInterest.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.EMAIL);
                }

                for (Route route : notification.getRoutes()) {
                    userWantsMail |= route.getNotificationMedia().contains(NotificationMedium.NotificationMediumType.EMAIL);
                }

                if (mailVerified(notification) && userWantsMail) {
                    sendMail(notification);
                }
            });
        } else {
            unhandled(message);
        }
    }

    private boolean mailVerified(Message.Notification notification) {
        return notification.getUser().getEmailisValidated();
    }

    private void sendMail(Message.Notification notification) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper msg = new MimeMessageHelper(mimeMessage);
            msg.setTo(notification.getUser().getEmail());
            msg.setFrom("Mobiliteitscentrum");
            msg.setSubject("Nieuw incident!");
            Map<String, Object> model = new HashMap<>();
            model.put("url", "https://vopro5.ugent.be/app/#/event/" + notification.getEvent().getIdentifier() + "/");
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "new_event.vm", "utf-8", model);
            msg.setText(text, true);
        };

        try {
            this.mailSender.send(preparator);
        } catch (MailException ex) {
            ex.printStackTrace();
            throw new APIException.InternalErrorException("Mail could not be sent");
        }

    }
}
