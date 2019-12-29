package com.fijimf.deepfoj.user.model

import java.util.UUID

import com.fijimf.deepfij.user.model.AuthToken
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
}
