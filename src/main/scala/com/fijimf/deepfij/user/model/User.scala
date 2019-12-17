package com.fijimf.deepfij.user.model

import java.util.UUID

case class User(
                 userId: UUID,
                 providerId: String,
                 providerKey:String,
                 firstName: Option[String],
                 lastName: Option[String],
                 fullName: Option[String],
                 email: Option[String],
                 avatarURL: Option[String],
                 activated: Boolean)