package com.fijimf.deepfij.user.model

case class PasswordInfo(hasher: String, password: String, salt: Option[String] = None)

