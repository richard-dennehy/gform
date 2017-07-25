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

package uk.gov.hmrc.gform.formtemplate

import uk.gov.hmrc.gform.core._
import uk.gov.hmrc.gform.models._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import cats.data._
import cats.implicits._

class FormTemplateService(formTemplateRepo: FormTemplateRepo) {

  //TODO authorisation, especially form modifying template

  def get(formTemplateId: FormTemplateId): Future[FormTemplate] = formTemplateRepo.get(formTemplateId.value)

  def verifyAndSave(
    formTemplate: FormTemplate
  ): FOpt[Unit] = {

    val sectionsList = formTemplate.sections

    val exprs = sectionsList.flatMap(_.fields.map(_.`type`))

    // format: OFF
    for {
      _          <- fromOptA          (Section.validateChoiceHelpText(sectionsList).toEither)
      _          <- fromOptA          (Section.validateUniqueFields(sectionsList).toEither)
      _          <- fromOptA          (ComponentType.validate(exprs, formTemplate).toEither)
      _          <- fromOptA          (FormTemplateSchema.jsonSchema.conform(formTemplate).toEither)
      res        <- fromFutureOptA    (formTemplateRepo.upsert(formTemplate))
    } yield res
    // format: ON
  }

}
