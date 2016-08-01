package be.ugent.vopro.stresstest

import io.gatling.core.Predef._
import io.gatling.http.Predef._

/**
  * Created by anton on 5/9/16.
  */
object CommonRequests {

  val uri = Constants.URI_SHORT

  val loginExec = exec(http("login")
    .post("http://" + uri + ":8080/api/refresh_token/regular")
    .headers(ScenarioHelpers.headersWithToken("NO_TOKEN"))
    .body(StringBody("""${user}"""))
    //match refresh-token
    .check(regex("""[A-Za-z0-9\.\-\_]+""")
    .find(1).saveAs("refresh-token"),
    //match userID
    regex("""[^\"\/][a-z0-9]+-[a-z0-9]+-[a-z0-9]+-[a-z0-9]+-[a-z0-9]+""")
      .find(1).saveAs("userID")
  ))

  val postRefreshTokenExec = exec(http("post_refresh_token")
    .post("http://" + uri + ":8080/api/access_token/")
    .headers(ScenarioHelpers.headersWithToken("${refresh-token}"))
    .body(StringBody("""
                            {"refresh_token":"${refresh-token}"}
                     """))
    .check(regex("""[A-Za-z0-9\.\-\_]{25,}""").saveAs("token"))
  )

}
