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
@file:UseSerializers(serializerClasses = [ObjectIdSerializer::class])

package org.sourcegrade.jagrweb.core.model

import dev.morphia.annotations.Entity
import dev.morphia.query.FindOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId
import org.sourcegrade.jagrweb.core.serializer.ObjectIdSerializer

@Entity("submission-uploads")
@Serializable
class SubmissionUpload : ObjectWithId.ObjectWithObjectId() {
    var submissionId: ObjectId? = null
    var version: Version? = null
    var jobs: List<GradingJob.Reference>? = null
    var data: ByteArray? = null

    @Entity(discriminator = "SubmissionUpload.Archive")
    @Serializable
    data class Archive(
        val id: ObjectId? = null,
        val submissionId: ObjectId? = null,
        val version: Version? = null,
    )

    @Serializable
    data class Dto(
        val id: ObjectId? = null,
        val submissionId: ObjectId? = null,
        val version: Version? = null,
        val jobs: List<GradingJob.Reference>? = null,
    )
}

/**
 * Does not create a finished Archive object, only fetches the data required to construct one
 */
fun projectSubmissionUploadArchive(): FindOptions = FindOptions().projection().include("submissionId", "version")
fun SubmissionUpload.toArchive() = SubmissionUpload.Archive(getId(), submissionId, version)
fun SubmissionUpload.toDto() = SubmissionUpload.Dto(getId(), submissionId, version, jobs)
