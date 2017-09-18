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

import play.api.libs.json.JsValue
import uk.gov.hmrc.gform.core.{ fromFutureOptA, fromFutureOptionA }
import uk.gov.hmrc.gform.save4later.Save4Later
import uk.gov.hmrc.gform.sharedmodel.UserId
import uk.gov.hmrc.gform.sharedmodel.form._
import uk.gov.hmrc.gform.sharedmodel.formtemplate.FormTemplateId
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
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
    val form = Form(formId, envelopeId, userId, formTemplateId, None, emptyFormData)
    save4Later.upsert(formId, form)
  }

  def updateUserData(formId: FormId, userData: UserData)(implicit hc: HeaderCarrier): Future[Unit] = {
    for {
      form <- save4Later.get(formId)
      newForm = form
        .copy(formData = userData.formData)
        .copy(repeatingGroupStructure = userData.repeatingGroupStructure)
      _ <- save4Later.upsert(formId, newForm)
    } yield ()
  }

  def saveKeyStore(formId: FormId, data: Map[String, JsValue])(implicit hc: HeaderCarrier): Future[Unit] = save4Later.saveKeyStore(formId, data)

  def getKeyStore(formId: FormId)(implicit hc: HeaderCarrier): Future[Option[Map[String, JsValue]]] = save4Later.getKeyStore(formId)

}
