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

package uk.gov.hmrc.cdsimportsdds.repositories

import com.kenshoo.play.metrics.Metrics
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import uk.gov.hmrc.cdsimportsdds.config.AppConfig
import uk.gov.hmrc.cdsimportsdds.models.ImportsDeclaration
import MongoFormatters.formatDeclaration
import reactivemongo.api.indexes.{Index, IndexType}
import uk.gov.hmrc.cdsimportsdds.domain.Eori
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.objectIdFormats

import scala.concurrent.{ExecutionContext, Future}

class DeclarationRepository @Inject()(mc: ReactiveMongoComponent, appConfig: AppConfig, metrics: Metrics)(implicit ec: ExecutionContext)
  extends ReactiveRepository[ImportsDeclaration, BSONObjectID](
    "declarations",
    mc.mongoConnector.db,
    formatDeclaration,
    objectIdFormats
  ) {

  override def indexes: Seq[Index] = Seq(
    Index(Seq("eori" -> IndexType.Ascending, "id" -> IndexType.Ascending), Some("eoriAndIdIdx"), unique = true),
    Index(Seq("submissionDateTime" -> IndexType.Ascending), Some("declarationTtlIdx"),
      options = BSONDocument("expireAfterSeconds" -> appConfig.submittedDeclarationRetentionPeriod.toSeconds))
  )

  def create(declaration: ImportsDeclaration): Future[ImportsDeclaration] =
    super.insert(declaration).map(_ => declaration)

  def findByEori(eori: Eori): Future[Seq[ImportsDeclaration]] =
    super.find("eori" -> eori)

}

