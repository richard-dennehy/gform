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

package uk.gov.hmrc.gform.form

import cats.implicits._
import play.api.libs.json.Json
import play.api.mvc.{ Action, AnyContent, RequestHeader }
import uk.gov.hmrc.gform.fileUpload.FileUploadConnector
import uk.gov.hmrc.gform.formtemplate.{ FormTemplateRepo, FormTemplateService }
import uk.gov.hmrc.gform.models._
import uk.gov.hmrc.gform.save4later.Save4Later
import uk.gov.hmrc.gform.submission.SubmissionRepo
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future

class FormController(
    s4l: Save4Later,
    formTemplateRepo: FormTemplateRepo,
    formTemplateService: FormTemplateService,
    submissionRepo: SubmissionRepo,
    fileUploadConnector: FileUploadConnector,
    formService: FormService
) extends BaseController {

  def newForm(userId: UserId, formTemplateId: FormTemplateId) = Action.async { implicit request =>
    //
    //    val templateF: Future[Option[FormTemplate]] =
    //      formTemplateRepo
    //        .find(formTypeId.value)
    //
    //    val x: LeftResult[Option[FormTemplate]] = asRes(templateF)
    //
    //    for {
    //      x <- asRes(templateF)
    //
    //    } yield ???
    //
    //    val templateRes: LeftResult[Either[LeftResult, FormTemplate]] = templateF.map{
    //            case Some(x) => x.asRight
    //            case None => BadRequest("Bad formTemplateId").asLeft
    //      }
    //
    //
    //    val envelopeIdF = fileUploadConnector.createEnvelope(formTypeId)
    //    val formId = FormId(userId, formTypeId)
    //    val response = for {
    //      formTemplate <- asRes(templateF)
    //      envelopeId <- envelopeIdF
    //      form <- formService.insertEmpty(userId, formTypeId, envelopeId, formId)
    //    } yield form
    //
    //    response.fold(
    //      error => error.toResult,
    //      response => Ok(Json.toJson(response))
    //    )

    ???
  }

  def getByIdAndVersion(formTypeId: FormTemplateId) = Action.async { implicit request =>

    //    FormService.getByIdAndVersion(formTypeId, version).fold(
    //      error => error.toResult,
    //      response => {
    //        val links = response.map(formLink)
    //        Ok(Json.toJson(links))
    //      }
    //    )
    ???
  }

  def get(formId: FormId) = Action.async { implicit request =>
    //    FormService.get(formId).fold(
    //      error => NotFound,
    //      response => Ok(Json.toJson(response))
    //    )
    //    ???
    Future.successful(Ok("jajaj"))
  }

  def testEndPoint(formId: FormId) = Action.async { implicit request =>
    //    FormService.get(formId).fold(
    //      error => error.toResult,
    //      response => Ok(Json.toJson(response))
    //    )
    ???
  }

  def update(formId: FormId, tolerant: Option[Boolean]) = Action.async(parse.json[FormData]) { implicit request =>
    //    val operation = tolerant match {
    //      case Some(true) => UpdateTolerantOperation
    //      case _ => UpdateOperation
    //    }
    //    FormService.updateFormData(formId, request.body).fold(
    //      er => BadRequest(er.jsonResponse),
    //      success => success.toResult
    //    )
    ???
  }

  def submission(formId: FormId) = Action.async { implicit request =>
    //    SubmissionService.submission(formId).fold(
    //      error => error.toResult,
    //      response => Ok(response)
    //    )
    ???
  }

  def delete(formId: FormId): Action[AnyContent] = Action.async { implicit request =>
    //    FormService.delete(formId).fold(
    //      errors => {
    //        Logger.warn(s"${formId.value} failed to be deleted due to ${errors.toResult}")
    //        errors.toResult
    //      },
    //      success => {
    //        success.toResult
    //      }
    //    )
    ???
  }

  def submissionStatus(formId: FormId) = Action.async { implicit request =>
    Future.successful(NotImplemented)
  }

  private def formLink(form: Form)(implicit request: RequestHeader) = {
    //    val Form(formId, formData, envelopeId) = form
    //    routes.FormController.get(formId).absoluteURL()
    ???
  }
}
