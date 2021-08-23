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
import org.sourcegrade.jagrweb.core.serializer.ObjectIdSerializer

@Entity("graders")
@Serializable
class Grader : ObjectWithId.ObjectWithObjectId() {
    var name: String? = null
    var assignmentIds: List<String>? = null
    var uploads: List<GraderUpload.Archive>? = null
    var solutionUploads: List<SolutionUpload.Archive>? = null

    @Entity(discriminator = "Grader.Selector")
    @Serializable
    class Selector {
        var id: ObjectId? = null
        var version: Version? = null
    }
}
