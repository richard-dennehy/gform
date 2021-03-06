/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.gform.sharedmodel.formtemplate

import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.gform.formtemplate.FormCompomentMaker

case class FormComponent(
  id: FormComponentId,
  `type`: ComponentType,
  label: String,
  helpText: Option[String],
  shortName: Option[String],
  validIf: Option[ValidIf],
  mandatory: Boolean,
  editable: Boolean,
  submissible: Boolean,
  derived: Boolean,
  onlyShowOnSummary: Boolean = false,
  errorMessage: Option[String],
  presentationHint: Option[List[PresentationHint]] = Option.empty[List[PresentationHint]])

object FormComponent {

  //TODO: remove this logic from case class representing data
  implicit val format: OFormat[FormComponent] = {
    implicit val formatFieldValue = Json.format[FormComponent]

    val toFieldValue: Reads[FormComponent] = Reads[FormComponent] { (json: JsValue) => new FormCompomentMaker(json).optFieldValue() fold (us => JsError(us.toString), fv => JsSuccess(fv)) }

    val reads: Reads[FormComponent] = (formatFieldValue: Reads[FormComponent]) | toFieldValue

    OFormat[FormComponent](reads, formatFieldValue)
  }
}
