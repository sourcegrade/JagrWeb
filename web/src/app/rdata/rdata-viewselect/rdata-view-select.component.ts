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

import { Component, EventEmitter, OnInit, Output } from "@angular/core"

import { MatButtonToggleChange } from "@angular/material/button-toggle"

@Component({
    selector: "rdata-view-select",
    templateUrl: "./rdata-view-select.component.html",
    styleUrls: ["./rdata-view-select.component.scss"],
})
export class RDataViewSelectComponent implements OnInit {
    @Output() viewSelected = new EventEmitter<ViewType>()

    constructor() {
    }

    ngOnInit() {
    }

    onChange(value: MatButtonToggleChange) {
        this.viewSelected.emit(value as unknown as ViewType)
    }
}

export type ViewType = "grid" | "table"
