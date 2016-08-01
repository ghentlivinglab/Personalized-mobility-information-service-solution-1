package be.ugent.vopro.stresstest.simulations

import be.ugent.vopro.stresstest.{Constants, ScenarioHelpers}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import ScenarioHelpers._
import be.ugent.vopro.stresstest.CommonRequests._

import scala.util.Random

class POISimulation extends Simulation {

  val uri = Constants.URI_SHORT
  val fullURI = Constants.URI_FULL

  val httpProtocol = http
    .baseURL(fullURI)
    .inferHtmlResources(BlackList(""".*\.css""", """.*\.js""", """.*\.jpg""", """.*\.ico""", """.*maps.*""", """.*spotify.*""", """.*ws.*""", """.*font.*""", """.*cdn.*"""), WhiteList())
    .acceptHeader("application/json, text/plain, */*")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .acceptLanguageHeader("en")
    .userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36")

  val getsToPOIPageExec = exec(http("gets_to_poi_page")
    .options("http://" + uri + ":8080/api/user/${userID}/")
    .headers(headersNoToken)
    //do multiple requests in parallel like a real browser would do
    .resources(
    http("get_user")
      .get("http://" + uri + ":8080/api/user/${userID}/")
      .headers(headersWithToken("${token}")),
    http("get_active_events")
      .get("http://" + uri + ":8080/api/event/?active=true&page=1&user_id=${userID}")
      .headers(headersWithToken("${token}")),
    http("get_travels")
      .get("http://" + uri + ":8080/api/user/${userID}/travel/")
      .headers(headersWithToken("${token}")),
    http("get_pois")
      .get("http://" + uri + ":8080/api/user/${userID}/point_of_interest/")
      .headers(headersWithToken("${token}"))))

  val getEventTypesExec = exec(http("get_eventtypes")
      .get("http://" + uri + ":8080/api/eventtype/")
      .headers(headersWithToken("${token}")))

  val postPOIExec = exec(http("post_poi")
    .post("http://" + uri + ":8080/api/user/${userID}/point_of_interest/")
    .headers(headersWithToken("${token}"))
    .body(StringBody("${poi}")))

  val scn = ScenarioHelpers.createScenarioTwoFeed("CreatePOISimulation",
    createStreetFeeder(),createRegisteredUserFeeder(),
    loginExec,postRefreshTokenExec,getsToPOIPageExec,getEventTypesExec,postPOIExec
  )

  setUp(scn.inject(atOnceUsers(Constants.NR_OF_USERS))).protocols(httpProtocol)
}