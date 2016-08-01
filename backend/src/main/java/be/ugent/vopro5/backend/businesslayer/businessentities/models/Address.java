package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.*;

/**
 * Buisiness entity representing an address
 */
public class Address {
    private final String street;
    private final String houseNumber;
    private final City city;
    private final String country;
    private final LatLon coordinates;

    /**
     * Create a new Address object
     * @param street
     * @param houseNumber
     * @param city City object representing the city
     * @param country
     * @param coordinates
     * @throws ValidationException
     */
    public Address(String street, String houseNumber, City city, String country, LatLon coordinates) throws ValidationException {
        notBlank(street, "Street can not be blank");
        this.street = street;
        notBlank(houseNumber, "HouseNumber can not be blank");
        this.houseNumber = houseNumber;
        notNull(city, "City can not be null");
        this.city = city;
        length(country, 2, "Country has to be a 2-letter country code");
        this.country = country;
        notNull(coordinates, "Coordinates can't be null");
        this.coordinates = coordinates;
    }


    /**
     * Create a new Addres object
     * @param street
     * @param houseNumber
     * @param cityName
     * @param cityPostalCode
     * @param country
     * @param coordinates
     * @throws ValidationException
     */
    public Address(String street, String houseNumber, String cityName, String cityPostalCode, String country, LatLon coordinates) throws ValidationException {
        this(street, houseNumber, new City(cityName, cityPostalCode), country, coordinates);
    }

    /**
     * @return The street of this address.
     */
    public String getStreet() {
        return street;
    }

    /**
     * @return The housenumber of this address.
     */
    public String getHouseNumber() {
        return houseNumber;
    }

    /**
     * @return The city of this address.
     */
    public City getCity() {
        return city;
    }

    /**
     * @return The country of this adress.
     */
    public String getCountry() {
        return country;
    }
    /**
     * @return The coordinates of this address.
     */
    public LatLon getCoordinates() {
        return coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Address address = (Address) o;

        if (!street.equals(address.street)) {
            return false;
        }
        if (!houseNumber.equals(address.houseNumber)) {
            return false;
        }
        if (!city.equals(address.city)) {
            return false;
        }
        if (!country.equals(address.country)) {
            return false;
        }
        return coordinates.equals(address.coordinates);

    }

    @Override
    public int hashCode() {
        int result = street.hashCode();
        result = 31 * result + houseNumber.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + country.hashCode();
        result = 31 * result + coordinates.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", city=" + city +
                ", country='" + country + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}
