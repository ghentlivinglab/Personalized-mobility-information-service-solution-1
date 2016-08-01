package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

/**
 * Buisiness entity representing an event publisher.
 * An event publisher is able to generate events that will be published to all users
 */
public abstract class EventPublisher {
    protected final EventPublisherType type;

    /**
     * Create a new event publisher
     * @param type
     */
    public EventPublisher(EventPublisherType type) {
        this.type = type;
    }

    /**
     * Factory method to create subclasses easy
     * @param type     The type of EventPublisher to create
     * @param name     The name of the EventPublisher
     * @param imageURL The url to the icon of the EventPublisher
     * @return A subclass of EventPublisher
     */
    public static EventPublisher factory(EventPublisherType type, String name, String imageURL) {
        if (type == EventPublisherType.EXTERNAL) {
            return new ExternalEventPublisher(name, imageURL);
        } else if (type == EventPublisherType.WAZE) {
            return new WazeEventPublisher();
        } else {
            throw new ValidationException("No EventPublisher exists with this type");
        }
    }

    /**
     * @return The name of this EventPublisher
     */
    public abstract String getName();

    /**
     * @return The path of the image for this EventPublisher.
     */
    public abstract String getImage();

    public EventPublisherType getType() {
        return type;
    }

    /**
     * enum representing the different types of event publishers
     */
    public enum EventPublisherType {
        EXTERNAL,
        WAZE
    }

    /**
     * Business entity representing internal event publishers
     * an internal event publisher is an event publisher which fetches its data automatically from en axternal service
     * (like WAZE)
     */
    public static abstract class InternalEventPublisher extends EventPublisher {
        /**
         * Create a new internal event publisher
         * @param type
         */
        public InternalEventPublisher(EventPublisherType type) {
            super(type);
        }
    }

    /**
     * Business entity representing external event publishers
     * an external event publisher is an event publisher which gets its data manually.
     * New events are created by operators on the website.
     */
    public static class ExternalEventPublisher extends EventPublisher {
        private String name;
        private String imageUrl;

        /**
         * create a new internal event publisher
         * @param name: the name of this publisher
         * @param imageUrl: the url for the icon shown on the website
         */
        public ExternalEventPublisher(String name, String imageUrl) {
            super(EventPublisherType.EXTERNAL);
            this.name = name;
            this.imageUrl = imageUrl;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getImage() {
            return imageUrl;
        }
    }
}
