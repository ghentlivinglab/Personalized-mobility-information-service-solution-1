package be.ugent.vopro.stresstest.simulations

import be.ugent.vopro.stresstest.Constants
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import be.ugent.vopro.stresstest.ScenarioHelpers._

/**
  * Created by anton on 5/5/16.
  */
class RegisterUsers extends Simulation{

  val amountOfUsers = Constants.NR_OF_USERS

  val uri = Constants.URI_SHORT
  val fullURI = Constants.URI_FULL

  val httpProtocol = http
    .baseURL(fullURI)
    .inferHtmlResources(BlackList(""".*\.css""", """.*\.js""", """.*\.jpg""", """.*\.ico""", """.*maps.*""", """.*spotify.*""", """.*ws.*""", """.*font.*""", """.*cdn.*"""), WhiteList())
    .acceptHeader("application/json, text/plain, */*")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .acceptLanguageHeader("en")
    .userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36")

  val postUserExec = exec(http("post_user")
      .post("http://" + uri + ":8080/api/user/")
      .headers(headersWithToken("WONT_BE_VERIFIED"))
      .body(StringBody("""${user}""")))

  val scn = createScenario("RegisterUsersSimulation",createNewUserFeeder(),postUserExec)

  setUp(scn.inject(atOnceUsers(amountOfUsers))).protocols(httpProtocol)
}
