package com.fijimf.deepfij.user.model

import java.util.UUID

import io.circe.parser.decode
import io.circe.syntax._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.scalatest.FunSpec

class AuthTokenSpec extends FunSpec {
  describe("An AuthToken ") {
    val authToken: AuthToken = AuthToken(UUID.randomUUID(), UUID.randomUUID(), new DateTime())
    it("can generate JSON ") {
      val js: String = authToken.asJson.spaces2
      assert(js.contains("\"id\""))
      assert(js.contains("\"userId\""))
      assert(js.contains("\"expiry\""))
    }
    it("can parse JSON ") {
      val js: String =
        """
          |{
          |  "id" : "38079249-3594-4e3d-98aa-410efb6647ab",
          |  "userId" : "49658509-24d2-4d18-ae61-eb31717b24dc",
          |  "expiry" : "2019-12-28 18:36:04.255"
          |}
          |""".stripMargin
      decode[AuthToken](js) match {
        case Left(thr) => fail(thr)
        case Right(authToken) =>
          assert(authToken.id.toString === "38079249-3594-4e3d-98aa-410efb6647ab")
          assert(authToken.userId.toString === "49658509-24d2-4d18-ae61-eb31717b24dc")
          assert(authToken.expiry === DateTime.parse("2019-12-28 18:36:04.255", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")))

      }

    }

    it("can round trip JSON idempotently") {
      decode[AuthToken](authToken.asJson.spaces2) match {
        case Left(thr) => fail(thr)
        case Right(z) =>
          assert(authToken === z)
      }
    }

  }

  describe("An option of authToken") {
    val some: Option[AuthToken] = Some(AuthToken(UUID.randomUUID(), UUID.randomUUID(), new DateTime()))
    val none: Option[AuthToken] = Option.empty[AuthToken]
    it(" can generate JSON") {
      val js: String = some.asJson.spaces2
      println(js)
      val ks: String = none.asJson.spaces2
      println(ks)
    }

    it("can parse JSON ") {
      val js: String =
        """{
          |  "id" : "e2405681-cecd-4284-96be-372d0c35d908",
          |  "userId" : "a6cf644b-fa9f-498a-8049-42493c9426d1",
          |  "expiry" : "2019-12-29 01:24:25.308"
          |}
          |""".stripMargin
      decode[Option[AuthToken]](js) match {
        case Left(thr) => fail(thr)
        case Right(Some(authToken)) =>
          assert(authToken.id.toString === "e2405681-cecd-4284-96be-372d0c35d908")
          assert(authToken.userId.toString === "a6cf644b-fa9f-498a-8049-42493c9426d1")
          assert(authToken.expiry === DateTime.parse("2019-12-29 01:24:25.308", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")))
        case Right(_) => fail()
      }
      val ks: String = """null"""
      decode[Option[AuthToken]](ks) match {
        case Left(thr) => fail(thr)
        case Right(None) => //OK
        case Right(_) => fail()
      }

    }


    it("can round trip JSON idempotently") {
      decode[Option[AuthToken]](some.asJson.spaces2) match {
        case Left(thr) => fail(thr)
        case Right(z) =>
          assert(some === z)
      }
      decode[Option[AuthToken]](none.asJson.spaces2) match {
        case Left(thr) => fail(thr)
        case Right(z) =>
          assert(none === z)
      }
    }

  }
}
