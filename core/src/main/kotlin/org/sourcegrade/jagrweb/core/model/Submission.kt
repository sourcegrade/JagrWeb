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
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId
import org.sourcegrade.jagrweb.core.datastore.SubmissionUploadRepository
import org.sourcegrade.jagrweb.core.datastore.UserRepository
import org.sourcegrade.jagrweb.core.serializer.ObjectIdSerializer
import java.time.Instant

@Entity("submissions")
@Serializable
class Submission : ObjectWithId.ObjectWithObjectId() {
    var ownerId: ObjectId? = null
    var name: String? = null
    var info: Info? = null
    var uploads: List<SubmissionUpload.Archive>? = null

    @Entity(discriminator = "Submission.Info")
    @Serializable
    class Info {
        var assignmentId: String? = null
        var studentId: String? = null
        var firstName: String? = null
        var lastName: String? = null
    }

    @Entity(discriminator = "Submission.Selector")
    @Serializable
    class Selector {
        var id: ObjectId? = null
        var version: Version? = null
    }

    @Entity(discriminator = "Submission.Archive")
    @Serializable
    class Archive {
        var id: ObjectId? = null
        var ownerId: ObjectId? = null
        var name: String? = null
        var info: Info? = null
    }

    @Serializable
    data class Dto(
        val id: ObjectId? = null,
        val owner: User? = null,
        val name: String? = null,
        val info: Info? = null,
        val status: String? = null,
        val uploads: List<SubmissionUpload.Dto>? = null,
    )
}

fun Submission.toDto(
    submissionUploadRepository: SubmissionUploadRepository,
    userRepository: UserRepository,
): Submission.Dto {
    val owner = ownerId?.let(userRepository::getOne)
    val uploads = uploads?.run {
        asSequence()
            .mapNotNull { it.id }
            .mapNotNull { submissionUploadRepository.getOne(it, projectSubmissionUploadArchive()) }
            .map { it.toDto() }
            .toList()
    }
    val status = uploads?.run {
        asSequence()
            .mapNotNull { it.jobs }
            .flatten()
            .maxByOrNull { it.finishedUtc ?: Instant.EPOCH }
            ?.status
    } ?: "success"
    return Submission.Dto(getId(), owner, name, info, status, uploads)
}
