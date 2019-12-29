package com.fijimf.deepfoj.user.model

import com.fijimf.deepfij.user.model.PasswordInfo
import io.circe.parser.decode
import io.circe.syntax._
import org.apache.commons.codec.digest.DigestUtils
import org.scalatest.FunSpec

class PasswordInfoSpec extends FunSpec {
  describe("A PasswordInfo ") {
    val pi: PasswordInfo = PasswordInfo("MD5", DigestUtils.md5Hex("_password_1"), Some("salty"))
    val pins: PasswordInfo = PasswordInfo("MD5", DigestUtils.md5Hex("_password_2"), None)
    it("can generate JSON with salt provided") {
      val js: String = pi.asJson.spaces2
      assert(js.contains("\"hasher\""))
      assert(js.contains("\"password\""))
      assert(js.contains("\"salt\""))
    }
    it("can generate JSON with no salt provided") {
      val ks: String = pins.asJson.spaces2
      assert(ks.contains("\"hasher\""))
      assert(ks.contains("\"password\""))
      assert(ks.contains("\"salt\""))
    }
    it("can parse JSON with salt provided") {
      val js: String =
        """{
          |  "hasher" : "MD5",
          |  "password" : "30b87534e7c818088dcff38d3c7af24f",
          |  "salt" : "salty"
          |}
          |""".stripMargin
      decode[PasswordInfo](js) match {
        case Left(thr) => fail(thr)
        case Right(pi) =>
          assert(pi.hasher.toString === "MD5")
          assert(pi.password.toString === "30b87534e7c818088dcff38d3c7af24f")
          assert(pi.salt === Some("salty"))

      }
    }
    it("can parse JSON with no salt provided") {
      val js: String =
        """{
          |  "hasher" : "MD5",
          |  "password" : "e82417ccac5ef7390414bef641c97ba9",
          |  "salt" : null
          |}
          |""".stripMargin
      decode[PasswordInfo](js) match {
        case Left(thr) => fail(thr)
        case Right(pi) =>
          assert(pi.hasher.toString === "MD5")
          assert(pi.password.toString === "e82417ccac5ef7390414bef641c97ba9")
          assert(pi.salt === None)

      }
      val ks: String =
        """{
          |  "hasher" : "MD5",
          |  "password" : "e82417ccac5ef7390414bef641c97ba9"
          |}
          |""".stripMargin
      decode[PasswordInfo](ks) match {
        case Left(thr) => fail(thr)
        case Right(pi) =>
          assert(pi.hasher.toString === "MD5")
          assert(pi.password.toString === "e82417ccac5ef7390414bef641c97ba9")
          assert(pi.salt === None)

      }
    }

    it("can round trip JSON idempotently") {
      decode[PasswordInfo](pi.asJson.spaces2) match {
        case Left(thr) => fail(thr)
        case Right(z) =>
          assert(pi === z)
      }
      decode[PasswordInfo](pins.asJson.spaces2) match {
        case Left(thr) => fail(thr)
        case Right(z) =>
          assert(pins === z)
      }
    }
  }
}
