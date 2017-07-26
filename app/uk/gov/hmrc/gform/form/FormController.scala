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

import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, RequestHeader }
import uk.gov.hmrc.gform.controllers.BaseController
import uk.gov.hmrc.gform.fileUpload.{ FileUploadConnector, FileUploadService }
import uk.gov.hmrc.gform.formtemplate.{ FormTemplateRepo, FormTemplateService }
import uk.gov.hmrc.gform.models._
import uk.gov.hmrc.gform.save4later.Save4Later
import uk.gov.hmrc.gform.submission.SubmissionRepo
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._

import scala.concurrent.Future

class FormController(
    formTemplateService: FormTemplateService,
    fileUploadService: FileUploadService,
    formService: FormService
) extends BaseController {

  def newForm(userId: UserId, formTemplateId: FormTemplateId) = Action.async { implicit request =>
    //TODO authentication
    //TODO user should be obtained from secure action
    //TODO authorisation
    //TODO Prevent creating new form when there exist one. Ask user to explicitly delete it

    val envelopeIdF = fileUploadService.createEnvelope(formTemplateId)
    val formId = FormId(userId, formTemplateId)
    val formIdF: Future[FormId] = for {
      envelopeId <- envelopeIdF
      _ <- formService.insertEmpty(userId, formTemplateId, envelopeId, formId)

    } yield formId

    formIdF.asOkJson
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
    //TODO authentication
    //TODO authorisation

    formService
      .get(formId)
      .asOkJson
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
