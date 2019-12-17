package com.fijimf.deepfij.user.model

import doobie.util.update.Update0

case class PasswordInfo(hasher: String, password: String, salt: Option[String] = None)

object PasswordInfo {




}
