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
import org.sourcegrade.jagrweb.core.model.Submission
import org.sourcegrade.jagrweb.core.model.SubmissionUpload
import org.sourcegrade.jagrweb.core.model.Version
import org.sourcegrade.jagrweb.core.model.toArchive

class SubmissionUploadRepository @Inject constructor(
    private val logger: Logger,
) : ObjectIdRepository<SubmissionUpload>() {
    override val tClass: Class<SubmissionUpload> = SubmissionUpload::class.java
    override fun insertOne(item: SubmissionUpload): SubmissionUpload? {
        return verifyUpload(
            Submission::class, Submission::uploads, SubmissionUpload::toArchive,
            item.submissionId, logger, item,
        ) { submission, upload ->
            val version = upload.version
            if (version == null) {
                upload.version = submission.uploads
                    ?.asSequence()
                    ?.mapNotNull { it.version }
                    ?.maxOrNull()
                    ?.apply { ++patch }
                    ?: Version(minor = 1)
            } else if (submission.uploads?.any { it.version == version } == true) {
                throw IllegalArgumentException("Version $version already exists in submission!")
            }
            super.insertOne(upload)
        }
    }
}
