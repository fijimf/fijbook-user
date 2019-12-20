package com.fijimf.deepfij.user.services

import java.util.UUID

import cats.MonadError
import cats.effect.Sync
import com.fijimf.deepfij.user.model.{AuthToken, PasswordInfo, User, UserDao}
import doobie.implicits._
import doobie.util.transactor.Transactor

class UserRepo[F[_] : Sync](xa: Transactor[F]) {

  val me = implicitly[MonadError[F, Throwable]]

  def healthcheck: F[Boolean] = {
    doobie.FC.isValid(2 /*timeout in seconds*/).transact(xa)
  }

  def getPasswordInfo(id: String, key: String): F[Option[PasswordInfo]] = {
    UserDao.getPasswordInfo(id, key)
      .option
      .transact(xa)
      .exceptSql(ex => me.raiseError[Option[PasswordInfo]](ex))
  }

  def savePasswordInfo(id: String, key: String, passwordInfo: PasswordInfo): F[PasswordInfo] = {
    UserDao.savePasswordInfo(id, key, passwordInfo)
      .withUniqueGeneratedKeys[PasswordInfo]("hasher", "password", "salt")
      .transact(xa)
      .exceptSql(ex => me.raiseError[PasswordInfo](ex))
  }

  def deletePasswordInfo(id: String, key: String): F[Int] = {
    UserDao.deletePasswordInfo(id, key)
      .run
      .transact(xa)
  }

  def getUserByLoginInfo(id: String, key: String): F[Option[User]] = {
    UserDao.getUserByLoginInfo(id, key)
      .option
      .transact(xa)
      .exceptSql(ex => me.raiseError[Option[User]](ex))
  }

  def getUserByUUID(uuid: UUID): F[Option[User]] = {
    UserDao.getUserByUUID(uuid)
      .option
      .transact(xa)
      .exceptSql(ex => me.raiseError[Option[User]](ex))
  }

  def saveUser(u: User): F[User] = {
    import UserDao._
    UserDao.saveUser(u)
      .withUniqueGeneratedKeys[User]("user_uuid", "provider_id", "provider_key",
        "first_name", "last_name", "full_name","email", "avatar_url", "activated")
      .transact(xa)
      .exceptSql(ex => me.raiseError[User](ex))
  }

  def getToken(uuid: UUID): F[Option[AuthToken]] = {
    UserDao.getToken(uuid)
      .option
      .transact(xa)
      .exceptSql(ex => me.raiseError(ex))
  }

  def saveToken(token: AuthToken): F[AuthToken] = {
    import UserDao._
    UserDao.saveToken(token)
      .withUniqueGeneratedKeys[AuthToken]("uuid", "user_uuid", "expiry")
      .transact(xa)
      .exceptSql(ex => me.raiseError[AuthToken](ex))
  }

  def getExpiredTokens(millis:Long): F[List[AuthToken]] ={
    UserDao.getExpired(millis)
      .to[List]
      .transact(xa)
      .exceptSql(ex => me.raiseError[List[AuthToken]](ex))
  }

  def deleteToken(uuid: UUID): F[Int] = {
    UserDao.deleteToken(uuid)
      .run
      .transact(xa)
  }
}
