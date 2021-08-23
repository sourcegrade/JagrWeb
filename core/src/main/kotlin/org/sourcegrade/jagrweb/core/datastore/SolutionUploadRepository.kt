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
import org.slf4j.Logger
import org.sourcegrade.jagrweb.core.model.Grader
import org.sourcegrade.jagrweb.core.model.SolutionUpload
import org.sourcegrade.jagrweb.core.model.toArchive

class SolutionUploadRepository @Inject constructor(
    private val logger: Logger,
) : ObjectIdRepository<SolutionUpload>() {
    override val tClass: Class<SolutionUpload> = SolutionUpload::class.java
    override fun insertOne(item: SolutionUpload): SolutionUpload? {
        return verifyUpload(
            Grader::class, Grader::solutionUploads, SolutionUpload::toArchive,
            item.graderId, logger, item,
        ) { grader, solutionUpload ->
            super.insertOne(solutionUpload)
        }
    }
}
