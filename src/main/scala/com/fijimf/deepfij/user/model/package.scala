package com.fijimf.deepfij.user

import cats.Applicative
import cats.effect.Sync
import cats.implicits._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

package object model {

  val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
  implicit val dateEncoder: Encoder[DateTime] = Encoder.encodeString.contramap[DateTime](_.toString(formatter))
  implicit val dateDecoder: Decoder[DateTime] = Decoder.decodeString.emap[DateTime](str => {
    Either.catchNonFatal(DateTime.parse(str, formatter)).leftMap(_.getMessage)
  })

  implicit val passwordInfoEncoder: Encoder.AsObject[PasswordInfo] = deriveEncoder[PasswordInfo]
  implicit val passwordInfoDecoder: Decoder[PasswordInfo] = deriveDecoder[PasswordInfo]

  implicit def passwordInfoEntityEncoder[F[_] : Applicative]: EntityEncoder[F, PasswordInfo] = jsonEncoderOf

  implicit def passwordInfoEntityDecoder[F[_] : Sync]: EntityDecoder[F, PasswordInfo] = jsonOf

  implicit def intEntityDecoder[F[_] : Sync]: EntityDecoder[F, Int] = jsonOf

  implicit def intEntityEncoder[F[_] : Applicative]: EntityEncoder[F, Int] = jsonEncoderOf

  implicit val authTokenEncoder: Encoder.AsObject[AuthToken] = deriveEncoder[AuthToken]
  implicit val authTokenDecoder: Decoder[AuthToken] = deriveDecoder[AuthToken]

  implicit def authTokenEntityEncoder[F[_] : Applicative]: EntityEncoder[F, AuthToken] = jsonEncoderOf

  implicit def authTokenEntityDecoder[F[_] : Sync]: EntityDecoder[F, AuthToken] = jsonOf

  implicit def lstAuthTokenEntityEncoder[F[_] : Applicative]: EntityEncoder[F, List[AuthToken]] = jsonEncoderOf

  implicit def lstAuthTokenEntityDecoder[F[_] : Sync]: EntityDecoder[F, List[AuthToken]] = jsonOf


  implicit val userEncoder: Encoder.AsObject[User] = deriveEncoder[User]
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]

  implicit def userEntityEncoder[F[_] : Applicative]: EntityEncoder[F, User] = jsonEncoderOf

  implicit def userEntityDecoder[F[_] : Sync]: EntityDecoder[F, User] = jsonOf

}
