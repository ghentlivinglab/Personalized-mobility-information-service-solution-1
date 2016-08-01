package be.ugent.vopro5.backend.businesslayer.util.mapping;

import be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization.RESTModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by thibault on 3/2/16.
 */
public class MyObjectMapper extends ObjectMapper {
    /**
     * Configure properties of the objectmapper.
     */
    public MyObjectMapper() {
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

        configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        registerModule(new RESTModule());
    }
}
