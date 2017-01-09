package com.eigenroute.authentication

import java.security.PublicKey
import java.util.UUID

import com.eigenroute.time.TimeProvider
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import pdi.jwt.JwtJson
import pdi.jwt.algorithms.JwtAsymetricAlgorithm
import play.api.libs.json.JsObject
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future
import scala.util.{Success, Try}

trait AuthenticatedActionCreator {

  val jWTPublicKeyProvider: JWTPublicKeyProvider
  val jWTAlgorithmProvider: JWTAlgorithmProvider
  val timeProvider: TimeProvider
  lazy val publicKey: PublicKey = jWTPublicKeyProvider.publicKey
  lazy val algorithm: JwtAsymetricAlgorithm = jWTAlgorithmProvider.algorithm

  val configuration = ConfigFactory.load()

  def validateClaim[T](
      block: => (UUID) => T,
      unauthorized: => T,
      claim: JsObject): T =
    claim.value.get("iat").flatMap(_.asOpt[DateTime]).fold[T](unauthorized) { iat =>
      val jWTValidity = Try(configuration.getInt("eigenrouteAuthenticatedAction.jwtValidityDays")).toOption.getOrElse(-1)
      val tokenExpired = iat.isBefore(timeProvider.now().minusDays(jWTValidity)) && jWTValidity > 0
      if (tokenExpired)
        unauthorized
      else {
        val maybeUUID = claim.value.get("userId").flatMap(_.asOpt[String])
        maybeUUID.fold(unauthorized){ uUID => block(UUID.fromString(uUID))}
      }
    }

  def decodeToken[T](
      token: String,
      block: => (UUID) => T,
      unauthorized: => T): T =
    JwtJson.decodeJson(token, publicKey, Seq(algorithm)) match {
      case Success(claim) =>
        validateClaim(block, unauthorized, claim)
      case _ =>
        unauthorized
    }

  object AuthenticatedAction extends ActionBuilder[AuthenticatedRequest] {
    def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
      request.headers.get("Authorization").map(_.drop(7)).filterNot(_.trim.isEmpty)
      .fold[Future[Result]](Future.successful(Unauthorized)) { token =>
        decodeToken(
          token,
          (userId: UUID) => block(new AuthenticatedRequest(userId, request)),
          Future.successful(Unauthorized))
      }
  }

}