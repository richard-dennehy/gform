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
import play.api.http.Status.{ BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND }
import play.api.libs.json.Json
import play.api.mvc.Results.{ BadRequest, NotFound }
import play.api.mvc.{ RequestHeader, Result }
import play.api.routing.Router
import play.api.{ Configuration, Environment, Logger }
import play.core.SourceMapper
import uk.gov.hmrc.play.audit.EventTypes.{ ResourceNotFound, ServerInternalError, ServerValidationError, TransactionFailureReason }
import uk.gov.hmrc.play.audit.http.HttpAuditEvent
import uk.gov.hmrc.play.audit.http.config.ErrorAuditingSettings
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.microservice.bootstrap.{ ErrorResponse, JsonErrorHandling }

import scala.concurrent.Future

class ErrorHandler(
    //  val auditConnector: AuditConnector,
    environment: Environment,
    configuration: Configuration,
    sourceMapper: Option[SourceMapper],
    router: => Router //used to display better error messages in dev-mode
) extends DefaultHttpErrorHandler(environment, configuration, sourceMapper, Some(router)) //  with JsonErrorHandling
//  with ErrorAuditingSettings
{

  override protected def onBadRequest(request: RequestHeader, message: String): Future[Result] = super.onBadRequest(request, message)
  override protected def onForbidden(request: RequestHeader, message: String): Future[Result] = super.onForbidden(request, message)
  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = super.onNotFound(request, message)
  override protected def onOtherClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = super.onOtherClientError(request, statusCode, message)

  //
  //  override def xxx(request: RequestHeader, ex: Throwable) = {
  //    val message = s"! Internal server error, for (${request.method}) [${request.uri}] -> "
  //    Logger.error(message, ex)
  //    Future.successful(resolveError(ex))
  //  }
  //
  //
  //  private def resolveError(ex: Throwable): Result = {
  //    val errorResponse = ex match {
  //      case e: HttpException       => ErrorResponse(e.responseCode, e.getMessage)
  //      case e: Upstream4xxResponse => ErrorResponse(e.reportAs, e.getMessage)
  //      case e: Upstream5xxResponse => ErrorResponse(e.reportAs, e.getMessage)
  //      case e: Throwable           => ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage)
  //    }
  //
  //    new play.api.mvc.Results.Status(errorResponse.statusCode)(Json.toJson(errorResponse))
  //  }
  //
  //  override def onHandlerNotFound(request: RequestHeader) = {
  //    Future.successful {
  //      val er = ErrorResponse(NOT_FOUND, "URI not found", requested = Some(request.path))
  //      NotFound(Json.toJson(er))
  //    }
  //  }
  //
  //  override def onBadRequest(request: RequestHeader, error: String) = {
  //    Future.successful {
  //      val er = ErrorResponse(BAD_REQUEST, error)
  //      BadRequest(Json.toJson(er))
  //    }
  //  }
  //
  //  //////////////////
  //  def auditConnector: AuditConnector
  //
  //  private val unexpectedError = "Unexpected error"
  //  private val notFoundError = "Resource Endpoint Not Found"
  //  private val badRequestError = "Request bad format exception"
  //
  //  override def onServerError(request: RequestHeader, ex: Throwable): Future[Result] = {
  //    val code = ex match {
  //      case e: NotFoundException => ResourceNotFound
  //      case jsError: JsValidationException => ServerValidationError
  //      case _ => ServerInternalError
  //    }
  //
  //    auditConnector.sendEvent(dataEvent(code, unexpectedError, request)
  //      .withDetail((TransactionFailureReason, ex.getMessage)))
  //    super.onError(request, ex)
  //  }
  //
  //  override def onHandlerNotFound(request: RequestHeader): Future[Result] = {
  //    auditConnector.sendEvent(dataEvent(ResourceNotFound, notFoundError, request))
  //    super.onHandlerNotFound(request)
  //  }
  //
  //  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
  //    auditConnector.sendEvent(dataEvent(ServerValidationError, badRequestError, request))
  //    super.onBadRequest(request, error)
  //  }
}

class A extends HttpAuditEvent {
  override def appName: String = ???
}