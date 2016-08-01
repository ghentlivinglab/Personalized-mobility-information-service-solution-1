package be.ugent.vopro.stresstest

import scala.util.Random

/**
  * Created by anton on 5/8/16.
  */
object EntityGenerators {

  def genNewUser(index : Int): Map[String,Any] ={
    Map("user"->
      s"""
         {"mute_notifications":false,"cell_number":null,"validated":{"email":false,"cell_number":false},
         "email":"stresstest${index}@mail.com","password":"test1234"}
      """)
  }

  def genRegisteredUser(index : Int): Map[String,Any] ={
    Map("user"->
      s"""
      {
        "email": "stresstest${index}@mail.com", "password": "test1234"
      }
      """
    )
  }

  def genPOI(street : String): Map[String, Any] = {
    val houseNr = Random.nextInt(20)+1
    Map("poi"-> s"""
             {"active":true,
             "address":{"city":"Gent","postal_code":"9000","country":"be","street":"${street}","housenumber":"${houseNr}",
             "coordinates":{"lat":51.0396681,"lon":3.727935200000047}},"radius":"250","notify":{},
             "notify_for_event_types":
             [
              {"type":"HAZARD_WEATHER_FLOOD"},{"type":"HAZARD_WEATHER_FOG"},{"type":"HAZARD_WEATHER_FREEZING_RAIN"},{"type":"HAZARD_WEATHER_HAIL"},{"type":"HAZARD_WEATHER_HEAT_WAVE"},{"type":"HAZARD_WEATHER_HEAVY_RAIN"},{"type":"HAZARD_WEATHER_HEAVY_SNOW"},{"type":"HAZARD_WEATHER_HURRICANE"},{"type":"HAZARD_WEATHER_MONSOON"},{"type":"HAZARD_WEATHER_TORNADO"},{"type":"WEATHERHAZARD"},{"type":"HAZARD_ON_ROAD"},{"type":"HAZARD_ON_ROAD_CAR_STOPPED"},{"type":"HAZARD_ON_ROAD_CONSTRUCTION"},{"type":"HAZARD_ON_ROAD_ICE"},{"type":"HAZARD_ON_ROAD_LANE_CLOSED"},{"type":"HAZARD_ON_ROAD_OBJECT"},{"type":"HAZARD_ON_ROAD_OIL"},{"type":"HAZARD_ON_ROAD_POT_HOLE"},{"type":"HAZARD_ON_ROAD_ROAD_KILL"},{"type":"HAZARD_ON_SHOULDER"},{"type":"HAZARD_ON_SHOULDER_ANIMALS"},{"type":"HAZARD_ON_SHOULDER_CAR_STOPPED"},{"type":"HAZARD_ON_SHOULDER_MISSING_SIGN"},{"type":"JAM_HEAVY_TRAFFIC"},{"type":"JAM_LIGHT_TRAFFIC"},{"type":"JAM_MODERATE_TRAFFIC"},{"type":"JAM_STAND_STILL_TRAFFIC"},{"type":"CONSTRUCTION"},{"type":"ROAD_CLOSED_CONSTRUCTION"},{"type":"ROAD_CLOSED_EVENT"},{"type":"ROAD_CLOSED_HAZARD"},{"type":"OTHER"}
             ]
             ,"name":"POI${Random.nextInt()}"}
              """)
  }
}
