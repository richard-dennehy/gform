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

package uk.gov.hmrc.gform.save4later

import uk.gov.hmrc.gform.models._
import uk.gov.hmrc.gform.models.api.form.Form
import uk.gov.hmrc.http.cache.client.ShortLivedCache
import uk.gov.hmrc.play.http.{ HeaderCarrier, NotFoundException }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }

class Save4Later(cache: ShortLivedCache, ex: ExecutionContext) {

  def find(formId: FormId)(implicit hc: HeaderCarrier): Future[Option[Form]] =
    cache.fetchAndGetEntry[Form](formId.value, formCacheKey)

  def get(formId: FormId)(implicit hc: HeaderCarrier): Future[Form] =
    find(formId) map {
      //use the same API as using WSHTTP
      case None => throw new NotFoundException(s"Not found 'form' for the given id: '${formId.value}'")
      case Some(form) => form
    }

  //  def get(formId: FormId)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[List[Form]] = {
  //    cache.fetch(formId.value).map {
  //      case Some(x) =>
  //        x.data.values.flatMap { json =>
  //          Json.fromJson[Form](json) match {
  //            case JsSuccess(y, _) => List(y)
  //            case JsError(err) => List.empty[Form]
  //          }
  //        }.toList
  //      case None => List.empty[Form]
  //    }
  //  }

  //  def put(formId: FormId, form: Form)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[Opt[Unit]] = {
  //    findOne(formId).flatMap {
  //      case None => save(formId, form)
  //      case Some(x) =>
  //        val fields = x.formData.fields ++ form.formData.fields
  //        val concatenatedFormData: FormData = form.formData.copy(fields = fields)
  //        save(formId, form.copy(formData = concatenatedFormData))
  //    }
  //  }

  def upsert(formId: FormId, form: Form)(implicit hc: HeaderCarrier): Future[Unit] = {
    cache.cache[Form](formId.value, formCacheKey, form).map(_ => ())
  }

  def delete(formId: FormId)(implicit hc: HeaderCarrier): Future[Unit] = {
    cache.remove(formId.value).map(_ => ())
  }

  private lazy val formCacheKey = "form"
}
