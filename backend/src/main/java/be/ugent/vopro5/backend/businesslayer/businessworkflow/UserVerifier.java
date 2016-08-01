package be.ugent.vopro5.backend.businesslayer.businessworkflow;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by evert on 16/03/16.
 */
@Service
public class UserVerifier implements GeneralUserVerifier{

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private VelocityEngine velocityEngine;

    /**
     * Send an email so the user can verify its email address
     * @param user
     */
    public void sendEmailVerification(User user) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper msg = new MimeMessageHelper(mimeMessage);
            msg.setTo(user.getEmail());
            msg.setFrom("Mobiliteitscentrum");
            msg.setSubject("Verifieer uw email");
            Map model = new HashMap();
            model.put("code", user.getEmailVerification());
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "verify_email.vm", "utf-8", model);
            msg.setText(text, true);
        };

        try {
            this.mailSender.send(preparator);
        } catch (MailException ex) {
            ex.printStackTrace();
            throw new APIException.InternalErrorException("Mail could not be sent");
        }
    }

    /**
     * Checks if pin submitted by user is valid
     * (put in verifier for easy mocking)
     * @param user
     * @param pinAttempt pin that is sent by user
     * @return true if pin correct
     */
    public boolean verifyEmailPin(User user, String pinAttempt){
        return user.getEmailVerification().equals(pinAttempt);
    }

}
