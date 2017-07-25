/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.gform.config

import java.io.File

import net.ceedubs.ficus.Ficus._
import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.{ ConfigFactory, Config => TypeSafeConfig }
import play.api.Mode.Mode
import play.api.http.{ HttpErrorHandler, HttpRequestHandler }
import play.api.{ Application, Configuration }
import pureconfig._
import uk.gov.hmrc.gform.ApplicationModule
import uk.gov.hmrc.play.auth.controllers.AuthParamsControllerConfig
import uk.gov.hmrc.play.config.{ ControllerConfig, ServicesConfig }

import scala.concurrent.Future

class ConfigModule(applicationModule: ApplicationModule) {

  val typesafeConfig = ConfigFactory.load()
  val appConfig: AppConfig = AppConfig.loadOrThrow(typesafeConfig)
  val serviceConfig: ServicesConfig = new ServicesConfig {}
  val playConfiguration = applicationModule.configuration

  val controllerConfig = new ControllerConfig {
    lazy val controllerConfigs = typesafeConfig.as[TypeSafeConfig]("controllers")
  }

  val authParamsControllerConfig = new AuthParamsControllerConfig {
    lazy val controllerConfigs = controllerConfig.controllerConfigs
  }

}

case class AppConfig(
  appName: String,
  formExpiryDays: Long,
  formMaxAttachmentSizeMB: Int,
  formMaxAttachments: Int
)

object AppConfig {

  def loadOrThrow(config: TypeSafeConfig): AppConfig = {
    implicit def hint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))
    val appConfig = loadConfigOrThrow[AppConfig](config)

    appConfig.formExpiryDays.verifyThat(_ > 0, s"'formExpiryDays' must be positive, was ${appConfig.formExpiryDays}")
    appConfig.formMaxAttachments.verifyThat(_ > 0, s"'formMaxAttachments' must be positive, was ${appConfig.formMaxAttachments}")
    appConfig.formMaxAttachmentSizeMB.verifyThat(_ > 0, s"'formMaxAttachmentSizeMB' must be positive, was ${appConfig.formMaxAttachmentSizeMB}")

    appConfig
  }

  private implicit class VerifyThat[T](t: T) {
    def verifyThat(assertion: T => Boolean, message: String = "") = if (!assertion(t)) throw new AppConfigException(message)
  }

  class AppConfigException(message: String) extends IllegalArgumentException(message)

  /**
   * This is copy/paste from file-upload project. This is how they validate config. We will do the same:
   */
  private def isAValidSize(size: String): Boolean = {

    val sizeRegex = "([1-9][0-9]{0,3})([KB,MB]{2})".r

    if (size.isEmpty) false
    else {
      size.toUpperCase match {
        case sizeRegex(num, unit) =>
          unit match {
            case "KB" => true
            case "MB" => true
            case _ => false
          }
        case _ => false
      }
    }
  }

}
