package be.ugent.vopro5.backend.businesslayer.applicationfacade.user;

import be.ugent.vopro5.backend.businesslayer.businesscontrollers.ConcurrencyController;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.NotificationMedium;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.User;
import be.ugent.vopro5.backend.businesslayer.util.ControllerCheck;
import be.ugent.vopro5.backend.datalayer.dataaccessinterface.DataAccessProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/{id}/notificationmedium/")
public class NotificationMediumController {

    @Autowired
    private DataAccessProvider dataAccessProvider;

    @Autowired
    private ConcurrencyController concurrencyController;

    /**
     * Add an app to the user's notificationMedia. On this manner a user logged in to the app can receive notifications
     * of events.
     * @param id The identifier of the user.
     * @param appBody The identifier of the application.
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/app/", method = RequestMethod.POST)
    public NotificationMedium addApp(@PathVariable String id, @RequestBody AppBody appBody) {
        concurrencyController.enter(id);
        NotificationMedium appNotificationMedium;
        try {
            User user = dataAccessProvider.getDataAccessContext().getUserDAO().find(id);
            ControllerCheck.notNull(user, User.class);

            appNotificationMedium = new NotificationMedium(NotificationMedium.NotificationMediumType.APP, appBody.appId, true);
            user.addToNotificationMedia(appNotificationMedium);

            dataAccessProvider.getDataAccessContext().getUserDAO().update(user);
        } finally {
            concurrencyController.leave(id);
        }

        return appNotificationMedium;
    }

    private static class AppBody {
        @JsonProperty
        public String appId;
    }
}
