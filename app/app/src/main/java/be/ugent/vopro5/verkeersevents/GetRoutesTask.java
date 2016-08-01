package be.ugent.vopro5.verkeersevents;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

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
public class GetRoutesTask extends GetterTask<Route> {
    private final String userID;
    private final RouteProgressListener listener;

    public GetRoutesTask(String token, String userID, RouteProgressListener listener) {
        super(token, listener);
        this.userID = userID;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            String accessToken = getAccessToken();
            if (accessToken == null) {
                return null;
            }
            URL url = new URL(String.format(Constants.API_URL + "user/%s/travel/", URLEncoder.encode(userID, "UTF-8")));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\A");

            JSONArray response = (JSONArray) new JSONTokener(scanner.next()).nextValue();
            scanner.close();
            for (int i = 0; i < response.length(); i++) {
                LatLng startpoint = new LatLng(response.getJSONObject(i).getJSONObject("startpoint").getJSONObject("coordinates").getDouble("lat"),
                        response.getJSONObject(i).getJSONObject("startpoint").getJSONObject("coordinates").getDouble("lon"));
                LatLng endpoint = new LatLng(response.getJSONObject(i).getJSONObject("endpoint").getJSONObject("coordinates").getDouble("lat"),
                        response.getJSONObject(i).getJSONObject("endpoint").getJSONObject("coordinates").getDouble("lon"));
                getRoutes(accessToken, response.getJSONObject(i).getString("id"), startpoint, endpoint);
            }
        } catch (FileNotFoundException e) {
            listener.connectionFailed();
        } catch (JSONException | IOException e) {
            Log.e("getRoutes", e.getMessage(), e);
        }
        return null;
    }

    private void getRoutes(String accessToken, String id, LatLng startpoint, LatLng endpoint) throws IOException, JSONException {
        URL url = new URL(String.format(Constants.API_URL + "user/%s/travel/%s/route/",
                URLEncoder.encode(userID, "UTF-8"),
                URLEncoder.encode(id, "UTF-8")));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        Scanner scanner = new Scanner(connection.getInputStream());
        scanner.useDelimiter("\\A");

        JSONArray response = (JSONArray) new JSONTokener(scanner.next()).nextValue();
        scanner.close();
        for (int i = 0; i < response.length(); i++) {
            publishProgress(Route.fromJSONObject(startpoint, endpoint, response.getJSONObject(i)));
        }
    }

    @Override
    protected void onProgressUpdate(Route... values) {
        listener.progress(values);
    }

    public interface RouteProgressListener extends ProgressListener {
        void progress(Route... routes);
    }
}
