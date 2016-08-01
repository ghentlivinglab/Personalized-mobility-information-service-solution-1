package be.ugent.vopro5.backend.businesslayer.businessworkflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by anton on 24.03.16.
 */
public class GoogleCaptchaHelper implements CaptchaHelper {

    private static final Logger logger = LogManager.getLogger(GoogleCaptchaHelper.class.getName());

    private String secret;
    private ObjectMapper objectMapper;

    /**
     * Create a new GoogleCaptchaHelper
     * @param objectMapper
     * @param secret
     */
    public GoogleCaptchaHelper(ObjectMapper objectMapper, String secret) {
        this.objectMapper = objectMapper;
        this.secret = secret;
    }

    /**
     * Verify the google captcha
     * @param authorizationCode the authorization-code the captcha generated
     * @return true if the captcha is valid
     */
    public boolean verifyCaptcha(String authorizationCode) {
        if (authorizationCode == null || "".equals(authorizationCode)) {
            return false;
        }
        JsonNode jsonObject = null;
        try {
            String charset = java.nio.charset.StandardCharsets.UTF_8.name();
            String url = "https://www.google.com/recaptcha/api/siteverify";
            String query = String.format("secret=%s&response=%s",
                    URLEncoder.encode(secret, charset),
                    URLEncoder.encode(authorizationCode, charset));

            jsonObject = objectMapper.readTree(new URL(url + "?" + query));
        } catch (IOException e) {
            logger.error(e);
        }
        return jsonObject != null && jsonObject.get("success").asBoolean();
    }
}
