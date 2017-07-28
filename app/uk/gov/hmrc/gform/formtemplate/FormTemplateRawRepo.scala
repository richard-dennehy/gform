package uk.gov.hmrc.gform.formtemplate

import reactivemongo.api.DefaultDB
import uk.gov.hmrc.gform.models.FormTemplateRaw
import uk.gov.hmrc.gform.repo.Repo

class FormTemplateRawRepo(mongo: () => DefaultDB) extends Repo[FormTemplateRaw]("formTemplateRaw", mongo, _._id.value)
