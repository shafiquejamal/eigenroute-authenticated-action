package com.eigenroute.authentication

import pdi.jwt.algorithms.JwtAsymetricAlgorithm

trait JWTAlgorithmProvider {

  def algorithm: JwtAsymetricAlgorithm

}
