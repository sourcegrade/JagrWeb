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
import org.sourcegrade.jagrweb.core.model.GraderUpload
import org.sourcegrade.jagrweb.core.model.toArchive

class GraderUploadRepository @Inject constructor(
    private val logger: Logger,
) : ObjectIdRepository<GraderUpload>() {
    override val tClass: Class<GraderUpload> = GraderUpload::class.java
    override fun insertOne(item: GraderUpload): GraderUpload? {
        return verifyUpload(
            Grader::class, Grader::uploads, GraderUpload::toArchive,
            item.graderId, logger, item,
        ) { _, upload ->
            super.insertOne(upload)
        }
    }
}
