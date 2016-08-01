package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.City;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.LatLon;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Created by thibault on 3/21/16.
 */
public abstract class AddressMixin {
    @JsonProperty("street")
    private String street;
    @JsonProperty("housenumber")
    private String houseNumber;
    @JsonUnwrapped
    @JsonProperty
    private City city;
    @JsonProperty("country")
    private String country;
    @JsonProperty("coordinates")
    private LatLon coordinates;

    public AddressMixin(
            @JsonProperty("street") String street,
            @JsonProperty("housenumber") String houseNumber,
            @JsonProperty("city") String cityName,
            @JsonProperty("postal_code") String cityPostalCode,
            @JsonProperty("country") String country,
            @JsonProperty("coordinates") LatLon coordinates
    ) {}
}
