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

package uk.gov.hmrc.gform.playcomponents

import play.api.http.DefaultHttpErrorHandler
import play.api.mvc.{ RequestHeader, Result }
import play.api.routing.Router
import play.api.{ Configuration, Environment }
import play.core.SourceMapper
import uk.gov.hmrc.play.audit.http.config.ErrorAuditingSettings
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.microservice.bootstrap.JsonErrorHandling

import scala.concurrent.Future

class CustomErrorHandling(
  val auditConnector: AuditConnector,
  val appName: String,
  environment: Environment,
  configuration: Configuration,
  sourceMapper: Option[SourceMapper] = None,
  router: => Option[Router] = None
) extends DefaultHttpErrorHandler(environment, configuration, sourceMapper, router)
    with JsonErrorHandling
    with ErrorAuditingSettings {

  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
    super.onBadRequest(request, error)
  }

  override def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    super.onHandlerNotFound(request)
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    super.onError(request, exception)
  }
}
