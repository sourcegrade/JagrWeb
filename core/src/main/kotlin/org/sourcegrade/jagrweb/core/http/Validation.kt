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

import org.sourcegrade.jagrweb.core.model.Assignment
import org.sourcegrade.jagrweb.core.model.Grader
import org.sourcegrade.jagrweb.core.model.GraderUpload
import org.sourcegrade.jagrweb.core.model.GradingBatch
import org.sourcegrade.jagrweb.core.model.GradingBatchTemplate
import org.sourcegrade.jagrweb.core.model.ObjectWithId
import org.sourcegrade.jagrweb.core.model.SolutionUpload
import org.sourcegrade.jagrweb.core.model.Submission
import org.sourcegrade.jagrweb.core.model.SubmissionUpload
import kotlin.reflect.KProperty1

val fail = Pair(null, true)
val empty = Pair(null, false)
fun <T> T.success() = Pair(this, false)
const val validationErrorMessage = "An error occurred processing your request!"

fun requireId(id: String?, writer: Appendable? = null): String? {
    if (id == null) {
        writer?.appendLine("ID is required")
        return null
    }
    return id
}

fun requireId(obj: ObjectWithId<*>, writer: Appendable? = null): Boolean {
    if (obj.getId() == null) {
        writer?.appendLine("ID is required")
        return false
    }
    return true
}

fun validateAscending(value: String?, writer: Appendable? = null): Pair<Boolean?, Boolean> {
    return validateBoolean("ascending", value, writer)
}

fun validateBoolean(property: String, value: String?, writer: Appendable? = null): Pair<Boolean?, Boolean> {
    return when (value) {
        null -> empty
        "true" -> true.success()
        "false" -> false.success()
        else -> {
            writer?.appendLine("Could not parse $property boolean $value")
            fail
        }
    }
}

fun validateInteger(property: String, value: String?, writer: Appendable? = null): Pair<Int?, Boolean> {
    return try {
        value?.toInt().success()
    } catch (e: NumberFormatException) {
        writer?.appendLine("Could not parse $property integer $value")
        fail
    }
}

fun validateString(property: String?, value: String?, writer: Appendable? = null): Boolean {
    if (property != null && value != null) {
        return false
    }
    if (property == null) {
        writer?.appendLine("Placeholder $property is null")
        return true
    }
    if (value == null) {
        writer?.appendLine("Value $value of placeholder $property is null")
    }
    return true
}

fun validateLimit(value: String?, writer: Appendable? = null): Pair<Int?, Boolean> {
    return validateInteger("limit", value, writer)
}

fun validatePreview(value: String?, writer: Appendable? = null): Pair<Boolean?, Boolean> {
    return validateBoolean("preview", value, writer)
}

sealed interface ModelValidation<T : ObjectWithId<*>> {
    fun validateSortByField(value: String?, writer: Appendable? = null): Pair<String?, Boolean>
    fun validateCreate(obj: T, writer: Appendable? = null): Boolean = true
    fun validateUpdate(obj: T, writer: Appendable? = null): Boolean = requireId(obj, writer)
}

fun <T : ObjectWithId<*>> createModelDelegate(
    plural: String,
    validSortFields: List<KProperty1<T, *>> = listOf(),
): ModelValidation<T> {
    return OnlySortModelValidation(plural, validSortFields)
}

private open class OnlySortModelValidation<T : ObjectWithId<*>>(
    val plural: String,
    validSortFields: List<KProperty1<T, *>>,
) : ModelValidation<T> {
    val validSortFieldNames = validSortFields.map { it.name }
    override fun validateSortByField(value: String?, writer: Appendable?): Pair<String?, Boolean> {
        if (value == null) {
            return empty
        }
        if (validSortFieldNames.contains(value)) {
            return value.success()
        }
        writer?.appendLine("Could not sort $plural by field $value")
        return fail
    }
}

object AssignmentValidation : ModelValidation<Assignment> by createModelDelegate(
    plural = "assignments",
    validSortFields = listOf(
        Assignment::name,
    ),
)

object GraderValidation : ModelValidation<Grader> by createModelDelegate(
    plural = "graders",
    validSortFields = listOf(
        Grader::name,
    ),
)

object GraderUploadValidation : ModelValidation<GraderUpload> by createModelDelegate(
    plural = "grader uploads",
    validSortFields = listOf(
        GraderUpload::version,
    ),
) {
    override fun validateCreate(obj: GraderUpload, writer: Appendable?): Boolean {
        if (obj.graderId == null) {
            writer?.appendLine("graderId is required")
            return false
        }
        return true
    }
}

object GradingBatchValidation : ModelValidation<GradingBatch> by createModelDelegate(
    plural = "grading batches",
    validSortFields = listOf(
        GradingBatch::scheduledUtc,
        GradingBatch::startedUtc,
        GradingBatch::finishedUtc,
    ),
)

object GradingBatchTemplateValidation : ModelValidation<GradingBatchTemplate> by createModelDelegate(
    plural = "grading batch templates",
    validSortFields = listOf(
        GradingBatchTemplate::name,
    ),
)

object SolutionUploadValidation : ModelValidation<SolutionUpload> by createModelDelegate(
    plural = "solution uploads",
    validSortFields = listOf(
        SolutionUpload::version,
    ),
) {
    override fun validateCreate(obj: SolutionUpload, writer: Appendable?): Boolean {
        if (obj.graderId == null) {
            writer?.appendLine("graderId is required")
            return false
        }
        return true
    }
}

object SubmissionValidation : ModelValidation<Submission> by createModelDelegate(
    plural = "submissions",
    validSortFields = listOf(
        Submission::name,
        Submission::info,
    ),
) {
    override fun validateCreate(obj: Submission, writer: Appendable?): Boolean {
        var failed = false
        if (obj.name == null) {
            writer?.appendLine("name is required")
            failed = true
        }
        val info = obj.info
        if (info == null) {
            writer?.appendLine("info is required")
            failed = true
        } else {
            if (info.assignmentId == null) {
                writer?.appendLine("info.assignmentId is required")
                failed = true
            }
            if (info.studentId == null) {
                writer?.appendLine("info.studentId is required")
                failed = true
            }
        }

        return !failed
    }
}

object SubmissionUploadVerification : ModelValidation<SubmissionUpload> by createModelDelegate(
    plural = "submission upload",
    validSortFields = listOf(
        SubmissionUpload::version,
    ),
) {
    override fun validateCreate(obj: SubmissionUpload, writer: Appendable?): Boolean {
        if (obj.submissionId == null) {
            writer?.appendLine("submissionId is required")
            return false
        }
        return true
    }
}
