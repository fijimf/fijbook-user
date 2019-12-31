package com.fijimf.deepfij.user


import java.sql.Timestamp
import java.time.LocalDateTime

import cats.effect.Sync
import cats.implicits._
import com.fijimf.deepfij.user.model._
import com.fijimf.deepfij.user.services.UserRepo
import com.fijimf.deepfij.user.util.ServerInfo
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.middleware.RequestLogger
import org.slf4j.{Logger, LoggerFactory}


object UserRoutes {
  /*
      GET    /authInfo/:providerID/:providerKey => Option[PasswordInfo]
      POST   /authInfo/:providerID/:providerKey => PasswordInfo
      DELETE /authInfo:providerID/:providerKey  => Unit

      GET    /token/:uuid                       => Option[AuthToken]
      GET    /token/expired?epochMillis=:millis => Seq[AuthToken]
      POST   /token/                            => AuthToken
      DELETE /token/:uuid                       => Unit

      GET    /user/:providerID/:providerKey     => Option[User]
      GET    /user/:uuid                        => Option[User]
      POST   /user/                             => User
   */

  val log: Logger = LoggerFactory.getLogger(UserRoutes.getClass)

  def routes[F[_]](repo: UserRepo[F])(implicit F: Sync[F]): HttpRoutes[F] = {
    val dsl: Http4sDsl[F] = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {

      case req@POST -> Root / "user" => {
        (for {
          u <- req.as[User]
          x <- repo.saveUser(u)
          resp <- Ok(x)
        } yield {
          resp
        }).recoverWith { case thr: Throwable =>
          InternalServerError(thr.getMessage) }
      }

      case GET -> Root / "status" =>
        for {
          status<-repo.healthcheck.map(isOk=>ServerInfo.fromStatus(isOk))
          resp <- if (status.isOk) Ok(status) else InternalServerError(status)
        } yield {
          resp
        }


      case GET -> Root / "authInfo" / id / key =>
        (for {
          passwordInfo <- repo.getPasswordInfo(id, key)
          resp <- passwordInfo match {
            case Some(pi) => Ok(pi)
            case None => NotFound()
          }
        } yield {
          resp
        }).recoverWith { case thr: Throwable => InternalServerError(thr.getMessage) }
      case req@POST -> Root / "authInfo" / id / key =>
        (for {
          s <- req.as[PasswordInfo]
          x <- repo.savePasswordInfo(id, key, s)
          resp <- Ok(x)
        } yield {
          resp
        }).recoverWith { case thr: Throwable => InternalServerError(thr.getMessage) }
      case DELETE -> Root / "authInfo" / id / key =>
        (for {
          i <- repo.deletePasswordInfo(id, key)
          resp <- Ok(i)
        } yield {
          resp
        })


      case req@GET -> Root / "token" / "expired" => {
        val m = req.params.get("epochMillis").map(_.toLong).getOrElse(Timestamp.valueOf(LocalDateTime.now()).getTime)
        (for {
          ts <- repo.getExpiredTokens(m)
          resp <- Ok(ts)
        } yield {
          resp
        }).recoverWith { case thr: Throwable => InternalServerError(thr.getMessage) }
      }

      case GET -> Root / "token" / UUIDVar(uuid) => {
        (for {
          a <- repo.getToken(uuid)
          resp <- a match {
            case Some(authToken) => Ok(authToken)
            case None => NotFound()
          }
        } yield {
          resp
        }).recoverWith { case thr: Throwable => InternalServerError(thr.getMessage) }
      }

      case req@POST -> Root / "token" =>
        (for {
          t <- req.as[AuthToken]
          x <- repo.saveToken(t)
          resp <- Ok(x)
        } yield {
          resp
        }).recoverWith { case thr: Throwable => InternalServerError(thr.getMessage) }

      case DELETE -> Root / "token" / UUIDVar(uuid) => {
        (for {
          i <- repo.deleteToken(uuid)
          resp <- Ok(i)
        } yield {
          resp
        }).recoverWith { case thr: Throwable => InternalServerError(thr.getMessage) }
      }


      case GET -> Root / "user" / id / key => {
        (for {
          u <- repo.getUserByLoginInfo(id, key)
          resp <- u match {
            case Some(user) => Ok(user)
            case None => NotFound()
          }
        } yield {
          resp
        }).recoverWith { case thr: Throwable => InternalServerError(thr.getMessage) }
      }
      case GET -> Root / "user" / UUIDVar(uuid) => {
        (for {
          u <- repo.getUserByUUID(uuid)
          resp <- u match {
            case Some(user) => Ok(user)
            case None => NotFound()
          }
        } yield {
          resp
        }).recoverWith { case thr: Throwable => InternalServerError(thr.getMessage) }
      }

    }
  }
}