package be.ugent.vopro5.backend.datalayer.dataaccessobjects.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class CityMixin {

    CityMixin(@JsonProperty("name") String name,
              @JsonProperty("postal_code") String postalCode) {
    }

    @JsonProperty("name")
    public abstract String getName();

    @JsonProperty("postal_code")
    public abstract String getPostalCode();
}
