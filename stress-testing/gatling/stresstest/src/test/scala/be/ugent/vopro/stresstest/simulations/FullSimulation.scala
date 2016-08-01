package be.ugent.vopro.stresstest.simulations


import be.ugent.vopro.stresstest.CommonRequests._
import be.ugent.vopro.stresstest.ScenarioHelpers._
import be.ugent.vopro.stresstest.{Constants, ScenarioHelpers}

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class FullSimulation extends Simulation {

	val uri = Constants.URI_SHORT
	val fullURI = Constants.URI_FULL

	val httpProtocol = http
		.baseURL(fullURI)
		.inferHtmlResources(BlackList(""".*\.css""", """.*\.js""", """.*\.html""", """.*maps.*""", """.*font.*""", """.*cdn.*""", """.*spotify.*""", """.*ws.*""", """.*\.jpg"""), WhiteList())
		.acceptHeader("application/json, text/plain, */*")
		.acceptEncodingHeader("gzip, deflate, sdch")
		.acceptLanguageHeader("en")
		.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36")

	val preRouteGetsExec = exec(http("pre_route_gets")
		.options("/api/refresh_token/regular")
		.headers(headersNoToken)
		.resources(
			http("get_user")
				.get("http://" + uri + ":8080/api/user/${userID}/")
				.headers(headersWithToken("${token}")),
			http("get_active_events")
				.get("http://" + uri + ":8080/api/event/?active=true&page=1&user_id=${userID}")
				.headers(headersWithToken("${token}")),
			http("get_travels_for_user")
				.get("http://" + uri + ":8080/api/user/${userID}/travel/")
				.headers(headersWithToken("${token}")),
			http("get_pois_for_user_1")
				.get("http://" + uri + ":8080/api/user/${userID}/point_of_interest/")
				.headers(headersWithToken("${token}")),
      http("get_transportationtype")
        .get("http://" + uri + ":8080/api/transportationtype/")
        .headers(headersWithToken("${token}"))))

  val postTravelExec = exec(
      http("post_travel_1")
        .post("http://" + uri + ":8080/api/user/${userID}/travel/")
        .headers(headersWithToken("${token}"))
        .body(StringBody("${travel}"))
				.check(jsonPath("$.id").saveAs("travelID"))
  )

  val postRouteExec = exec(http("post_route_1")
    .post("http://" + uri + ":8080/api/user/${userID}/travel/${travelID}/route/")
    .headers(headersWithToken("${token}"))
    .body(StringBody("${route}")))

	val scn = ScenarioHelpers.createScenarioTwoFeed("FullSimulation",
		createTravelRouteFeeder(),createRegisteredUserFeeder(),
		loginExec,postRefreshTokenExec,preRouteGetsExec,postTravelExec,postRouteExec
	)

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}