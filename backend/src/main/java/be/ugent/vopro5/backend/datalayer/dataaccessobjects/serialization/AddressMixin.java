package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.City;
import be.ugent.vopro5.backend.businesslayer.businessentities.models.LatLon;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AddressMixin {

    AddressMixin(@JsonProperty("street") String street,
                 @JsonProperty("house_number") String houseNumber,
                 @JsonProperty("city") City city,
                 @JsonProperty("country") String country,
                 @JsonProperty("coordinates") LatLon coordinates) {
    }

    @JsonProperty("street")
    public abstract String getStreet();

    @JsonProperty("house_number")
    public abstract String getHouseNumber();

    @JsonProperty("city")
    public abstract City getCity();

    @JsonProperty("country")
    public abstract String getCountry();

    @JsonProperty("coordinates")
    public abstract LatLon getCoordinates();
}
