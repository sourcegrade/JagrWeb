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

package org.sourcegrade.jagrweb.core.http

import com.google.inject.Inject
import dev.morphia.query.UpdateException
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.sourcegrade.jagrweb.core.datastore.AssignmentRepository
import org.sourcegrade.jagrweb.core.datastore.GraderRepository
import org.sourcegrade.jagrweb.core.datastore.GraderUploadRepository
import org.sourcegrade.jagrweb.core.datastore.GradingBatchRepository
import org.sourcegrade.jagrweb.core.datastore.GradingBatchTemplateRepository
import org.sourcegrade.jagrweb.core.datastore.Repository
import org.sourcegrade.jagrweb.core.datastore.SolutionUploadRepository
import org.sourcegrade.jagrweb.core.datastore.SubmissionRepository
import org.sourcegrade.jagrweb.core.datastore.SubmissionUploadRepository
import org.sourcegrade.jagrweb.core.datastore.UserRepository
import org.sourcegrade.jagrweb.core.model.ObjectWithId
import org.sourcegrade.jagrweb.core.model.toArchive
import org.sourcegrade.jagrweb.core.model.toDto

class RepositoryRouting @Inject constructor(
    private val assignmentRepository: AssignmentRepository,
    private val graderRepository: GraderRepository,
    private val graderUploadRepository: GraderUploadRepository,
    private val gradingBatchRepository: GradingBatchRepository,
    private val gradingBatchTemplateRepository: GradingBatchTemplateRepository,
    private val solutionUploadRepository: SolutionUploadRepository,
    private val submissionRepository: SubmissionRepository,
    private val submissionUploadRepository: SubmissionUploadRepository,
    private val userRepository: UserRepository,
) : Configurable<Route> {
    override fun Route.configure() {
        route("/api/v1/assignments") {
            configureRepository(assignmentRepository, AssignmentValidation)
        }
        route("/api/v1/graders") {
            configureRepository(graderRepository, GraderValidation)
        }
        route("/api/v1/grader-uploads") {
            configureRepository(graderUploadRepository, GraderUploadValidation) {
                it.toDto()
            }
        }
        route("/api/v1/grading-batches") {
            configureRepository(gradingBatchRepository, GradingBatchValidation)
        }
        route("/api/v1/grading-batch-templates") {
            configureRepository(gradingBatchTemplateRepository, GradingBatchTemplateValidation)
        }
        route("/api/v1/solution-uploads") {
            configureRepository(solutionUploadRepository, SolutionUploadValidation) {
                it.toArchive()
            }
        }
        route("/api/v1/submissions") {
            configureRepository(submissionRepository, SubmissionValidation) {
                it.toDto(submissionUploadRepository, userRepository)
            }
        }
        route("/api/v1/submission-uploads") {
            configureRepository(submissionUploadRepository, SubmissionUploadVerification) {
                it.toArchive()
            }
        }
    }
}

inline fun <reified T : ObjectWithId<*>> Route.configureRepository(
    repository: Repository<*, T>,
    modelValidation: ModelValidation<T>,
    noinline mapper: (T) -> Any = {},
) {
    get {
        val errors = StringBuilder(validationErrorMessage)
        val ascending = validateAscending(call.request.header("ascending"), errors)
        val field = modelValidation.validateSortByField(call.request.header("field"), errors)
        val limit = validateLimit(call.request.header("limit"), errors)
        val preview = validatePreview(call.request.header("preview"), errors)
        val search = call.request.header("search")
        if (ascending.second
            || field.second
            || limit.second
            || preview.second
        ) {
            return@get call.respondText(errors.toString(), status = HttpStatusCode.BadRequest)
        }
        getAndRun({
            repository.getAll(
                ascending.first,
                field.first,
                limit.first,
                preview.first,
                search,
            ).map(mapper).toList()
        }) {
            if (it != null) {
                it.printStackTrace()
                call.respondText("Failed to get documents: ${it.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    get("{id}") {
        val errors = StringBuilder(validationErrorMessage)
        val id = requireId(call.parameters["id"])
            ?: return@get call.respondText(errors.toString(), status = HttpStatusCode.BadRequest)
        val parsedId = try {
            with(repository) { id.asTKey }
        } catch (e: IllegalArgumentException) {
            return@get call.respondText("Invalid id: ${e.message}", status = HttpStatusCode.BadRequest)
        }
        getAndRun({ repository.getOne(parsedId)?.let(mapper) }) {
            if (it == null) {
                call.respondText("No document with id $id", status = HttpStatusCode.NotFound)
            } else {
                it.printStackTrace()
                call.respondText("Failed to get document with id $id: ${it.message}", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    post {
        receiveAndRun(repository::insertOne, modelValidation::validateCreate) {
            call.respondText(
                "Failed to insert document: ${throwable?.message}",
                status = HttpStatusCode.InternalServerError,
            )
        }
    }
    put {
        receiveAndRun(repository::updateOne, modelValidation::validateUpdate) {
            throwable?.printStackTrace()
            when {
                original.getId() == null -> call.respondText(
                    "Id is required to update document",
                    status = HttpStatusCode.BadRequest,
                )
                throwable is UpdateException -> call.respondText(
                    "No document with id ${original.getId()}: ${throwable.message}",
                    status = HttpStatusCode.BadRequest,
                )
                else -> call.respondText(
                    "Failed to update document: ${throwable?.message}",
                    status = HttpStatusCode.InternalServerError,
                )
            }
        }
    }
}

suspend inline fun <reified T> PipelineContext<Unit, ApplicationCall>.getAndRun(
    dbFun: () -> T,
    ifFailed: (Throwable?) -> Unit,
) {
    val result: Pair<T?, Throwable?> = try {
        dbFun() to null
    } catch (e: Throwable) {
        null to e
    }
    if (result.first == null) {
        ifFailed(result.second)
    } else {
        call.respond(result.first!!)
    }
}

suspend inline fun <reified T : ObjectWithId<*>> PipelineContext<Unit, ApplicationCall>.receiveAndRun(
    dbFun: (T) -> T?,
    validate: (T, Appendable) -> Boolean = { _, _ -> true },
    ifFailed: (FailedResult<T>).() -> Unit,
) {
    val received = try {
        call.receive<T>()
    } catch (e: Throwable) {
        return call.respondText(e.message ?: "An error occurred", status = HttpStatusCode.BadRequest)
    }
    val errors = StringBuilder()
    if (!validate(received, errors)) {
        return call.respondText(errors.toString(), status = HttpStatusCode.BadRequest)
    }
    val result: Pair<T?, Throwable?> = try {
        dbFun(received) to null
    } catch (e: Throwable) {
        null to e
    }
    if (result.first == null) {
        ifFailed(FailedResult(received, result.second))
    } else {
        call.respond(result.first!!)
    }
}

data class FailedResult<T>(
    val original: T,
    val throwable: Throwable?,
)
