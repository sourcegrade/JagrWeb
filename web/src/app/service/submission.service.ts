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

import { BaseRepositoryService, Named, ObjectWithId, Versioned, WithJobbedUploads } from "./BaseRepositoryService"

import { HttpClient } from "@angular/common/http"
import { Injectable } from "@angular/core"
import { Owned } from "./user.service"
import { Router } from "@angular/router"
import { SubmissionUpload } from "./submission-upload.service"

@Injectable({
    providedIn: "root",
})
export class SubmissionService extends BaseRepositoryService<Submission> {
    constructor(http: HttpClient, router: Router) {
        super(http, router, `${window.location.origin}/api/v1/submissions`)
    }
}

export interface Submission extends Named, ObjectWithId, Owned, WithJobbedUploads {
    info: SubmissionInfo
    uploads: SubmissionUpload[]
}

export interface SubmissionInfo {
    assignmentId: string
    studentId: string
    firstName: string
    lastName: string
}

export interface SubmissionSelector extends ObjectWithId, Versioned {
}
