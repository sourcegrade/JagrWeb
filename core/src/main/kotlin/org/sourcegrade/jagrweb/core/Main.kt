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

import com.google.inject.Guice
import com.google.inject.Injector
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.sourcegrade.jagrweb.core.http.Configurable
import org.sourcegrade.jagrweb.core.http.RepositoryRouting
import org.sourcegrade.jagrweb.core.http.SinglePageApplication

fun main() {
    val logger = LoggerFactory.getLogger("JagrWeb")
    logger.info("Starting initialization")
    val injector = Guice.createInjector(CoreModule(logger))
    val config = injector.getInstance(Config::class.java)
    val server = embeddedServer(
        Netty,
        port = config.webPort,
        host = config.webHost,
    ) {
        module(injector)
    }.start()
    Runtime.getRuntime().addShutdownHook(Thread {
        logger.info("Shutting down...")
        server.stop(500, 1000)
    })
}

fun Application.module(injector: Injector) {
    install(ContentNegotiation) {
        json(
            Json {
                encodeDefaults = false
                ignoreUnknownKeys = true
            }
        )
    }
    install(SinglePageApplication) {
        folderPath = "static"
        ignoreIfContains = Regex("^/api.*$")
    }
    routing {
        configure<Route, RepositoryRouting>(injector)
    }
}

inline fun <R, reified T : Configurable<R>> R.configure(injector: Injector) {
    with(injector.getInstance(T::class.java)) {
        configure()
    }
}
