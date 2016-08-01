package be.ugent.vopro5.backend.businesslayer.businessworkflow.waze;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by evert on 4/11/16.
 */
public class WazeInputStreamProvider {

    private final URL wazeURL;

    /**
     * Create a new WazeInputStreamProvider
     * @param url the url to the waze api
     */
    public WazeInputStreamProvider(URL url) {
        this.wazeURL = url;
    }

    public InputStream getInputStream() throws IOException {
        URLConnection connection = wazeURL.openConnection();
        return connection.getInputStream();
    }
}
