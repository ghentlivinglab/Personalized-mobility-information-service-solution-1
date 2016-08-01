package be.ugent.vopro5.verkeersevents;

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
public class GetPointsOfInterestTask extends GetterTask<PointOfInterest> {
    private final String userID;
    private final PointOfInterestProgressListener listener;

    public GetPointsOfInterestTask(String token, String userID, PointOfInterestProgressListener listener) {
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
            URL url = new URL(String.format(Constants.API_URL + "user/%s/point_of_interest/", URLEncoder.encode(userID, "UTF-8")));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\A");

            JSONArray response = (JSONArray) new JSONTokener(scanner.next()).nextValue();
            scanner.close();
            for (int i = 0; i < response.length(); i++) {
                publishProgress(PointOfInterest.fromJSONObject(response.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            listener.connectionFailed();
        } catch (JSONException | IOException e) {
            Log.e("getPointsOfInterest", e.getMessage(), e);
        }
        return null;

    }

    @Override
    protected void onProgressUpdate(PointOfInterest... values) {
        listener.progress(values);
    }

    public interface PointOfInterestProgressListener extends ProgressListener {
        void progress(PointOfInterest... pointsOfInterest);
    }
}
