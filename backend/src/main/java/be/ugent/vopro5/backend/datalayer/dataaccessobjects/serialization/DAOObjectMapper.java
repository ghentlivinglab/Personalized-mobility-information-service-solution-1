package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class DAOObjectMapper extends ObjectMapper {
    public DAOObjectMapper() {
        // Disable auto-detection of getters and setters
        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NONE);
        // Add modules
        registerModule(new JavaTimeModule());
        registerModule(new DAOMixinModule());
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
