package com.eigenroute.authentication

import java.security.PrivateKey

trait JWTPrivateKeyProvider {

  def privateKey: PrivateKey

}
