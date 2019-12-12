/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.cdsimportsdds.controllers

import java.util.UUID

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, ControllerComponents, Result}
import uk.gov.hmrc.cdsimportsdds.config.AppConfig
import uk.gov.hmrc.cdsimportsdds.models.ImportsDeclaration
import uk.gov.hmrc.cdsimportsdds.services.DeclarationService
import uk.gov.hmrc.cdsimportsdds.util.RESTFormatters.formatImportsDeclaration
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class DeclarationController @Inject()(
                                       appConfig: AppConfig,
                                       declarationService: DeclarationService,
                                       override val controllerComponents: ControllerComponents
                                     )(implicit executionContext: ExecutionContext)
  extends RESTController(controllerComponents) {

  def create(): Action[ImportsDeclarationRequest] = Action.async(parsingJson[ImportsDeclarationRequest]) { implicit request =>
    val eori = request.headers.get("X-EORI-Number")
    eori match {
      case Some(eori) => saveImportsDeclaration(request.body, eori)
      case _ => Future.successful(BadRequest(Json.toJson(ErrorResponse("X-EORI-Number header missing"))))
    }
  }

  private def saveImportsDeclaration(importsDeclarationRequest: ImportsDeclarationRequest, eori: String)(implicit hc: HeaderCarrier): Future[Result] = {
    val importsDeclaration: ImportsDeclaration =
      importsDeclarationRequest.toImportsDeclaration(id = UUID.randomUUID().toString, eori)
    declarationService
      .create(importsDeclaration)
      .map(declaration => Created(declaration))
  }

}