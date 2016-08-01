package mobiliteit

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://192.168.0.100:8080")
		.inferHtmlResources(BlackList(""".*\.css""", """.*\.js""", """.*\.ico""", """.*maps.*""", """.*\/ws\/.*"""), WhiteList())
		.acceptHeader("application/json, text/plain, */*")
		.acceptEncodingHeader("gzip, deflate, sdch")
		.acceptLanguageHeader("en")
		.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Accept" -> "text/html",
		"Authorization" -> "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkMGNhYjU0Mi02ZDEwLTQyZWEtODkzOC0xNmQwOWEyNDdkZmEifQ.3nQeybdYzu9am38qFFrunka2ZKoX_tclQmLeM8YqDE7TQC6p0K946C2XcyddDeWcaMZ2sTL-pDHgc04oV8pEsw")

	val headers_2 = Map("Accept" -> "image/webp,image/*,*/*;q=0.8")

	val headers_3 = Map(
		"Accept" -> "*/*",
		"Origin" -> "http://192.168.0.100")

	val headers_5 = Map("Authorization" -> "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkMGNhYjU0Mi02ZDEwLTQyZWEtODkzOC0xNmQwOWEyNDdkZmEifQ.3nQeybdYzu9am38qFFrunka2ZKoX_tclQmLeM8YqDE7TQC6p0K946C2XcyddDeWcaMZ2sTL-pDHgc04oV8pEsw")

	val headers_6 = Map(
		"Accept" -> "*/*",
		"Access-Control-Request-Headers" -> "accept, authorization, content-type",
		"Access-Control-Request-Method" -> "POST",
		"Origin" -> "http://192.168.0.100")

	val headers_7 = Map(
		"Accept-Encoding" -> "gzip, deflate",
		"Authorization" -> "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkMGNhYjU0Mi02ZDEwLTQyZWEtODkzOC0xNmQwOWEyNDdkZmEifQ.3nQeybdYzu9am38qFFrunka2ZKoX_tclQmLeM8YqDE7TQC6p0K946C2XcyddDeWcaMZ2sTL-pDHgc04oV8pEsw",
		"Content-Type" -> "application/json;charset=UTF-8",
		"Origin" -> "http://192.168.0.100")

	val headers_11 = Map(
		"Accept-Encoding" -> "gzip, deflate",
		"Authorization" -> "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwM2U5ZDZkYi0zM2JjLTQyMjEtOGE3OS0wNjYxMjgwZWQ5MmMifQ.S_ORa2H__EPkBR_Oo06_JxOVwG14dnfCt8GQ-dpasZXV7fzL_RriL86-dxWd_6wAvzNUairsZlL-6LEKhAP47A",
		"Content-Type" -> "application/json;charset=UTF-8",
		"Origin" -> "http://192.168.0.100")

	val headers_12 = Map(
		"Accept" -> "*/*",
		"Access-Control-Request-Headers" -> "accept, authorization, content-type",
		"Access-Control-Request-Method" -> "PUT",
		"Origin" -> "http://192.168.0.100")

	val headers_13 = Map(
		"Authorization" -> "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwM2U5ZDZkYi0zM2JjLTQyMjEtOGE3OS0wNjYxMjgwZWQ5MmMifQ.S_ORa2H__EPkBR_Oo06_JxOVwG14dnfCt8GQ-dpasZXV7fzL_RriL86-dxWd_6wAvzNUairsZlL-6LEKhAP47A",
		"Content-Type" -> "application/json;charset=UTF-8",
		"Origin" -> "http://192.168.0.100")

	val headers_14 = Map(
		"Accept" -> "text/html",
		"Authorization" -> "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwM2U5ZDZkYi0zM2JjLTQyMjEtOGE3OS0wNjYxMjgwZWQ5MmMifQ.S_ORa2H__EPkBR_Oo06_JxOVwG14dnfCt8GQ-dpasZXV7fzL_RriL86-dxWd_6wAvzNUairsZlL-6LEKhAP47A")

	val headers_15 = Map(
		"Accept" -> "*/*",
		"Access-Control-Request-Headers" -> "accept, authorization",
		"Access-Control-Request-Method" -> "GET",
		"Origin" -> "http://192.168.0.100")

	val headers_16 = Map("Authorization" -> "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwM2U5ZDZkYi0zM2JjLTQyMjEtOGE3OS0wNjYxMjgwZWQ5MmMifQ.S_ORa2H__EPkBR_Oo06_JxOVwG14dnfCt8GQ-dpasZXV7fzL_RriL86-dxWd_6wAvzNUairsZlL-6LEKhAP47A")

	val headers_17 = Map(
		"Authorization" -> "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwM2U5ZDZkYi0zM2JjLTQyMjEtOGE3OS0wNjYxMjgwZWQ5MmMifQ.S_ORa2H__EPkBR_Oo06_JxOVwG14dnfCt8GQ-dpasZXV7fzL_RriL86-dxWd_6wAvzNUairsZlL-6LEKhAP47A",
		"Origin" -> "http://192.168.0.100")

    val uri1 = "192.168.0.100"
    val uri2 = "www.google.com"
    val uri3 = "maps.googleapis.com"

	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("http://" + uri1 + "/")
			.headers(headers_0)
			.resources(http("request_1")
			.get("http://" + uri1 + "/app/components/index/indexView.html")
			.headers(headers_1),
            http("request_2")
			.get("http://" + uri1 + "/assets/img/indexBackground.jpg")
			.headers(headers_2)
			.check(status.is(304)),
            http("request_3")
			.get("http://" + uri1 + "/assets/fonts/fontawesome-webfont.woff2?v=4.5.0")
			.headers(headers_3)
			.check(status.is(304))))
		.pause(2)
		.exec(http("request_4")
			.get("http://" + uri1 + "/app/components/signup/signUpView.html")
			.headers(headers_1)
			.resources(http("request_5")
			.get("http://" + uri1 + "/app/common/directives/editUserProfileForm/editUserProfileFormView.html")
			.headers(headers_5)))
		.pause(28)
		.exec(http("request_6")
			.options("/api/user/")
			.headers(headers_6)
			.resources(http("request_7")
			.post("http://" + uri1 + ":8080/api/user/")
			.headers(headers_7)
			.body(RawFileBody("RecordedSimulation_0007_request.txt")),
            http("request_8")
			.options("http://" + uri1 + ":8080/api/refresh_token/regular")
			.headers(headers_6),
            http("request_9")
			.post("http://" + uri1 + ":8080/api/refresh_token/regular")
			.headers(headers_7)
			.body(RawFileBody("RecordedSimulation_0009_request.txt")),
            http("request_10")
			.options("http://" + uri1 + ":8080/api/access_token/")
			.headers(headers_6),
            http("request_11")
			.post("http://" + uri1 + ":8080/api/access_token/")
			.headers(headers_11)
			.body(RawFileBody("RecordedSimulation_0011_request.txt"))))
		.pause(5)
		.exec(http("request_12")
			.options("/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/verify/")
			.headers(headers_12)
			.resources(http("request_13")
			.put("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/verify/")
			.headers(headers_13)
			.body(RawFileBody("RecordedSimulation_0013_request.txt")),
            http("request_14")
			.get("http://" + uri1 + "/app/components/setup/setupView.html")
			.headers(headers_14),
            http("request_15")
			.options("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/")
			.headers(headers_15),
            http("request_16")
			.get("http://" + uri1 + "/app/common/directives/editPointOfInterestForm/editPointOfInterestFormView.html")
			.headers(headers_16),
            http("request_17")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/")
			.headers(headers_17),
            http("request_18")
			.get("http://" + uri1 + "/app/common/directives/editAddressForm/editAddressFormView.html")
			.headers(headers_16),
            http("request_19")
			.options("http://" + uri1 + ":8080/api/eventtype/")
			.headers(headers_15),
            http("request_20")
			.get("http://" + uri1 + ":8080/api/eventtype/")
			.headers(headers_17)))
		.pause(20)
		.exec(http("request_21")
			.options("/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/point_of_interest/")
			.headers(headers_6)
			.resources(http("request_22")
			.post("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/point_of_interest/")
			.headers(headers_11)
			.body(RawFileBody("RecordedSimulation_0022_request.txt"))))
		.pause(1)
		.exec(http("request_23")
			.get("http://" + uri1 + "/app/components/app.html")
			.headers(headers_14)
			.resources(http("request_24")
			.get("http://" + uri1 + "/app/components/home/homeView.html")
			.headers(headers_14),
            http("request_25")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/")
			.headers(headers_17),
            http("request_26")
			.options("http://" + uri1 + ":8080/api/event/?active=true&page=1&user_id=03e9d6db-33bc-4221-8a79-0661280ed92c")
			.headers(headers_15),
            http("request_27")
			.options("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/")
			.headers(headers_15),
            http("request_28")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/point_of_interest/")
			.headers(headers_17),
            http("request_29")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/")
			.headers(headers_17),
            http("request_30")
			.get("http://" + uri1 + ":8080/api/event/?active=true&page=1&user_id=03e9d6db-33bc-4221-8a79-0661280ed92c")
			.headers(headers_17)))
		.pause(24)
		.exec(http("request_31")
			.get("http://" + uri1 + "/app/components/travels/travelIndexView.html")
			.headers(headers_14)
			.resources(http("request_32")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/")
			.headers(headers_17),
            http("request_33")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/")
			.headers(headers_17)))
		.pause(2)
		.exec(http("request_34")
			.get("http://" + uri1 + "/app/components/addresses/addressIndexView.html")
			.headers(headers_14)
			.resources(http("request_35")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/")
			.headers(headers_17),
            http("request_36")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/point_of_interest/")
			.headers(headers_17)))
		.pause(2)
		.exec(http("request_37")
			.get("/api/eventtype/")
			.headers(headers_17))
		.pause(27)
		.exec(http("request_38")
			.post("/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/point_of_interest/")
			.headers(headers_11)
			.body(RawFileBody("RecordedSimulation_0038_request.txt")))
		.pause(30)
		.exec(http("request_39")
			.get("/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/")
			.headers(headers_17)
			.resources(http("request_40")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/")
			.headers(headers_17)))
		.pause(22)
		.exec(http("request_41")
			.get("http://" + uri1 + "/app/common/templates/editTravelModal/editTravelModalView.html")
			.headers(headers_16)
			.resources(http("request_42")
			.options("http://" + uri1 + ":8080/api/transportationtype/")
			.headers(headers_15),
            http("request_43")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/")
			.headers(headers_17),
            http("request_44")
			.get("http://" + uri1 + ":8080/api/transportationtype/")
			.headers(headers_17),
            http("request_45")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/point_of_interest/")
			.headers(headers_17)))
		.pause(52)
		.exec(http("request_46")
			.options("/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/")
			.headers(headers_6)
			.resources(http("request_47")
			.get("http://" + uri1 + ":8080/api/transportationtype/")
			.headers(headers_17),
            http("request_48")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/")
			.headers(headers_17),
            http("request_49")
			.post("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/")
			.headers(headers_11)
			.body(RawFileBody("RecordedSimulation_0049_request.txt")),
            http("request_50")
			.get("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/point_of_interest/")
			.headers(headers_17),
            http("request_51")
			.options("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/d54c1b70-64ef-45a0-91c1-2aa82c9f66f9/route/")
			.headers(headers_6),
            http("request_52")
			.post("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/d54c1b70-64ef-45a0-91c1-2aa82c9f66f9/route/")
			.headers(headers_11)
			.body(RawFileBody("RecordedSimulation_0052_request.txt"))))
		.pause(37)
		.exec(http("request_53")
			.post("/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/")
			.headers(headers_11)
			.body(RawFileBody("RecordedSimulation_0053_request.txt"))
			.resources(http("request_54")
			.options("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/962caf95-8e49-434b-8f25-21d00da65e5d/route/")
			.headers(headers_6),
            http("request_55")
			.post("http://" + uri1 + ":8080/api/user/03e9d6db-33bc-4221-8a79-0661280ed92c/travel/962caf95-8e49-434b-8f25-21d00da65e5d/route/")
			.headers(headers_11)
			.body(RawFileBody("RecordedSimulation_0055_request.txt"))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}