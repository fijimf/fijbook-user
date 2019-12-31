package com.fijimf.deepfij.user

import java.util.UUID

import cats.effect.IO
import com.fijimf.deepfij.user.model._
import com.fijimf.deepfij.user.services.UserRepo
import doobie.implicits._
import org.apache.commons.codec.digest.DigestUtils
import org.joda.time.DateTime

class UserRepoSpec extends DbIntegrationSpec {
  val containerName = "user-repo-spec"
  val port = "7315"

  describe("User repo ops") {
    val repo = new UserRepo[IO](transactor)

    describe("AuthToken ops") {
      it("should save an authToken") {
        val id: UUID = UUID.randomUUID()
        val userId: UUID = UUID.randomUUID()
        (for {
          _ <- UserDao.truncateAuthToken().run.transact(transactor)
          tok <- repo.saveToken(AuthToken(id, userId, DateTime.now()))
        } yield {
          assert(tok.id === id)
          assert(tok.userId === userId)
        }).unsafeRunSync()
      }

      it("should find an authToken") {
        val id: UUID = UUID.randomUUID()
        val badId: UUID = UUID.randomUUID()
        val userId: UUID = UUID.randomUUID()
        (for {
          _ <- UserDao.truncateAuthToken().run.transact(transactor)
          tok <- repo.saveToken(AuthToken(id, userId, DateTime.now()))
          find <- repo.getToken(id)
          missing <- repo.getToken(badId)
        } yield {
          assert(tok.id === id)
          assert(tok.userId === userId)
          assert(find === Some(tok))
          assert(missing === None)
        }).unsafeRunSync()
      }

      it("should delete an authToken") {
        val id: UUID = UUID.randomUUID()
        val badId: UUID = UUID.randomUUID()
        val userId: UUID = UUID.randomUUID()
        (for {
          _ <- UserDao.truncateAuthToken().run.transact(transactor)
          tok <- repo.saveToken(AuthToken(id, userId, DateTime.now()))
          before <- repo.getToken(id)
          count <- repo.deleteToken(id)
          after <- repo.getToken(id)
          badCount <- repo.deleteToken(badId)
        } yield {
          assert(tok.id === id)
          assert(tok.userId === userId)
          assert(before === Some(tok))
          assert(count === 1)
          assert(after === None)
          assert(badCount === 0)
        }).unsafeRunSync()
      }


      it("should find all expired tokens") {
        val now: DateTime = DateTime.now()
        (for {
          _ <- UserDao.truncateAuthToken().run.transact(transactor)
          _ <- repo.saveToken(AuthToken(UUID.randomUUID(), UUID.randomUUID(), now))
          _ <- repo.saveToken(AuthToken(UUID.randomUUID(), UUID.randomUUID(), now.plusMillis(10000)))
          _ <- repo.saveToken(AuthToken(UUID.randomUUID(), UUID.randomUUID(), now.plusMillis(20000)))
          _ <- repo.saveToken(AuthToken(UUID.randomUUID(), UUID.randomUUID(), now.plusMillis(30000)))
          l1 <- repo.getExpiredTokens(now.getMillis - 1)
          l2 <- repo.getExpiredTokens(now.getMillis + 9999)
          l3 <- repo.getExpiredTokens(now.getMillis + 19999)
          l4 <- repo.getExpiredTokens(now.getMillis + 29999)
          l5 <- repo.getExpiredTokens(now.getMillis + 39999)
        } yield {
          assert(l1.size === 4)
          assert(l2.size === 3)
          assert(l3.size === 2)
          assert(l4.size === 1)
          assert(l5.size === 0)
        }).unsafeRunSync()
      }
    }

    describe("PasswordInfo ops") {
      it("should save a passwordInfo") {
        val u: User = User(UUID.randomUUID(), "credentials", "fijimf@gmail.com", Some("Jim"), Some("Frohnhofer"), None, Some("fijimf@gmail.com"), None, true)

        val pi = PasswordInfo("MD5", DigestUtils.md5Hex("password" + "salty"), Some("salty"))
        (for {
          _ <- UserDao.truncatePasswordInfo().run.transact(transactor)
          _ <- UserDao.truncateUser().run.transact(transactor)
          _ <- repo.saveUser(u)
          p <- repo.savePasswordInfo("credentials", "fijimf@gmail.com", pi)
        } yield {
          assert (p===pi)
        }).unsafeRunSync()
      }

    it("should fail to save a passwordInfo if the user doesn't exist") {
        val pi = PasswordInfo("MD5", DigestUtils.md5Hex("password" + "salty"), Some("salty"))
        (for {
          _ <- UserDao.truncatePasswordInfo().run.transact(transactor)
          _ <- UserDao.truncateUser().run.transact(transactor)
          a <- repo.savePasswordInfo("credentials", "fijimf@gmail.com", pi)
        } yield {
          a
        })
          .attempt
          .unsafeRunSync() match {
          case Left(thr) => //Ok
          case Right(passwordInfo) => fail("Should not save")
        }
      }
    }
    describe("User ops") {
      it("should save a user") {
        val u: User = User(UUID.randomUUID(), "credentials", "fijimf@gmail.com", Some("Jim"), Some("Frohnhofer"), None, Some("fijimf@gmail.com"), None, true)

        (for {
          _ <- UserDao.truncateUser().run.transact(transactor)
          a <- repo.saveUser(u)
        } yield {
          assert(a === u)
        }).unsafeRunSync()
      }

    }
  }
}

