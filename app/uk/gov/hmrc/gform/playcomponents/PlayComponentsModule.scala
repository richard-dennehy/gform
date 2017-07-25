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
import uk.gov.hmrc.gform.ApplicationModule
import uk.gov.hmrc.gform.auditing.AuditingModule
import uk.gov.hmrc.gform.config.ConfigModule
import uk.gov.hmrc.play.audit.filters.AuditFilter
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.auth.controllers.AuthParamsControllerConfig
import uk.gov.hmrc.play.auth.microservice.connectors.AuthConnector
import uk.gov.hmrc.play.auth.microservice.filters.AuthorisationFilter
import uk.gov.hmrc.play.filters.{ NoCacheFilter, RecoveryFilter }
import uk.gov.hmrc.play.http.logging.filters.LoggingFilter

class PlayComponentsModule(
    applicationModule: ApplicationModule,
    configModule: ConfigModule,
    auditingModule: AuditingModule
) {

  // Don't use uk.gov.hmrc.play.graphite.GraphiteMetricsImpl as it won't allow hot reload due to overridden onStop() method
  val metrics = new MetricsImpl(
    applicationModule.applicationLifecycle,
    applicationModule.configuration
  )

  val metricsFilter: MetricsFilter = new MetricsFilterImpl(metrics)(applicationModule.materializer)

  val metricsController = new MetricsController(metrics)

  val microserviceAuditFilter = new AuditFilter {
    override val appName: String = configModule.appConfig.appName
    override def mat: Materializer = applicationModule.materializer
    override val auditConnector: AuditConnector = auditingModule.auditConnector
    override def controllerNeedsAuditing(controllerName: String): Boolean = configModule.controllerConfig.paramsForController(controllerName).needsAuditing
  }

  val loggingFilter = new LoggingFilter {
    override def mat: Materializer = applicationModule.materializer
    override def controllerNeedsLogging(controllerName: String): Boolean = configModule.controllerConfig.paramsForController(controllerName).needsLogging
  }

  val microserviceAuthConnector = new AuthConnector {
    override def authBaseUrl: String = configModule.serviceConfig.baseUrl("auth")
  }

  val authFilter = new AuthorisationFilter {
    override def mat: Materializer = applicationModule.materializer
    override lazy val authParamsConfig: AuthParamsControllerConfig = configModule.authParamsControllerConfig
    override lazy val authConnector: AuthConnector = microserviceAuthConnector
    override def controllerNeedsAuth(controllerName: String): Boolean = configModule.controllerConfig.paramsForController(controllerName).needsAuth
  }

  lazy val errorHandling = new CustomErrorHandling(
    auditingModule.auditConnector,
    configModule.appConfig.appName,
    applicationModule.environment,
    applicationModule.configuration,
    applicationModule.sourceMapper,
    Some(applicationModule.router)
  )

  val httpFilters: Seq[EssentialFilter] = Seq(
    metricsFilter,
    microserviceAuditFilter,
    loggingFilter,
    authFilter,
    NoCacheFilter,
    RecoveryFilter
  )

  val httpRequestHandler = new CustomHttpRequestHandler(
    applicationModule.router,
    errorHandling,
    applicationModule.httpConfiguration,
    applicationModule.httpFilters
  )
}
