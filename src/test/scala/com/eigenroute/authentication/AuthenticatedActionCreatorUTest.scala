package com.eigenroute.authentication

import java.security.spec.ECPrivateKeySpec
import java.security.{KeyFactory, PrivateKey, Security}
import java.util.UUID

import com.eigenroute.util.id.TestUUIDProviderImpl
import com.eigenroute.util.time.{TestTimeProviderImpl, TimeProvider}
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import org.scalatest.{FlatSpecLike, ShouldMatchers}
import pdi.jwt.JwtJson
import play.api.libs.json.Json

class AuthenticatedActionCreatorUTest extends FlatSpecLike with ShouldMatchers {

  val authenticatedActionCreator = new AuthenticatedActionCreator {
    override val jWTPublicKeyProvider: JWTPublicKeyProvider = new JWTPublicKeyProviderImpl()
    override val jWTAlgorithmProvider: JWTAlgorithmProvider = new JWTAlgorithmProviderImpl()
    override val timeProvider: TimeProvider = new TestTimeProviderImpl()
  }

  val now = authenticatedActionCreator.timeProvider.now()
  val uUIDProvider = new TestUUIDProviderImpl()
  val uUID = uUIDProvider.randomUUID()
  val claimValid = Json.obj("userId" -> uUID, "iat" -> now.minusDays(1))
  val claimExpired = Json.obj("userId" -> uUID, "iat" -> now.minusDays(3))


  val jWTPrivateKeyProvider = new JWTPrivateKeyProviderImpl()
  val validToken =
    JwtJson.encode(claimValid, jWTPrivateKeyProvider.privateKey, authenticatedActionCreator.jWTAlgorithmProvider.algorithm)

  trait WrongPrivateKey {
    val S = BigInt("abcd", 16)
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
    val curveParams = ECNamedCurveTable.getParameterSpec("P-521")
    val curveSpec =
      new ECNamedCurveSpec("P-521", curveParams.getCurve, curveParams.getG, curveParams.getN, curveParams.getH)
    val privateSpec = new ECPrivateKeySpec(S.underlying(), curveSpec)
    val wrongPrivateKey: PrivateKey = KeyFactory.getInstance("ECDSA", "BC").generatePrivate(privateSpec)
    val inValidToken =
      JwtJson.encode(claimValid, wrongPrivateKey, authenticatedActionCreator.jWTAlgorithmProvider.algorithm)
  }


  var blockWasCalled = false
  def block(uUID: UUID): Boolean = {
    blockWasCalled = true
    blockWasCalled
  }

  var unauthorizedWasCalled = false
  def unauthorized():Boolean = {
    unauthorizedWasCalled = true
    unauthorizedWasCalled
  }

  def initializeVars(): Unit = {
    unauthorizedWasCalled = false
    blockWasCalled = false
  }

  trait InitialConditions {
    initializeVars()
  }

  "Validating the claim" should "succeed if the claim has not expired" in new InitialConditions {
    authenticatedActionCreator.validateClaim(block, unauthorized(), claimValid)
    blockWasCalled shouldBe true
    unauthorizedWasCalled shouldBe false
  }

  it should "fail if the claim has expired" in new InitialConditions {
    authenticatedActionCreator.validateClaim(block, unauthorized(), claimExpired)
    blockWasCalled shouldBe false
    unauthorizedWasCalled shouldBe true
  }

  "Decoding a valid token" should "call the block if the decoding succeeds" in new InitialConditions {
    authenticatedActionCreator.decodeToken(validToken, block, unauthorized())
    blockWasCalled shouldBe true
    unauthorizedWasCalled shouldBe false
  }

  it should "call the unauthorized function if the decoding fails" in new WrongPrivateKey with InitialConditions {
    authenticatedActionCreator.decodeToken(inValidToken, block, unauthorized())
    blockWasCalled shouldBe false
    unauthorizedWasCalled shouldBe true
  }

}
