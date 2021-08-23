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
import { FormsModule } from "@angular/forms"
import { MatButtonModule } from "@angular/material/button"
import { MatButtonToggleModule } from "@angular/material/button-toggle"
import { MatCardModule } from "@angular/material/card"
import { MatIconModule } from "@angular/material/icon"
import { MatListModule } from "@angular/material/list"
import { MatTableModule } from "@angular/material/table"
import { NgModule } from "@angular/core"

import {
    RDataContainerComponent,
    RDataDefaultCellComponent,
    RDataElementDirective,
} from "./rdata-container/rdata-container.component"

import { RDataGridComponent } from "./rdata-grid/rdata-grid.component"
import { RDataTableComponent } from "./rdata-table/rdata-table.component"
import { RDataViewSelectComponent } from "./rdata-viewselect/rdata-view-select.component"

@NgModule({
    declarations: [
        RDataContainerComponent,
        RDataDefaultCellComponent,
        RDataElementDirective,
        RDataGridComponent,
        RDataTableComponent,
        RDataViewSelectComponent,
    ],
    exports: [
        RDataContainerComponent,
        RDataViewSelectComponent,
    ],
    imports: [
        CommonModule,
        FormsModule,
        MatButtonModule,
        MatButtonToggleModule,
        MatCardModule,
        MatIconModule,
        MatListModule,
        MatTableModule,
    ],
})
export class RDataModule {
}
