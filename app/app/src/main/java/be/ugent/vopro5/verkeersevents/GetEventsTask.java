package be.ugent.vopro5.verkeersevents;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by evert on 5/1/16.
 */
class GetEventsTask extends GetterTask<Event> {

    private final String userID;
    private final Context context;
    private final EventProgressListener listener;

    public GetEventsTask(String token, String userID, EventProgressListener listener, Context context) {
        super(token, listener);
        this.userID = userID;
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                return null;
            }
            getEvents(accessToken);
        } catch (FileNotFoundException e) {
            listener.connectionFailed();
        } catch (IOException | JSONException e) {
            Log.e("getEvents", e.getMessage(), e);
        }
        return null;
    }

    private void getEvents(String accessToken) throws IOException, JSONException {
        URL url = new URL(String.format(Constants.API_URL + "event/?active=true&user_id=%s", URLEncoder.encode(userID, "UTF-8")));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        Scanner scanner = new Scanner(connection.getInputStream());
        scanner.useDelimiter("\\A");

        JSONArray response = (JSONArray) new JSONTokener(scanner.next()).nextValue();
        scanner.close();
        for (int i = 0; i < response.length(); i++) {
            publishProgress(Event.fromJSONObject(response.getJSONObject(i), context));
        }
    }

    @Override
    protected void onProgressUpdate(Event... events) {
        listener.progress(events);
    }

    public interface EventProgressListener extends ProgressListener {
        void progress(Event... events);
    }
}
