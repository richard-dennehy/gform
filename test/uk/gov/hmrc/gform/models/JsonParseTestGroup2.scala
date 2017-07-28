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

import cats.data.NonEmptyList
import play.api.libs.json._
import uk.gov.hmrc.gform.Spec

class JsonParseTestGroup2 extends Spec {

  "A raw group" should "fail to parse if repeatsMin/Max has errors" in {

    val jsonStr =
      """
      {
        "type": "group",
        "id": "gid",
        "label": "glabel",
        "format" : "horizontal",
        "repeatsMin":6,
        "repeatsMax":5,
        "repeatLabel":"repeatLabel",
        "repeatAddAnotherText":"repeatAddAnotherText",
        "fields": [
          {
            "type": "choice",
            "id": "cid",
            "label": "clabel",
            "choices": [
              "A",
              "B"
            ]
          }
        ],
        "presentationHint" : "collapseGroupUnderLabel"

      }
    """

    val res = (Json.parse(jsonStr) \ "repeatsMax").validateOpt[Int]
    val s = jsonStr.replaceAll("5", """"A"""")

    println(s)
    val res2 = (Json.parse(s) \ "repeatsMax").validateOpt[Int]

    val res3 = (Json.parse(s) \ "presentationHint").validateOpt[String]
    val res4 = (Json.parse(jsonStr.replaceAll(""""collapseGroupUnderLabel"""", """123""")) \ "presentationHint").validateOpt[String]

    println(res3)

  }

}
