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

import play.api.mvc.{ Action, BodyParser }
import uk.gov.hmrc.gform.models._
import uk.gov.hmrc.gform.core._

import scala.concurrent.ExecutionContext.Implicits.global
import cats.data._
import cats.implicits._
import play.api.libs.json.{ JsResult, JsValue, Json }
import uk.gov.hmrc.gform.controllers.{ BaseController, ErrResponse }
import uk.gov.hmrc.gform.exceptions.UnexpectedState
import uk.gov.hmrc.play.http.NotImplementedException

import scala.concurrent.Future
import cats.data._
import cats.implicits._
import play.api.mvc.Result

class FormTemplatesController(
    formTemplateService: FormTemplateService
) extends BaseController {

  def upsert() = Action.async(parse.json[FormTemplateRaw]) { request =>
    //TODO authorisation (we don't want allow everyone to call this action

    val templateRaw = request.body

    val formTemplateOpt: Opt[FormTemplate] = Json.reads[FormTemplate].reads(templateRaw.value).fold(
      errors => UnexpectedState(errors.toString()).asLeft,
      valid => valid.asRight
    )

    val result = for {
      ft <- fromOptA(formTemplateOpt)
      _ <- formTemplateService.verifyAndSave(ft)
      _ <- formTemplateService.save(templateRaw)
    } yield ()

    result.fold(
      us => us.asBadRequest,
      _ => NoContent

    )
  }

  def get(id: FormTemplateId) = Action.async { implicit request =>
    formTemplateService
      .get(id)
      .asOkJson
  }

  def getRaw(id: FormTemplateRawId) = Action.async { implicit request =>
    formTemplateService
      .get(id)
      .asOkJson
  }

  def remove(formTemplateId: FormTemplateId) = Action.async { implicit request =>
    //TODO authorisation (we don't want allow everyone to call this action

    val result = for {
      r <- formTemplateService.delete(formTemplateId)
    } yield r

    result.fold(
      _.asBadRequest,
      _ => NoContent
    )
  }

  def all() = Action.async { implicit request =>

    formTemplateService
      .list()
      .map(_.map(_._id))
      .asOkJson
  }
}
