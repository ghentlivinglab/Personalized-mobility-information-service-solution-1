package be.ugent.vopro.stresstest

import io.gatling.core.feeder.{FeederBuilder, RecordSeqFeederBuilder}
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import EntityGenerators._
import play.api.libs.json._

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random
import play.api.libs.functional.syntax._

/**
  * Created by anton on 5/8/16.
  */
object ScenarioHelpers {

  def createScenario(name: String, feed: FeederBuilder[_], chains: ChainBuilder*): ScenarioBuilder = {
      scenario(name).feed(feed).exec(chains)
  }

  def createScenarioTwoFeed(name: String, feedA: FeederBuilder[_], feedB: FeederBuilder[_], chains: ChainBuilder*): ScenarioBuilder = {
    scenario(name).feed(feedA).feed(feedB).exec(chains)
  }
  def createScenarioThreeFeed(name: String, feedA: FeederBuilder[_], feedB: FeederBuilder[_],
                              feedC: FeederBuilder[_],chains: ChainBuilder*): ScenarioBuilder = {
    scenario(name).feed(feedA).feed(feedB).feed(feedC).exec(chains)
  }

  def createStreetFeeder(): RecordSeqFeederBuilder[Any] = {
    val fileURL = getClass.getResource("/stratenGent.txt")
    val fileSource = scala.io.Source.fromURL(fileURL)
    val randomPOIs = fileSource.getLines().map(genPOI(_)).toIndexedSeq

    randomPOIs.random
  }

  def createNewUserFeeder(): RecordSeqFeederBuilder[Any] = {
    createGeneralUserPreFeeder(genNewUser)
  }

  def createRegisteredUserFeeder(): RecordSeqFeederBuilder[Any] = {
    createGeneralUserPreFeeder(genRegisteredUser)
  }

  def createGeneralUserPreFeeder(userJSONFromIndex:Int => Map[String,Any]): RecordSeqFeederBuilder[Any] = {
    val userList = (0 until Constants.NR_OF_USERS).map(userJSONFromIndex(_))
    val userFeeder = userList.queue
    userFeeder
  }

  case class Tup(travel: JsValue, route: JsValue)

  implicit val locationReads: Reads[Tup] = (
    (JsPath \\ "travel").read[JsValue] and
      (JsPath \\ "route").read[JsValue]
    )(Tup.apply _)

  def createTravelRouteFeeder() : RecordSeqFeederBuilder[Any] = {
    val fileStream = getClass.getResourceAsStream("/TravelRouteFeeder.json")

    val travelRouteJSON : List[Tup] = Json.parse(fileStream).as[JsArray].as[List[Tup]]

    val arrayBuf = ArrayBuffer[Map[String, Any]]()
    travelRouteJSON.foreach(elt => {
      arrayBuf += Map(
        "travel" -> elt.travel.toString(),
        "route" -> elt.route.toString()
      )
    })
    arrayBuf.toIndexedSeq.random
  }

  val headersNoToken = Map(
    "Accept" -> "*/*",
    "Access-Control-Request-Headers" -> "accept, authorization, content-type",
    "Origin" -> Constants.URI_FULL)

  def headersWithToken(token : String) = Map(
    "Accept" -> "*/*",
    "Authorization" -> s"Bearer ${token}",
    "Content-Type" -> "application/json;charset=UTF-8",
    "Origin" -> Constants.URI_FULL)
}
