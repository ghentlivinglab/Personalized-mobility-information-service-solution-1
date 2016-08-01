package be.ugent.vopro5.verkeersevents;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by evert on 4/30/16.
 */
public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        if(preferences.getString(Constants.TOKEN, null) == null) {
            // Not logged in yet
            return;
        }


        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            URL url = new URL(String.format(
                    Constants.API_URL + "/user/%s/notificationmedium/app/",
                    URLEncoder.encode(preferences.getString(Constants.USER_ID, ""), "UTF-8")
            ));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + preferences.getString(Constants.TOKEN, null));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_id", token);

            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            writer.write(jsonObject.toString());
            writer.close();

            int response = urlConnection.getResponseCode();
            if(response != 200) {
                throw new IOException("Failed to post appId");
            }
            preferences.edit().putString(Constants.APP_ID, token).commit();
        } catch (IOException | JSONException e) {
            Log.e("register", e.getMessage(), e);
        }
    }
}
