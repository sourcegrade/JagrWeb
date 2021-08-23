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
import org.sourcegrade.jagrweb.core.datastore.GraderRepository
import org.sourcegrade.jagrweb.core.serializer.ObjectIdSerializer

@Entity("assignments")
@Serializable
class Assignment : ObjectWithId.ObjectWithStringId() {
    var name: String? = null
    var graders: List<Grader.Selector>? = null

    @Entity(discriminator = "Assignment.Archive")
    @Serializable
    data class Archive(
        var id: String? = null,
        var name: String? = null,
        var graderUploads: List<GraderUpload.Archive>? = null,
    )
}

fun Assignment.toArchive(graderRepository: GraderRepository): Assignment.Archive {
    val graderUploads = graders?.mapNotNull { graderRepository.getSelectedUpload(it) }
    return Assignment.Archive(getId(), name, graderUploads)
}
