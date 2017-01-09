package com.eigenroute.authentication

import java.security.PublicKey

trait JWTPublicKeyProvider {

  def publicKey: PublicKey

}
