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

package org.sourcegrade.jagrweb.core

import com.google.inject.AbstractModule
import org.slf4j.Logger
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Paths

class CoreModule(
    private val logger: Logger,
) : AbstractModule() {
    override fun configure() {
        bind(Logger::class.java).toInstance(logger)
        with(HoconConfigurationLoader.builder().path(Paths.get("./jagrweb.conf")).build()) {
            load().let { root ->
                if (root.empty()) Config().also { root.set(it).also(::save) }
                else root[Config::class.java]
            }.also(bind(Config::class.java)::toInstance)
        }
    }
}
