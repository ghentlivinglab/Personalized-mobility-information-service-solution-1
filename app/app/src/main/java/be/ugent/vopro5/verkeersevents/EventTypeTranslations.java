package be.ugent.vopro5.verkeersevents;

public enum EventTypeTranslations {
    ACCIDENT_MAJOR(R.string.accident_major),
    ACCIDENT_MINOR(R.string.accident_minor),
    CONSTRUCTION(R.string.construction),
    HAZARD_ON_ROAD(R.string.hazard_on_road),
    HAZARD_ON_ROAD_CAR_STOPPED(R.string.hazard_on_road_car_stopped),
    HAZARD_ON_ROAD_CONSTRUCTION(R.string.hazard_on_road_construction),
    HAZARD_ON_ROAD_ICE(R.string.hazard_on_road_ice),
    HAZARD_ON_ROAD_LANE_CLOSED(R.string.hazard_on_road_lane_closed),
    HAZARD_ON_ROAD_OBJECT(R.string.hazard_on_road_object),
    HAZARD_ON_ROAD_OIL(R.string.hazard_on_road_oil),
    HAZARD_ON_ROAD_POT_HOLE(R.string.hazard_on_road_pot_hole),
    HAZARD_ON_ROAD_ROAD_KILL(R.string.hazard_on_road_road_kill),
    HAZARD_ON_SHOULDER(R.string.hazard_on_shoulder),
    HAZARD_ON_SHOULDER_ANIMALS(R.string.hazard_on_shoulder_animals),
    HAZARD_ON_SHOULDER_CAR_STOPPED(R.string.hazard_on_shoulder_car_stopped),
    HAZARD_ON_SHOULDER_MISSING_SIGN(R.string.hazard_on_shoulder_missing_sign),
    HAZARD_WEATHER_FLOOD(R.string.hazard_weather_flood),
    HAZARD_WEATHER_FOG(R.string.hazard_weather_fog),
    HAZARD_WEATHER_FREEZING_RAIN(R.string.hazard_weather_freezing_rain),
    HAZARD_WEATHER_HAIL(R.string.hazard_weather_hail),
    HAZARD_WEATHER_HEAT_WAVE(R.string.hazard_weather_heat_wave),
    HAZARD_WEATHER_HEAVY_RAIN(R.string.hazard_weather_heavy_rain),
    HAZARD_WEATHER_HEAVY_SNOW(R.string.hazard_weather_heavy_snow),
    HAZARD_WEATHER_HURRICANE(R.string.hazard_weather_hurricane),
    HAZARD_WEATHER_MONSOON(R.string.hazard_weather_monsoon),
    HAZARD_WEATHER_TORNADO(R.string.hazard_weather_tornado),
    JAM(R.string.jam),
    JAM_HEAVY_TRAFFIC(R.string.jam_heavy_traffic),
    JAM_LIGHT_TRAFFIC(R.string.jam_light_traffic),
    JAM_MODERATE_TRAFFIC(R.string.jam_moderate_traffic),
    JAM_STAND_STILL_TRAFFIC(R.string.jam_stand_still_traffic),
    OTHER(R.string.other),
    ROAD_CLOSED(R.string.road_closed),
    ROAD_CLOSED_CONSTRUCTION(R.string.road_closed_construction),
    ROAD_CLOSED_EVENT(R.string.road_closed_event),
    ROAD_CLOSED_HAZARD(R.string.road_closed_hazard),
    WEATHERHAZARD(R.string.weatherhazard);

    private int stringResource;

    EventTypeTranslations(int stringResource) {
        this.stringResource = stringResource;
    }

    public int getStringResource() {
        return stringResource;
    }
}
