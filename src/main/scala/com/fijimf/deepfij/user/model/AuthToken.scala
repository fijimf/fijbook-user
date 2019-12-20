package com.fijimf.deepfij.user.model

import java.util.UUID

import org.joda.time.DateTime

case class AuthToken(id: UUID,
                      userId: UUID,
                      expiry: DateTime)