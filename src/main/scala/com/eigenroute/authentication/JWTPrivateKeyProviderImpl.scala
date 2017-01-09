package com.eigenroute.authentication

import java.security.spec.ECPrivateKeySpec
import java.security.{KeyFactory, PrivateKey, Security}

import com.typesafe.config.ConfigFactory
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.spec.ECNamedCurveSpec

class JWTPrivateKeyProviderImpl extends JWTPrivateKeyProvider {

  val configuration = ConfigFactory.load()

  override def privateKey: PrivateKey = {
    val sRaw: String = configuration.getString("eigenrouteAuthenticatedAction.privateKey.S")
    val S = BigInt(sRaw, 16)
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
    val curveParams = ECNamedCurveTable.getParameterSpec("P-521")
    val curveSpec =
      new ECNamedCurveSpec("P-521", curveParams.getCurve, curveParams.getG, curveParams.getN, curveParams.getH)
    val privateSpec = new ECPrivateKeySpec(S.underlying(), curveSpec)
    KeyFactory.getInstance("ECDSA", "BC").generatePrivate(privateSpec)
  }

}
