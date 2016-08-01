package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import java.time.LocalDateTime;

/**
 * Created by evert on 17/03/16.
 */
public interface TimeConstraint {

    /**
     * checks whether the given LocalDateTime lies within this time constraint
     * @param localDateTime
     * @return
     */
    boolean valid(LocalDateTime localDateTime);

}
