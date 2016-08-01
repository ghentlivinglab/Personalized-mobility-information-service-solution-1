package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.notBlank;
import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.notNull;

/**
 * Business entity which represents a notification medium. This can be any medium from which a user may want receive
 * notifications of events. ex. A user may want notifications of events to be sent to his email address or cellphone.
 */
public class NotificationMedium {
    private static final String NOTIFICATION_MEDIUM_VALUE_CAN_NOT_BE_BLANK = "NotificationMedium value can not be blank";

    protected final NotificationMediumType type;
    protected String value;
    protected boolean validated;
    protected String pin;

    /**
     * create a new notification medium
     * @param type The type of the medium ex. EMAIL.
     * @param value The value of of notificationMedium. If type equals EMAIL then the value will be the actual email address
     *              as a String. ex. "my-email@world.com". In case the type equals CELL_NUMBER then the value will be the actual
     *              cell number.
     * @param validated If validated is true, notifications can be sent to this notification medium.
     */
    public NotificationMedium(NotificationMediumType type, String value, boolean validated) {
        notNull(type, "NotificationMedium type can not be null");
        this.type = type;
        notBlank(value, NOTIFICATION_MEDIUM_VALUE_CAN_NOT_BE_BLANK);
        this.value = value;
        this.validated = validated;
        this.pin = String.valueOf((int) (Math.random() * 9000) + 1000);
    }

    /**
     * create a new notification medium
     * @param type
     * @param value
     * @param validated
     * @param pin This is usually a code sent to the users notification object to verify the ownership of the medium. If
     *            the user is the rightful owner of the medium then he/she may use this pin to validate the corresponding
     *            notificationMedium.
     */
    public NotificationMedium(NotificationMediumType type, String value, boolean validated, String pin) {
        this(type, value, validated);
        notBlank(pin, "NotificationMedium PIN cannot be blank");
        this.pin = pin;
    }

    /**
     * @return true if this notification has been validated, else returns false.
     */
    public boolean isValidated() {
        return validated;
    }

    /**
     * @return The type of the medium ex. EMAIL.
     */
    public NotificationMediumType getType() {
        return type;
    }

    /**
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value of of notificationMedium. If type equals EMAIL then the value will be the actual email address
     *              as a String. ex. "my-email@world.com". In case the type equals CELL_NUMBER then the value will be the actual
     *              cell number.
     */
    public void setValue(String value) {
        notBlank(value, NOTIFICATION_MEDIUM_VALUE_CAN_NOT_BE_BLANK);
        this.value = value;
        this.validated = false;
    }

    /**
     * validate this notification medium. Once validated is true, notifications can be sent to this notification medium.
     */
    public void validate() {
        this.validated = true;
    }

    /**
     * @return
     */
    public String getPin() {
        return pin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NotificationMedium notificationMedium = (NotificationMedium) o;

        if(notificationMedium.getType() != this.type) {
            return false;
        }

        return notificationMedium.getValue().equals(this.value);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NotificationMedium{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", validated=" + validated +
                '}';
    }

    /**
     * enum representing notification medium types
     */
    public enum NotificationMediumType {
        CELL_NUMBER,
        EMAIL,
        APP,
        FACEBOOK,
    }
}
