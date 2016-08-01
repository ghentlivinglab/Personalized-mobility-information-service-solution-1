package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.notEmpty;
import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.positive;

/**
 * Created on 25/02/16.
 */
public class Jam {
    private List<LatLon> points;
    private float speed;
    private int delay;

    /**
     * create a new jam
     * @param points The coordinates of the Jam.
     * @param speed The average speed of the vehicles residing inside the jam.
     * @param delay The expected delay that is caused by the jam.
     * @throws ValidationException
     */
    public Jam(List<LatLon> points, float speed, int delay) throws ValidationException {
        notEmpty(points, "Points can not be empty");
        this.points = new ArrayList<>(points);
        positive(speed, "Speed can not be negative");
        this.speed = speed;
        positive(delay, "Delay can not be negative");
        this.delay = delay;
    }

    public List<LatLon> getPoints() {
        return Collections.unmodifiableList(points);
    }

    /**
     * @return The average speed of the vehicles residing inside the traffic jam.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param speed The average speed to be set.
     */
    public void setSpeed(float speed) {
        positive(speed,"Speed cannot be negative");
        this.speed = speed;
    }

    /**
     * @return The delay of this jam.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * @param delay The delay to be set.
     */
    public void setDelay(int delay) {
        positive(delay,"Delay cannot be negative");
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "Jam{" +
                "points=" + points +
                ", speed=" + speed +
                ", delay=" + delay +
                '}';
    }

    /**
     * Returns a new object with the exact same values of the properties as this object. All the properties of the copy
     * are a deep copy of the properties of the original object.
     * @return Jam
     */
    public Jam deepCopy() {
        return new Jam(points.stream().map(LatLon::deepCopy).collect(Collectors.toList()), speed, delay);
    }
}
