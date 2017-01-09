package com.eigenroute.authentication

import java.security.spec.{ECPoint, ECPublicKeySpec}
import java.security.{KeyFactory, PublicKey, Security}

import com.typesafe.config.ConfigFactory
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveSpec

class JWTPublicKeyProviderImpl extends JWTPublicKeyProvider {

  val configuration = ConfigFactory.load()

  override def publicKey: PublicKey = {
    val xRaw: String = configuration.getString("eigenrouteAuthenticatedAction.publicKey.X")
    val yRaw: String = configuration.getString("eigenrouteAuthenticatedAction.publicKey.Y")
    val X = BigInt(xRaw, 16)
    val Y = BigInt(yRaw, 16)
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
    val curveParams = ECNamedCurveTable.getParameterSpec("P-521")
    val curveSpec =
      new ECNamedCurveSpec("P-521", curveParams.getCurve, curveParams.getG, curveParams.getN, curveParams.getH)
    val publicSpec = new ECPublicKeySpec(new ECPoint(X.underlying(), Y.underlying()), curveSpec)
    KeyFactory.getInstance("ECDSA", "BC").generatePublic(publicSpec)
  }

}
