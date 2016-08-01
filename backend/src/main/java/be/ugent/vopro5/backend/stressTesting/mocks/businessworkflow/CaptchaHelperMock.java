package be.ugent.vopro5.backend.stressTesting.mocks.businessworkflow;

import be.ugent.vopro5.backend.businesslayer.businessworkflow.CaptchaHelper;

/**
 * Created by anton on 4/30/16.
 */
public class CaptchaHelperMock implements CaptchaHelper {
    @Override
    public boolean verifyCaptcha(String authorizationCode) {
        try {
            Thread.sleep(30);//simulate request to google
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}
