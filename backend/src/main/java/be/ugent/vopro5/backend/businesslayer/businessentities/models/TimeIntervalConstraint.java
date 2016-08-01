package be.ugent.vopro5.backend.businesslayer.businessentities.models;

import be.ugent.vopro5.backend.businesslayer.businessentities.validators.ValidationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static be.ugent.vopro5.backend.businesslayer.businessentities.validators.Validators.notNull;

/**
 * Created on 24/02/16.
 */
public class TimeIntervalConstraint implements TimeConstraint {

    private final LocalTime starttime;
    private final Duration duration;

    /**
     * create a new time interval constraint
     * @param starttime The starttime of the interval
     * @param duration The duration of the interval. The duration can also be used to deduce the end of the interval.
     * @throws ValidationException
     */
    public TimeIntervalConstraint(LocalTime starttime, Duration duration) throws ValidationException {
        notNull(starttime, "StartTime can not be null");
        this.starttime = starttime;
        notNull(duration, "Duration can not be null");
        this.duration = duration;
    }

    /**
     * @return
     */
    public LocalTime getStarttime() {
        return starttime;
    }

    /**
     * @return
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * @return
     */
    public LocalTime getEndtime() {
        return starttime.plusNanos(duration.toNanos());
    }

    @Override
    public boolean valid(LocalDateTime localDateTime) {
        LocalTime localTime = localDateTime.toLocalTime();

        Duration timeInBetween;
        if (localTime.isAfter(starttime)) {
            timeInBetween = duration.minus(Duration.between(starttime, localTime));
        } else {
            timeInBetween = duration.minus(Duration.between(starttime, LocalTime.MIDNIGHT
                    .minus(1, ChronoUnit.MINUTES))
                    .plus(1, ChronoUnit.MINUTES)
                    .plus(Duration.between(LocalTime.MIDNIGHT, localTime)));
        }

        return !timeInBetween.isNegative();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimeIntervalConstraint that = (TimeIntervalConstraint) o;

        if (!starttime.equals(that.starttime)) {
            return false;
        }
        return duration.equals(that.duration);

    }

    @Override
    public int hashCode() {
        int result = starttime.hashCode();
        result = 31 * result + duration.hashCode();
        return result;
    }
}
