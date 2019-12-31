package com.fijimf.deepfij.user.model

import java.util.UUID

import io.circe.syntax._
import org.scalatest.FunSpec

class UserInfoSpec extends FunSpec {
  describe("A User ") {
    val u1: User = User(UUID.randomUUID(), "credentials", "fijimf@gmail.com", None, None, None, None, None, false)
    val u2: User = User(UUID.randomUUID(), "credentials", "fijimf@gmail.com", Some("Jim"), Some("Frohnhofer"), None, Some("fijimf@gmail.com"), None, true)
    it("can generate JSON ") {
      val js: String = u1.asJson.spaces2
      println(js)
      //      assert(js.contains("\"hasher\""))
      //      assert(js.contains("\"password\""))
      //      assert(js.contains("\"salt\""))
    }
    //    it("can generate JSON with no salt provided") {
    //      val ks: String = pins.asJson.spaces2
    //      assert(ks.contains("\"hasher\""))
    //      assert(ks.contains("\"password\""))
    //      assert(ks.contains("\"salt\""))
    //    }
    //    it("can parse JSON with salt provided") {
    //      val js: String =
    //        """{
    //          |  "hasher" : "MD5",
    //          |  "password" : "30b87534e7c818088dcff38d3c7af24f",
    //          |  "salt" : "salty"
    //          |}
    //          |""".stripMargin
    //      decode[PasswordInfo](js) match {
    //        case Left(thr) => fail(thr)
    //        case Right(pi) =>
    //          assert(pi.hasher.toString === "MD5")
    //          assert(pi.password.toString === "30b87534e7c818088dcff38d3c7af24f")
    //          assert(pi.salt === Some("salty"))
    //
    //      }
    //    }
    //    it("can parse JSON with no salt provided") {
    //      val js: String =
    //        """{
    //          |  "hasher" : "MD5",
    //          |  "password" : "e82417ccac5ef7390414bef641c97ba9",
    //          |  "salt" : null
    //          |}
    //          |""".stripMargin
    //      decode[PasswordInfo](js) match {
    //        case Left(thr) => fail(thr)
    //        case Right(pi) =>
    //          assert(pi.hasher.toString === "MD5")
    //          assert(pi.password.toString === "e82417ccac5ef7390414bef641c97ba9")
    //          assert(pi.salt === None)
    //
    //      }
    //      val ks: String =
    //        """{
    //          |  "hasher" : "MD5",
    //          |  "password" : "e82417ccac5ef7390414bef641c97ba9"
    //          |}
    //          |""".stripMargin
    //      decode[PasswordInfo](ks) match {
    //        case Left(thr) => fail(thr)
    //        case Right(pi) =>
    //          assert(pi.hasher.toString === "MD5")
    //          assert(pi.password.toString === "e82417ccac5ef7390414bef641c97ba9")
    //          assert(pi.salt === None)
    //
    //      }
    //    }
    //
    //    it("can round trip JSON idempotently") {
    //      decode[PasswordInfo](pi.asJson.spaces2) match {
    //        case Left(thr) => fail(thr)
    //        case Right(z) =>
    //          assert(pi === z)
    //      }
    //      decode[PasswordInfo](pins.asJson.spaces2) match {
    //        case Left(thr) => fail(thr)
    //        case Right(z) =>
    //          assert(pins === z)
    //      }
    //    }
  }
}
