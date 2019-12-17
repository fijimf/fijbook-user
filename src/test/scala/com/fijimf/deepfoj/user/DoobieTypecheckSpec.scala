package com.fijimf.deepfoj.user

import java.util.UUID

import com.fijimf.deepfij.user.model.{AuthToken, PasswordInfo, User, UserDao}
import org.joda.time.DateTime

class DoobieTypecheckSpec extends DbIntegrationSpec {
  val containerName = "doobie-typecheck-spec"
  val port = "7379"

  describe("Doobie typechecking Dao's") {
    describe("UserDao") {
      it("getPasswordInfo should typecheck") {
        check(UserDao.getPasswordInfo("userPassword", "fijimf@gmail.com"))
      }

      it("savePasswordInfo should typecheck") {
        check(UserDao.savePasswordInfo("userPassword", "fijimf@gmail.com", PasswordInfo("MD5", "4%defq900weW", Some("fijimf"))))
        check(UserDao.savePasswordInfo("userPassword", "fijimf@gmail.com", PasswordInfo("MD5", "4%defq900weW", None)))
      }

      it("deletePasswordInfo should typecheck") {
        check(UserDao.deletePasswordInfo("userPassword", "fijimf@gmail.com"))
      }

      it("getToken should typecheck") {
        check(UserDao.getToken(UUID.randomUUID()))
      }

      it("getExpired should typecheck") {
        check(UserDao.getExpired(DateTime.now().getMillis))
      }

      it("saveToken should typecheck") {
        check(UserDao.saveToken(AuthToken(UUID.randomUUID(), UUID.randomUUID(), DateTime.now())))
      }

      it("deleteToken should typecheck") {
        check(UserDao.deleteToken(UUID.randomUUID()))
      }

      it("getUserByLoginInfo should typecheck") {
        check(UserDao.getUserByLoginInfo("email", "fijimf@gmail.com"))
      }

      it("getUserByUUID should typecheck") {
        check(UserDao.getUserByUUID(UUID.randomUUID()))
      }

      it("saveUser should typecheck") {
        check(UserDao.saveUser(User(UUID.randomUUID(), "email", "fijimf@gmail.com", None, None, None, Some("fijimf@gmail.com"), None, true)))
        check(UserDao.saveUser(User(UUID.randomUUID(), "email", "fijimf@gmail.com", None, None, None, Some("fijimf@gmail.com"), None, false)))
        check(UserDao.saveUser(User(UUID.randomUUID(), "email", "fijimf@gmail.com", Some("Jim"), None, None, None, None, false)))
      }
    }
  }
}
