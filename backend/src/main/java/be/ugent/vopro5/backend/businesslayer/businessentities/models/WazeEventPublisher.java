package be.ugent.vopro5.backend.businesslayer.businessentities.models;

/**
 * Created by thibault on 3/21/16.
 */
public class WazeEventPublisher extends EventPublisher.InternalEventPublisher {

    /**
     * create a new waze event publisher
     */
    public WazeEventPublisher() {
        super(EventPublisherType.WAZE);
    }

    @Override
    public String getName() {
        return "Waze";
    }

    @Override
    public String getImage() {
        return "http://cdn-img.easyicon.net/png/11266/1126637.gif";
    }
}
