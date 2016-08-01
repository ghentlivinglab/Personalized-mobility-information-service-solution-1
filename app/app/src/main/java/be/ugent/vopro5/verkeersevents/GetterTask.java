package be.ugent.vopro5.verkeersevents;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by evert on 5/1/16.
 */
public abstract class GetterTask<T> extends AsyncTask<Void, T, Void> {

    private final ProgressListener listener;
    private final String token;

    public GetterTask(String token, ProgressListener listener) {
        this.listener = listener;
        this.token = token;
    }

    protected String getAccessToken() throws JSONException, IOException {
        URL url = new URL("https://vopro5.ugent.be/api/access_token/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");

        Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        JSONObject object = new JSONObject();
        object.put("refresh_token", token);
        writer.write(object.toString());
        writer.close();

        if (connection.getResponseCode() != 200) {
            listener.connectionFailed();
        }

        Scanner scanner = new Scanner(connection.getInputStream());
        scanner.useDelimiter("\\A");

        JSONObject response = (JSONObject) new JSONTokener(scanner.next()).nextValue();
        scanner.close();
        return response.getString("token");
    }

}
