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

import { ColumnNameMap, CustomRDataDOMComponent, RDataDOMConverter } from "../rdata/rdata-container/rdata-container.component"
import { Component, Input } from "@angular/core"
import { Submission, SubmissionService } from "../service/submission.service"

@Component({
    selector: "app-submissions",
    templateUrl: "./submissions.component.html",
    styleUrls: ["./submissions.component.scss"],
})
export class SubmissionsComponent {

    columnNameMap: ColumnNameMap<Submission> = {
        info: "Assignment",
    }

    domConverter: RDataDOMConverter<Submission> = {
        action: SubmissionActionComponent,
        info: SubmissionInfoComponent,
        status: SubmissionStatusComponent,
    }

    constructor(
        public submissionService: SubmissionService,
    ) {
    }
}

@Component({
    template: `
        <button mat-button [routerLink]="element.id">Detail</button>
    `,
})
export class SubmissionActionComponent implements CustomRDataDOMComponent<Submission> {
    element!: Submission
    key!: keyof Submission
}

@Component({
    template: `
        {{ element.info?.assignmentId }}
    `,
})
export class SubmissionInfoComponent implements CustomRDataDOMComponent<Submission> {
    element!: Submission
    key!: keyof Submission
}

@Component({
    selector: "submission-status",
    template: `
        <mat-icon
            *ngIf="element.status === 'success'"
            style="color: #4CA64C; cursor: pointer;"
            matTooltip="Success"
            matTooltipPosition="right"
        >check_circle
        </mat-icon>
        <mat-icon
            *ngIf="element.status === 'fail'"
            style="color: #CC3C3C; cursor: pointer;"
            matTooltip="Fail"
            matTooltipPosition="right"
        >cancel
        </mat-icon>
        <mat-icon
            *ngIf="element.status === 'progress'"
            style="color: #e77b4b; cursor: pointer;"
            matTooltip="In progress"
            matTooltipPosition="right"
        >watch_later
        </mat-icon>
        <mat-icon
            *ngIf="element.status === 'unknown'"
            style="color: #555; cursor: pointer;"
            matTooltip="Unknown"
            matTooltipPosition="right"
        >cancel
        </mat-icon>
    `,
})
export class SubmissionStatusComponent implements CustomRDataDOMComponent<Submission> {
    @Input() element!: Submission
    key!: keyof Submission
}
