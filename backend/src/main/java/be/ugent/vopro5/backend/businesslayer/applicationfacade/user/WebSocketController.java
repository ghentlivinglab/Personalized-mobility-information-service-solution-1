package be.ugent.vopro5.backend.businesslayer.applicationfacade.user;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * Created by thibault on 4/11/16.
 */
@Controller
public class WebSocketController {

    /**
     * Method that does nothing, but we need the @MessageMapping on some method
     */
    @MessageMapping("/ws")
    public void ws() {
        // Do nothing here, we just need the @MessageMapping on some method.
    }

}
