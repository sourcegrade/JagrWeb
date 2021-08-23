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

import { BaseRepositoryService, Named, ObjectWithId } from "./BaseRepositoryService"

import { GraderSelector } from "./grader.service"
import { HttpClient } from "@angular/common/http"
import { Injectable } from "@angular/core"
import { Owned } from "./user.service"
import { Router } from "@angular/router"
import { SubmissionSelector } from "./submission.service"

@Injectable({
    providedIn: "root",
})
export class GradingBatchTemplateService extends BaseRepositoryService<GradingBatchTemplate> {
    constructor(http: HttpClient, router: Router) {
        super(http, router, `${window.location.origin}/api/v1/grading-batch-templates`)
    }
}

export interface GradingBatchTemplate extends Named, ObjectWithId, Owned {
    graders: GraderSelector[]
    submissions: SubmissionSelector[]
}
