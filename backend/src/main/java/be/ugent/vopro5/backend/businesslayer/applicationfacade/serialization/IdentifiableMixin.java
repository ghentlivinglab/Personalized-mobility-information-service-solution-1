package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Created by thibault on 3/22/16.
 */
public abstract class IdentifiableMixin {
    @JsonProperty("id")
    public abstract UUID getIdentifier();
}
