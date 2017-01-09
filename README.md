# Eigenroute Authenticated Action

This project borrows heavily (almost entirely) from [JWT Scala](https://github.com/pauldijou/jwt-scala). 

## Installation

### SBT

```
resolvers += "Eigenroute maven repo" at "http://mavenrepo.eigenroute.com/"
libraryDependencies += "com.eigenroute" % "eigenroute-authenticated-action" % "0.0.1"
```

## Use

```scala
import authentication.{AuthenticatedActionCreator, JWTAlgorithmProvider, JWTPublicKeyProvider}
import com.eigenroute.time.TimeProvider
import com.google.inject.Inject
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._

class ReceiveSMSController @Inject() (
    override val timeProvider: TimeProvider,
    override val jWTAlgorithmProvider: JWTAlgorithmProvider,
    override val jWTPublicKeyProvider: JWTPublicKeyProvider
  ) extends Controller with AuthenticatedActionCreator {

  def post = AuthenticatedAction(parse.json) { request =>
    request.body.validate[SMSMessage] match {
      case success : JsSuccess[SMSMessage] =>
        Ok
      case error : JsError =>
        BadRequest
    }
  }

}
```

Don't forget to bind the injected dependencies in Module.scala.