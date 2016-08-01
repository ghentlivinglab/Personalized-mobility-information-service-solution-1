/**
 * Created by lukas on 19/04/2016.
 */

angular.module("mobiliteit").filter("translateFilter",function() {
    var map = {
        //transporttypes
        "BIKE": "Fiets",
        "BUS": "Bus",
        "STREETCAR": "Tram",
        "TRAIN": "Trein",
        "CAR": "Auto",
        //eventtypes
        "ACCIDENT_MAJOR": "Groot auto-ongeluk",
        "ACCIDENT_MINOR": "Klein auto-ongeluk",
        "CONSTRUCTION": "Wegenwerken",
        "HAZARD_ON_ROAD": "Gevaar op de weg",
        "HAZARD_ON_ROAD_CAR_STOPPED": "Gevaar wegens gestopte auto",
        "HAZARD_ON_ROAD_CONSTRUCTION": "Gevaar wegens wegenwerken",
        "HAZARD_ON_ROAD_ICE": "Gevaar wegens ijs op de baan",
        "HAZARD_ON_ROAD_LANE_CLOSED": "Gevaar wegens afgesloten rijvak",
        "HAZARD_ON_ROAD_OBJECT": "Gevaar wegens voorwerp op de baan",
        "HAZARD_ON_ROAD_OIL": "Gevaar wegens olie op de baan",
        "HAZARD_ON_ROAD_POT_HOLE": "Gevaar wegens slechte staat van de weg",
        "HAZARD_ON_ROAD_ROAD_KILL": "Gevaar wegens doodgereden dier",
        "HAZARD_ON_SHOULDER": "Gevaar op de berm",
        "HAZARD_ON_SHOULDER_ANIMALS": "Gevaar wegens loslopend dier",
        "HAZARD_ON_SHOULDER_CAR_STOPPED": "Gevaar wegens gestopte auto op de berm",
        "HAZARD_ON_SHOULDER_MISSING_SIGN": "Gevaar wegens ontbrekend verkeersbord",
        "HAZARD_WEATHER_FLOOD": "Gevaar wegens overstroming",
        "HAZARD_WEATHER_FOG": "Gevaar wegens mist",
        "HAZARD_WEATHER_FREEZING_RAIN": "Gevaar wegens aanvriezende regen",
        "HAZARD_WEATHER_HAIL": "Gevaar wegens hagel",
        "HAZARD_WEATHER_HEAT_WAVE": "Gevaar wegens hittegolf",
        "HAZARD_WEATHER_HEAVY_RAIN": "Gevaar wegens hevige regen",
        "HAZARD_WEATHER_HEAVY_SNOW": "Gevaar wegens hevige sneeuwbui",
        "HAZARD_WEATHER_HURRICANE": "Gevaar wegens een orkaan",
        "HAZARD_WEATHER_MONSOON": "Gevaar wegens de moesson",
        "HAZARD_WEATHER_TORNADO": "Gevaar wegens een orkaan",
        "JAM": "File",
        "JAM_HEAVY_TRAFFIC": "Zware file",
        "JAM_LIGHT_TRAFFIC": "Lichte file",
        "JAM_MODERATE_TRAFFIC": "Gematigde file",
        "JAM_STAND_STILL_TRAFFIC": "Stilstaand verkeer",
        "OTHER": "Overige",
        "ROAD_CLOSED": "Weg afgesloten",
        "ROAD_CLOSED_CONSTRUCTION": "Weg afgesloten wegens wegenwerken",
        "ROAD_CLOSED_EVENT": "Weg afgesloten wegens een evenement",
        "ROAD_CLOSED_HAZARD": "Weg afgesloten wegens gevaar",
        "WEATHERHAZARD": "Weergevaar"
    };

    return function (word){
            if (map[word]) {
                return map[word];
            }
    };
});
