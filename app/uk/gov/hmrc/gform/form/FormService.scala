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

import uk.gov.hmrc.gform.core._
import uk.gov.hmrc.gform.exceptions.UnexpectedState
import uk.gov.hmrc.gform.formtemplate.FormTemplateService
import uk.gov.hmrc.gform.models._
import uk.gov.hmrc.gform.models.api.formtemplate.FormTemplateId
import uk.gov.hmrc.gform.models.api.form.{ Form, FormData, FormId, UserId }
import uk.gov.hmrc.gform.save4later.Save4Later
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FormService(save4Later: Save4Later) {

  def get(formId: FormId)(implicit hc: HeaderCarrier): Future[Form] = {
    save4Later.get(formId)
  }

  def delete(formId: FormId)(implicit hc: HeaderCarrier): Future[Unit] = {
    save4Later.delete(formId)
  }

  def insertEmpty(userId: UserId, formTemplateId: FormTemplateId, envelopeId: EnvelopeId, formId: FormId)(implicit hc: HeaderCarrier): Future[Unit] = {
    val emptyFormData = FormData(fields = Nil)
    val form = Form(formId, envelopeId, userId, formTemplateId, emptyFormData)
    save4Later.upsert(formId, form)
  }

  def updateFormData(formId: FormId, formData: FormData)(implicit hc: HeaderCarrier): Future[Unit] = {
    for {
      form <- save4Later.get(formId)
      newForm = form.copy(formData = formData)
      _ <- save4Later.upsert(formId, newForm)
    } yield ()
  }

}
