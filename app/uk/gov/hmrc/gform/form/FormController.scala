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
import uk.gov.hmrc.gform.core.FormValidator
import uk.gov.hmrc.gform.exceptions.UnexpectedState
import uk.gov.hmrc.gform.fileUpload.{ FileUploadConnector, FileUploadService }
import uk.gov.hmrc.gform.formtemplate.{ FormTemplateRepo, FormTemplateService }
import uk.gov.hmrc.gform.models._
import uk.gov.hmrc.gform.save4later.Save4Later
import uk.gov.hmrc.gform.submission.SubmissionRepo
import uk.gov.hmrc.play.http.BadRequestException
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._

import scala.concurrent.Future
import scala.util.Try

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

  def get(formId: FormId) = Action.async { implicit request =>
    //TODO authentication
    //TODO authorisation

    formService
      .get(formId)
      .asOkJson
  }

  def updateFormData(formId: FormId) = Action.async(parse.json[FormData]) { implicit request =>
    //TODO: check form status. If after submission don't call this function
    //TODO authentication
    //TODO authorisation
    //TODO do we need to split form data into sections and update only part of the form data related to section? It will

    for {
      _ <- formService.updateFormData(formId, request.body)
    } yield NoContent
  }

  def validateSection(formId: FormId, sectionNumber: SectionNumber) = Action.async(parse.json[FormData]) { implicit request =>
    //TODO: check form status. If after submission don't call this function
    //TODO authentication
    //TODO authorisation
    //TODO wrap result into ValidationResult case class containign status of validation and list of errors

    val formData: FormData = request.body

    val result: Future[Either[UnexpectedState, Unit]] = for {
      formTemplate <- formTemplateService.get(formData.formTemplateId)
      section = getSection(formTemplate, sectionNumber)
    } yield FormValidator.validate(formData.fields.toList, section)

    result.map(_.fold(
      e => e.error,
      _ => "No errors"
    )).asOkJson
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

  private def formLink(form: Form)(implicit request: RequestHeader) = {
    //    val Form(formId, formData, envelopeId) = form
    //    routes.FormController.get(formId).absoluteURL()
    ???
  }

  private def getSection(formTemplate: FormTemplate, sectionNumber: SectionNumber): Section = {
    Try(formTemplate.sections(sectionNumber.value)).getOrElse(throw new BadRequestException(s"Wrong sectionNumber: $sectionNumber. There are ${formTemplate.sections.length} sections."))
  }
}
