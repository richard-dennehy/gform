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

import play.api.mvc.Action
import uk.gov.hmrc.gform.models._
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import cats.data._
import cats.implicits._
import play.api.libs.json.Json

class FormTemplatesController(
    formTemplateService: FormTemplateService
) extends BaseController {

  def save() = Action.async(parse.json[FormTemplate]) { request =>
    val formTemplate: FormTemplate = request.body
    val result = formTemplateService.verifyAndSave(formTemplate)
    result.fold(error => BadRequest(error.error), _ => Ok)
  }

  def get(formTemplateId: FormTemplateId) = Action.async { implicit request =>
    //    formTemplateService
    //      .get(formTypeId)
    //      .map {
    //        case Some(template) => Ok(Json.writes[FormTemplate].writes(template))
    //        case None => NotFound
    //      }
    ???
  }

  def remove(formTemplateId: FormTemplateId) = Action.async { implicit request =>
    //    formTemplateService
    //      .get(formTypeId)
    //
    //      .map {
    //        case Some(template) => Ok(Json.writes[FormTemplate].writes(template))
    //        case None => NotFound
    //      }

    ???
  }

  def formTemplateSchema() = Action {
    Ok(
      Json.toJson(FormTemplateSchema.schema)
    )
  }

  //all template ids
  def all() = play.mvc.Results.TODO
}

