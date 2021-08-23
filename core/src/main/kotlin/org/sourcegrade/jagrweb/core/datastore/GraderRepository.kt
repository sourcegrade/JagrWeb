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

import dev.morphia.query.FindOptions
import org.sourcegrade.jagrweb.core.model.Grader
import org.sourcegrade.jagrweb.core.model.GraderUpload
import org.sourcegrade.jagrweb.core.model.Version

class GraderRepository : ObjectIdRepository<Grader>() {
    override val tClass: Class<Grader> = Grader::class.java
    fun getSelectedUpload(graderSelector: Grader.Selector): GraderUpload.Archive? {
        val id = graderSelector.id ?: return null
        val uploads = asQuery(id).first(FindOptions().projection().include("uploads"))?.uploads ?: return null
        val version = graderSelector.version
        return if (version == null) {
            uploads.maxByOrNull { it.version ?: Version() }
        } else {
            uploads.first { it.version == version }
        }
    }
}
