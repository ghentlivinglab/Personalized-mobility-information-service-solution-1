package be.ugent.vopro5.backend.businesslayer.businessworkflow;

/**
 * Created by evert on 4/3/16.
 */
public interface CaptchaHelper {
    /**
     * Verify a captcha
     * @param authorizationCode
     * @return true if the captcha is valid
     */
    boolean verifyCaptcha(String authorizationCode);
}
