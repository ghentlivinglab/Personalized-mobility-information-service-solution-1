package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.time.LocalDateTime;
import java.util.Arrays;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.length;

/**
 * Time contraint for days of the week
 */
public class WeekDayConstraint implements TimeConstraint {

    private final boolean[] recurring;

    /**
     * create a new weekday constraint
     * @param recurring: a boolean array containing a boolean for each day of the week, starting with monday
     *                 true for a centain day means for that day of the week, the constraint is valid. Ex. [1,1,0,0,0,0,0]
     *                 means that the constraint is valid on monday and tuesday only.
     * @throws ValidationException
     */
    public WeekDayConstraint(boolean[] recurring) throws ValidationException {
        length(recurring, 7, "Recurring array has to have length 7");
        this.recurring = recurring;
    }

    /**
     * @return
     */
    public boolean[] getRecurring() {
        return recurring;
    }

    @Override
    public boolean valid(LocalDateTime localDateTime) {
        return recurring[localDateTime.getDayOfWeek().getValue() - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WeekDayConstraint that = (WeekDayConstraint) o;

        return Arrays.equals(recurring, that.recurring);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(recurring);
    }
}
