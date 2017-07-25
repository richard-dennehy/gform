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

package uk.gov.hmrc.gform.playcomponents

import akka.stream.Materializer
import com.kenshoo.play.metrics.{ MetricsController, MetricsFilter, MetricsFilterImpl, MetricsImpl }
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import uk.gov.hmrc.gform.ApplicationModule
import uk.gov.hmrc.gform.akka.AkkaModule
import uk.gov.hmrc.gform.auditing.AuditingModule
import uk.gov.hmrc.gform.config.ConfigModule
import uk.gov.hmrc.gform.form.FormModule
import uk.gov.hmrc.gform.formtemplate.FormTemplateModule
import uk.gov.hmrc.gform.metrics.MetricsModule
import uk.gov.hmrc.gform.testonly.TestOnlyModule
import uk.gov.hmrc.play.audit.filters.AuditFilter
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.auth.controllers.AuthParamsControllerConfig
import uk.gov.hmrc.play.auth.microservice.connectors.AuthConnector
import uk.gov.hmrc.play.auth.microservice.filters.AuthorisationFilter
import uk.gov.hmrc.play.filters.{ NoCacheFilter, RecoveryFilter }
import uk.gov.hmrc.play.health.AdminController
import uk.gov.hmrc.play.http.logging.filters.LoggingFilter

class PlayComponentsModule(
    playComponents: PlayComponents,
    akkaModule: AkkaModule,
    configModule: ConfigModule,
    auditingModule: AuditingModule,
    metricsModule: MetricsModule,
    formModule: FormModule,
    formTemplateModule: FormTemplateModule,
    testOnlyModule: TestOnlyModule
) {

  lazy val loggingFilter = new LoggingFilter {
    override def mat: Materializer = akkaModule.materializer
    override def controllerNeedsLogging(controllerName: String): Boolean = configModule.controllerConfig.paramsForController(controllerName).needsLogging
  }

  lazy val microserviceAuthConnector = new AuthConnector {
    override def authBaseUrl: String = configModule.serviceConfig.baseUrl("auth")
  }

  lazy val authFilter = new AuthorisationFilter {
    override def mat: Materializer = akkaModule.materializer
    override lazy val authParamsConfig: AuthParamsControllerConfig = configModule.authParamsControllerConfig
    override lazy val authConnector: AuthConnector = microserviceAuthConnector
    override def controllerNeedsAuth(controllerName: String): Boolean = configModule.controllerConfig.paramsForController(controllerName).needsAuth
  }

  lazy val appRoutes = new app.Routes(
    errorHandler,
    formModule.formController,
    formTemplateModule.formTemplatesController,
    testOnlyModule.testOnlyController
  )

  //must be lazy becouse other's depend on it
  lazy val routerVal: Router = new prod.Routes(
    errorHandler,
    appRoutes,
    //    health.Routes,
    metricsModule.metricsController
  )

  def router: Router = routerVal

  private lazy val someRouter = Some(router)

  lazy val errorHandler = new ErrorHandler(
    playComponents.context.environment,
    playComponents.context.initialConfiguration,
    playComponents.context.sourceMapper,
    router
  )

  lazy val httpFilters: Seq[EssentialFilter] = Seq(
    metricsModule.metricsFilter,
    auditingModule.microserviceAuditFilter,
    loggingFilter,
    //    authFilter, it thorws exception instead of working ...
    NoCacheFilter,
    RecoveryFilter
  )

  lazy val httpRequestHandler = new CustomHttpRequestHandler(
    router,
    errorHandler,
    playComponents.builtInComponents.httpConfiguration,
    httpFilters
  )

}
