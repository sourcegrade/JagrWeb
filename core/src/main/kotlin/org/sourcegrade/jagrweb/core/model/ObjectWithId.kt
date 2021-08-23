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
@file:UseSerializers(serializerClasses = [InstantSerializer::class, ObjectIdSerializer::class])

package org.sourcegrade.jagrweb.core.model

import dev.morphia.annotations.Id
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId
import org.sourcegrade.jagrweb.core.serializer.InstantSerializer
import org.sourcegrade.jagrweb.core.serializer.ObjectIdSerializer

sealed interface ObjectWithId<TKey : Comparable<TKey>> {

    fun getId(): TKey?

    @Serializable
    open class ObjectWithObjectId : ObjectWithId<ObjectId> {
        @Id
        @Serializable(ObjectIdSerializer::class)
        private var id: ObjectId? = null
        override fun getId(): ObjectId? = id
    }

    @Serializable
    open class ObjectWithStringId : ObjectWithId<String> {
        @Id
        private var id: String? = null
        override fun getId(): String? = id
    }
}
