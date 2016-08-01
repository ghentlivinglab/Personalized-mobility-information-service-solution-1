package be.ugent.vopro5.verkeersevents;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by evert on 4/30/16.
 */
public class EventsInstanceIDListenerService extends InstanceIDListenerService{

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
