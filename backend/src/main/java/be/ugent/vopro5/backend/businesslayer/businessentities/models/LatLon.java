package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.verifyComparable;

/**
 * Created on 24/02/16.
 */
public class LatLon {

    private final double lat;
    private final double lon;

    /**
     * generate a new LatLon
     * @param lat: latitude
     * @param lon: longitude
     * @throws ValidationException
     */
    public LatLon(double lat, double lon) throws ValidationException {
        verifyComparable(lat, -90.0, 90.0, "Latitude needs to be between -90 an 90");
        this.lat = lat;
        verifyComparable(lon, -180.0, 180.0, "Longitude needs to be between -180 an 180");
        this.lon = lon;
    }

    /**
     * @return The latitude of these coordinates.
     */
    public double getLat() {
        return lat;
    }

    /**
     * @return The longitude of these coordinates.
     */
    public double getLon() {
        return lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LatLon latLon = (LatLon) o;

        if (Double.compare(latLon.lat, lat) != 0) {
            return false;
        }
        return Double.compare(latLon.lon, lon) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "LatLon{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }

    /**
     * Returns a new object with the exact same values of the properties as this object. All the properties of the copy
     * are a deep copy of the properties of the original object.
     * @return
     */
    public LatLon deepCopy() {
        return new LatLon(lat,lon);
    }
}
