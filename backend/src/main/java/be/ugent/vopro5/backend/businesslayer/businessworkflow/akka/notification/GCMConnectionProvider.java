package be.ugent.vopro5.backend.businesslayer.businessworkflow.akka.notification;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GCMConnectionProvider {

    @Value("${google.gcm.secret}")
    private String apiKey;

    /**
     * Creates a connection that can be written to by a GCMNotifier.
     *
     * @return The connection to be written to
     * @throws IOException If something went wrong while creating the connection
     */
    public HttpURLConnection getConnection() throws IOException {
        URL url = new URL("https://gcm-http.googleapis.com/gcm/send");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("Authorization", "key=" + apiKey);
        connection.setDoOutput(true);
        return connection;
    }
}
