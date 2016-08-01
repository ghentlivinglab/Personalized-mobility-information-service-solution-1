package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.notBlank;

/**
 * Business entity representing a city
 */
public class City {
    private final String name;
    private final String postalCode;

    /**
     * Create a new City object
     * @param name
     * @param postalCode
     * @throws ValidationException
     */
    public City(String name, String postalCode) throws ValidationException {
        notBlank(name, "Name can not be blank");
        this.name = name;
        notBlank(postalCode, "PostalCode can not be blank");
        this.postalCode = postalCode;
    }

    /**
     * @return The name of this city
     */
    public String getName() {
        return name;
    }

    /**
     * @return The postal code of this city
     */
    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        City city = (City) o;

        if (!name.equals(city.name)) {
            return false;
        }
        return postalCode.equals(city.postalCode);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + postalCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}
