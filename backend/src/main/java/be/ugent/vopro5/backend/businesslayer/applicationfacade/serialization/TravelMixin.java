package be.ugent.vopro5.backend.businesslayer.applicationfacade.serialization;

import be.ugent.vopro5.backend.businesslayer.businessentities.models.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Created by Michael Weyns on 3/21/16.
 */
public abstract class TravelMixin {
    private static final String TIME_FORMAT = "HH:mm";

    @JsonCreator
    public TravelMixin(
            @JsonProperty("name") String name,
            @JsonProperty("is_arrival_time") boolean isArrivalTime,
            @JsonProperty("startpoint") Address startPoint,
            @JsonProperty("endpoint") Address endPoint,
            @JsonProperty("time_interval") @JsonDeserialize(using = TimeIntervalConstraintDeserializer.class) TimeIntervalConstraint timeIntervalConstraint,
            @JsonProperty("recurring") boolean[] recurring
    ) {}

    public static class TravelSerializer extends JsonSerializer<Travel> {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT, new Locale("nl", "be"));

        @Override
        public void serialize(Travel value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("id", value.getIdentifier().toString());
            gen.writeStringField("name", value.getName());
            gen.writeBooleanField("is_arrival_time", value.isArrivalTime());
            gen.writeObjectField("startpoint", value.getStartPoint());
            gen.writeObjectField("endpoint", value.getEndPoint());

            WeekDayConstraint weekDayConstraint = null;
            TimeIntervalConstraint timeIntervalConstraint = null;

            for (TimeConstraint constraint : value.getTimeConstraints()) {
                if (constraint instanceof TimeIntervalConstraint) {
                    timeIntervalConstraint = (TimeIntervalConstraint) constraint;
                } else if (constraint instanceof WeekDayConstraint){
                    weekDayConstraint = (WeekDayConstraint) constraint;
                } else {
                    throw new RuntimeException("Found a TimeConstraint I can't serialize");
                }
            }
            if (weekDayConstraint== null || timeIntervalConstraint == null) {
                throw new RuntimeException("Travel doesn't have enough constraints");
            }

            gen.writeFieldName("recurring");
            gen.writeObject(weekDayConstraint.getRecurring());


            gen.writeArrayFieldStart("time_interval");
            gen.writeString(formatter.format(timeIntervalConstraint.getStarttime()));
            gen.writeString(formatter.format(timeIntervalConstraint.getEndtime()));
            gen.writeEndArray();

            gen.writeEndObject();

        }
    }

    private static class TimeIntervalConstraintDeserializer extends JsonDeserializer<TimeIntervalConstraint> {

        private static final long SECONDS_IN_A_DAY = 86400;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT, new Locale("nl", "be"));


        @Override
        public TimeIntervalConstraint deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String[] times = p.readValueAs(String[].class);

            String starttime = times[0];
            String endtime = times[1];

            LocalTime start = LocalTime.parse(starttime, formatter);
            LocalTime end = LocalTime.parse(endtime, formatter);
            Duration duration;

            if (start == null || end == null) {
                throw new IOException("Not valid timestamp");
            }

            if (start.isAfter(end)) {
                duration = Duration.ofSeconds(SECONDS_IN_A_DAY).minus(Duration.between(end, start));
            } else {
                duration = Duration.between(start, end);
            }

            return new TimeIntervalConstraint(start, duration);
        }
    }
}
