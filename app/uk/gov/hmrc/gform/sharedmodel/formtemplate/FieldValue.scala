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

package uk.gov.hmrc.gform.sharedmodel.formtemplate

import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.gform.formtemplate.FieldValueMaker
import uk.gov.hmrc.gform.sharedmodel._

case class FieldValue(
  id: FieldId,
  `type`: ComponentType,
  label: String,
  helpText: Option[String],
  shortName: Option[String],
  mandatory: Boolean,
  editable: Boolean,
  submissible: Boolean,
  presentationHint: Option[PresentationHintExpr] = None

)

object FieldValue {

  implicit val format: OFormat[FieldValue] = {
    implicit val formatFieldValue = Json.format[FieldValue]

    val toFieldValue: Reads[FieldValue] = Reads[FieldValue] { (json: JsValue) => new FieldValueMaker(json).optFieldValue() fold (us => JsError(us.toString), fv => JsSuccess(fv)) }

    val reads: Reads[FieldValue] = (formatFieldValue: Reads[FieldValue]) | toFieldValue

    OFormat[FieldValue](reads, formatFieldValue)
  }
}
