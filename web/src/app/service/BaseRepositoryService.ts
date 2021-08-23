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

import { GradingJobReference } from "./grading-batch.service"
import { HttpClient, HttpResponse } from "@angular/common/http"

import { Observable, firstValueFrom } from "rxjs"
import { Router } from "@angular/router"
import { User } from "./user.service"

export abstract class BaseRepositoryService<T> {

    protected constructor(
        protected http: HttpClient,
        protected router: Router,
        protected endpointRepository: string,
    ) {
    }

    public async handle<E>(observable: Observable<HttpResponse<E>>): Promise<E | null> {
        return firstValueFrom(observable).then(result => result.body, async result => {
            if (result.status === 401) {
                await this.router.navigateByUrl(result.error)
            }
            return null
        })
    }

    public async getAll(
        ascending: boolean | null = null,
        field: string | null = null,
        limit: number | null = null,
        preview: boolean | null = null,
        search: string | null = null,
    ): Promise<T[] | null> {
        const headerMap: { [key: string]: string } = {
            ...(ascending && { ascending: ascending.toString() }),
            ...(field && { field }),
            ...(limit && { limit: limit.toString() }),
            ...(preview && { preview: preview.toString() }),
            ...(search && { search }),
        }
        return this.handle(this.http.get<T[]>(
            this.endpointRepository,
            {
                observe: "response",
                headers: headerMap,
            }))
    }

    public async get(id: string): Promise<T | null> {
        return this.handle(this.http.get<T>(this.endpointRepository + "/" + id, { observe: "response" }))
    }

    public async create(document: T): Promise<T | null> {
        return this.handle(this.http.post<T>(
            this.endpointRepository,
            document,
            {
                observe: "response",
                headers: {
                    "Content-Type": "application/json",
                },
                withCredentials: true,
            }))
    }

    public async update(document: T): Promise<T | null> {
        return this.handle(this.http.put<T>(
            this.endpointRepository,
            document,
            {
                observe: "response",
                headers: {
                    "Content-Type": "application/json",
                },
                withCredentials: true,
            }))
    }
}

export interface ObjectWithId {
    id: string
}

export interface Named {
    name: string
}

export interface Versioned {
    version: Version
}

export interface Statused {
    status: "success" | "fail" | "progress" | "unknown"
}

export interface Jobbed {
    jobs: GradingJobReference[]
}

export interface WithJobbedUploads extends Statused {
    uploads: Jobbed[]
}

export interface Owner {
    owner: User
}

export interface Version {
    major: number
    minor: number
    patch: number
}
