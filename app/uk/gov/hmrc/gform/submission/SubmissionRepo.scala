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

package uk.gov.hmrc.gform.submission

import reactivemongo.api.DefaultDB
import uk.gov.hmrc.gform.models.{ Form, Submission }
import uk.gov.hmrc.gform.repo.Repo

//TODO this should be replaced with Save4Later since there are user specific data

class SubmissionRepo(mongo: () => DefaultDB) extends Repo[Submission]("submission", mongo, _._id.value)