package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thibault on 4/1/16.
 */
public abstract class ExternalEventPublisherMixin {
    ExternalEventPublisherMixin(
            @JsonProperty("name") String name,
            @JsonProperty("icon_url") String imageUrl
    ) {
    }

    @JsonProperty("name")
    private String name;
    @JsonProperty("icon_url")
    private String imageUrl;
}
