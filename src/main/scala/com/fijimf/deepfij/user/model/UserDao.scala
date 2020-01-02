package com.fijimf.deepfij.user.model


import java.sql.Timestamp
import java.util.UUID

import doobie.implicits._
import doobie.util.Meta
import org.joda.time.DateTime

object UserDao {

  implicit val uuidMeta: Meta[UUID] = Meta[String]
    .imap(s => UUID.fromString(s))(uuid => uuid.toString)
  implicit val dateTimeMeta: Meta[DateTime] = Meta[Timestamp]
    .imap(ts => new DateTime(ts.getTime))(dt => new Timestamp(dt.getMillis))


  def truncateAuthToken(): doobie.Update0 = {
    sql""" TRUNCATE TABLE auth_token CASCADE""".update
  }

  def truncatePasswordInfo(): doobie.Update0 = {
    sql""" TRUNCATE TABLE password_info CASCADE""".update
  }

  def truncateUser(): doobie.Update0 = {
    sql""" TRUNCATE TABLE auth_user CASCADE""".update
  }

  //GET    /authInfo/:providerID/:providerKey => Option[PasswordInfo]
  def getPasswordInfo(id: String, key: String): doobie.Query0[PasswordInfo] = {
    sql"""SELECT p.hasher,p.password, p.salt from password_info p
         |INNER JOIN auth_user u ON u.id = p.user_id
         |WHERE u.provider_id= $id AND u.provider_key=$key
         |""".stripMargin.query[PasswordInfo]
  }

  //POST   /authInfo/:providerID/:providerKey => PasswordInfo
  def savePasswordInfo(id: String, key: String, passwordInfo: PasswordInfo): doobie.Update0 = {
    sql"""INSERT INTO password_info(user_id, hasher, password, salt)
         |SELECT u.id user_id, ${passwordInfo.hasher}, ${passwordInfo.password}, ${passwordInfo.salt} FROM auth_user u
         |WHERE u.provider_id=$id AND u.provider_key=$key
       """.stripMargin.update
  }

  //DELETE /authInfo/:providerID/:providerKey  => Unit
  def deletePasswordInfo(id: String, key: String): doobie.Update0 = {
    sql"""DELETE FROM password_info p
         | USING auth_user u
         | WHERE p.user_id = u.id AND u.provider_id=$id AND u.provider_key=$key
         |""".stripMargin.update
  }

  //  GET    /token/:uuid                       => Option[AuthToken]
  def getToken(id: UUID): doobie.Query0[AuthToken] = {
    sql"""SELECT uuid, user_uuid, expiry from auth_token where uuid=$id""".query[AuthToken]
  }


  //  GET    /token/expired?epochMillis=:millis => Seq[AuthToken]
  def getExpired(millis: Long): doobie.Query0[AuthToken] = {
    sql"""SELECT uuid, user_uuid, expiry from auth_token where expiry > ${new Timestamp(millis)}""".query[AuthToken]
  }

  //  POST   /token/                            => AuthToken
  def saveToken(tok: AuthToken): doobie.Update0 = {
    sql"""INSERT INTO auth_token( uuid, user_uuid, expiry) VALUES (${tok.id}, ${tok.userId}, ${tok.expiry} )""".update
  }

  //  DELETE /token/:uuid                       => Unit
  def deleteToken(uuid: UUID): doobie.Update0 = {
    sql"""DELETE FROM auth_token where uuid=${uuid}""".update
  }

  //  GET    /user/:providerID/:providerKey     => Option[User]
  def getUserByLoginInfo(id: String, key: String): doobie.Query0[User] = {
    sql"""
         |SELECT user_uuid,
         |       provider_id ,
         |       provider_key,
         |       first_name,
         |       last_name,
         |       full_name,
         |       email,
         |       avatar_url,
         |       activated
         |FROM auth_user where provider_id=$id and provider_key=$key
         |""".stripMargin.query[User]
  }

  //  GET    /user/:uuid                        => Option[User]
  def getUserByUUID(uuid: UUID): doobie.Query0[User] = {
    sql"""
         |SELECT user_uuid,
         |       provider_id ,
         |       provider_key,
         |       first_name,
         |       last_name,
         |       full_name,
         |       email,
         |       avatar_url,
         |       activated
         |FROM auth_user where user_uuid=${uuid}
         |""".stripMargin.query[User]
  }

  //  POST   /user/                             => User
  def saveUser(u: User): doobie.Update0 = {
    sql"""
         |INSERT INTO auth_user(user_uuid,
         |       provider_id ,
         |       provider_key,
         |       first_name,
         |       last_name,
         |       full_name,
         |       email,
         |       avatar_url,
         |       activated)  VALUES (
         |       ${u.userId},
         |       ${u.providerId},
         |       ${u.providerKey},
         |       ${u.firstName},
         |       ${u.lastName},
         |       ${u.fullName},
         |       ${u.email},
         |       ${u.avatarURL},
         |       ${u.activated}
         |       )
         |       ON CONFLICT (provider_id, provider_key) DO UPDATE SET
         |         first_name = ${u.firstName},
         |         last_name = ${u.lastName},
         |         full_name = ${u.fullName},
         |         email = ${u.email},
         |         avatar_url = ${u.avatarURL},
         |         activated = ${u.activated}
         |""".stripMargin.update
  }
}
