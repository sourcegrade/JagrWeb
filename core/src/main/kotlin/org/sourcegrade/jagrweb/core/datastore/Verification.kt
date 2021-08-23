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

import dev.morphia.query.experimental.filters.Filters
import dev.morphia.query.experimental.updates.AddToSetOperator
import org.slf4j.Logger
import org.sourcegrade.jagrweb.core.model.ObjectWithId
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

fun <TKey : Comparable<TKey>, T : ObjectWithId<TKey>, TParent : ObjectWithId<*>> Repository<TKey, T>.verifyUpload(
    parentType: KClass<TParent>,
    parentUploadProperty: KProperty1<TParent, List<*>?>,
    uploadToArchive: (T) -> Any,
    parentId: Any?,
    logger: Logger,
    upload: T,
    insertFun: (TParent, T) -> T?,
): T? {
    val parent = context.dataStore.find(parentType.java)
        .filter(Filters.eq("_id", parentId))
        .firstOrNull() ?: throw IllegalArgumentException("Could not find ${parentType.simpleName} with id $parentId")
    val inserted = insertFun(parent, upload) ?: return null
    val uploadId = inserted.getId() ?: return null
    val updateResult = context.dataStore.find(parentType.java)
        .filter(Filters.eq("_id", parentId))
        .update(AddToSetOperator(parentUploadProperty.name, uploadToArchive(inserted))).execute()
    if (updateResult.modifiedCount != 1L && asQuery(uploadId).delete().deletedCount != 1L) {
        logger.error("Unable to remove ${parentType.simpleName} upload $uploadId!")
    }
    return inserted
}
