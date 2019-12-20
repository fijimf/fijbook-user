package com.fijimf.deepfij.user

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Timer}
import com.fijimf.deepfij.user.services.UserRepo
import com.fijimf.deepfij.user.util.Banner
import doobie.util.transactor.Transactor
import fs2.Stream
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.syntax.kleisli._
import org.http4s.{HttpApp, HttpRoutes}

object UserServer {

  @SuppressWarnings(Array("org.wartremover.warts.Nothing", "org.wartremover.warts.Any"))
  def stream[F[_] : ConcurrentEffect](transactor: Transactor[F], port: Int)(implicit T: Timer[F], C: ContextShift[F]): Stream[F, ExitCode] = {
    val repo: UserRepo[F] = new UserRepo[F](transactor)
    val userService: HttpRoutes[F] = UserRoutes.routes(repo)

    val httpApp: HttpApp[F] =
      userService.orNotFound
    val finalHttpApp: HttpApp[F] = Logger.httpApp[F](logHeaders = true, logBody = true)(httpApp)
    val host = "0.0.0.0"
    for {
      exitCode <- BlazeServerBuilder[F]
        .bindHttp(port = port, host = host)
        .withHttpApp(finalHttpApp)
        .withBanner(Banner.banner( host, port, Some("status")))
        .serve
    } yield {
      exitCode
    }
    }.drain
}
