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

import { CommonModule } from "@angular/common"
import { MatButtonModule } from "@angular/material/button"
import { MatCardModule } from "@angular/material/card"
import { MatDividerModule } from "@angular/material/divider"
import { MatFormFieldModule } from "@angular/material/form-field"
import { MatIconModule } from "@angular/material/icon"
import { MatInputModule } from "@angular/material/input"
import { MatListModule } from "@angular/material/list"
import { MatTableModule } from "@angular/material/table"
import { MatTooltipModule } from "@angular/material/tooltip"
import { NgModule } from "@angular/core"
import { RDataModule } from "../rdata/rdata.module"
import { SubmissionDetailComponent } from "./submission-detail/submission-detail.component"
import { SubmissionsRoutingModule } from "./submissions-routing.module"
import {
    SubmissionActionComponent,
    SubmissionInfoComponent,
    SubmissionStatusComponent,
    SubmissionsComponent,
} from "./submissions.component"

@NgModule({
    declarations: [
        SubmissionActionComponent,
        SubmissionDetailComponent,
        SubmissionInfoComponent,
        SubmissionsComponent,
        SubmissionStatusComponent,
    ],
    imports: [
        CommonModule,
        MatButtonModule,
        MatCardModule,
        MatDividerModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatListModule,
        MatTableModule,
        MatTooltipModule,
        RDataModule,
        SubmissionsRoutingModule,
    ],
})
export class SubmissionsModule {
}
