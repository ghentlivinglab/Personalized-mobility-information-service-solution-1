package be.ugent.vopro5.backend.businesslayer.applicationfacade.user;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.util.APIException;
import be.ugent.vopro5.backend.businesslayer.util.AccessToken;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thibault on 4/7/16.
 */
@RestController
@RequestMapping("/user/forgot_password")
public class ForgotPasswordController {
    @Autowired
    private DataAccessProvider dataAccessProvider;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private VelocityEngine velocityEngine;
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Send an email to the user with a new access code.
     * @param body The body of the request
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST)
    public void forgotPassword(@Valid @RequestBody ForgotPasswordInputBody body) {
        User user = dataAccessProvider.getDataAccessContext().getUserDAO().findByEmail(body.getEmail());
        if (user != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 30);
            Date exp = calendar.getTime();
            AccessToken accessToken = new AccessToken(user.getIdentifier().toString(), exp);

            MimeMessagePreparator preparator = mimeMessage -> {
                MimeMessageHelper msg = new MimeMessageHelper(mimeMessage);
                msg.setTo(user.getEmail());
                msg.setFrom("Mobiliteitscentrum");
                msg.setSubject("Wachtwoord vergeten");
                Map model = new HashMap();
                model.put("token", accessToken.toToken(secret));
                model.put("exp", exp.toString());
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "template_forgot_password.vm", "utf-8", model);
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

    private static class ForgotPasswordInputBody {
        @JsonProperty(required = true, value = "email")
        private String email;

        public String getEmail() {
            return email;
        }
    }
}
