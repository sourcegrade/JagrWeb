/*
 *   JagrWeb - SourceGrade.org
 *   Copyright (C) 2021 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.sourcegrade.jagrweb.core.datastore

import com.google.inject.Inject
import com.google.inject.Singleton
import com.mongodb.client.MongoClients
import dev.morphia.Datastore
import dev.morphia.Morphia
import org.sourcegrade.jagrweb.core.Config
import org.sourcegrade.jagrweb.core.model.Assignment
import org.sourcegrade.jagrweb.core.model.Grader
import org.sourcegrade.jagrweb.core.model.GraderUpload
import org.sourcegrade.jagrweb.core.model.GradingBatch
import org.sourcegrade.jagrweb.core.model.GradingBatchTemplate
import org.sourcegrade.jagrweb.core.model.GradingJob
import org.sourcegrade.jagrweb.core.model.SolutionUpload
import org.sourcegrade.jagrweb.core.model.Submission
import org.sourcegrade.jagrweb.core.model.SubmissionUpload
import org.sourcegrade.jagrweb.core.model.User

@Singleton
class MongoContext @Inject constructor(
    config: Config,
) {

    val dataStore: Datastore = Morphia.createDatastore(MongoClients.create(config.mongodbConnection), "jagr")

    init {
        dataStore.mapper.map(
            Assignment::class.java,
            Grader::class.java,
            GraderUpload::class.java,
            GradingBatch::class.java,
            GradingBatchTemplate::class.java,
            GradingJob::class.java,
            SolutionUpload::class.java,
            Submission::class.java,
            SubmissionUpload::class.java,
            User::class.java,
        )
        dataStore.ensureIndexes()
    }
}
