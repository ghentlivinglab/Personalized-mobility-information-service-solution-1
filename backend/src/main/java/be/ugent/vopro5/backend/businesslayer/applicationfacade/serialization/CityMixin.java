package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thibault on 3/21/16.
 */
public class CityMixin {
    @JsonProperty(value = "city")
    private String name;
    @JsonProperty
    private String postalCode;
}
