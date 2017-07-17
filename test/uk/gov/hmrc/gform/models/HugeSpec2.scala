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

import org.scalatest._

/** self-referencing case class - compiles but throws NPE */
class HugeSpec2 extends FlatSpec with Matchers {
  "json" should "parse into Huge" in {

    import play.api.libs.functional.syntax._
    import play.api.libs.json._

    case class Huge(a: Int, b: String, c: Boolean, d: List[Huge])

    object s {
      val f: ((Int, String), (Boolean, List[Huge])) => Huge = {
        case ((a, b), (c, d)) => Huge(a, b, c, d)
      }

      implicit val hugeCaseClassReads: Reads[Huge] = (
        fields1to2 and fields3to4
      ) {
          f
        }

      lazy val fields1to2: Reads[(Int, String)] = (
        (__ \ "a").read[Int] and
        (__ \ "b").read[String]
      ).tupled

      lazy val fields3to4: Reads[(Boolean, List[Huge])] = (
        (__ \ "c").read[Boolean] and
        (__ \ "d").read[List[Huge]]
      ).tupled

    }

    val jsonStr =
      """
      {
        "a": 3,
        "b": "gid",
        "c": true,
        "d": [
          {
           "a": 3,
           "b": "gid",
           "c": true,
           "d": []
          }
        ]
      }
    """

    import s._

    val res = Json.parse(jsonStr).validate[Huge]

    res match {
      case s: JsSuccess[Huge] => println(s.get)
      case e: JsError => println(e)
    }
  }

}
