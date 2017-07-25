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

package uk.gov.hmrc.gform.models

import play.api.libs.json.{ JsObject, JsValue, Json, OFormat }
import uk.gov.hmrc.gform.core.{ JsonSchema, Opt, SchemaValidator }
import cats.data._
import cats.implicits._
import play.api.http.Writeable

import scala.util.Try

case class FormTemplate(
    _id: FormTemplateId,
    formName: String,
    description: String,
    characterSet: String,
    dmsSubmission: DmsSubmission,
    submitSuccessUrl: String,
    submitErrorUrl: String,
    sections: List[Section]
) {

  def section(sectionNumber: SectionNumber): Option[Section] = Try(sections(sectionNumber.value)).map(Some(_)).getOrElse(None)
}

object FormTemplate {
  implicit val format: OFormat[FormTemplate] = Json.format[FormTemplate]
}

case class FormTemplateSchema(value: JsObject)

object FormTemplateSchema {

  val schema: FormTemplateSchema = {
    val json: JsObject = Json.parse(
      getClass.getResourceAsStream("formTemplateSchema.json")
    ).as[JsObject]
    FormTemplateSchema(json)
  }

  val jsonSchema: JsonSchema = SchemaValidator.conform(schema).fold(
    _ => throw new UnsupportedOperationException("Looks like we have currupted schema file: formTemplateSchema.json"),
    identity
  )

  implicit val format: OFormat[FormTemplateSchema] = Json.format[FormTemplateSchema]
}