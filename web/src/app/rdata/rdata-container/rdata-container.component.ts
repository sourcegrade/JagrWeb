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

import {
    AfterViewInit,
    Component,
    ComponentFactoryResolver,
    Directive,
    Input,
    OnInit,
    QueryList,
    Type,
    ViewChildren,
    ViewContainerRef,
} from "@angular/core"
import { BaseRepositoryService, Named, ObjectWithId, Statused } from "../../service/BaseRepositoryService"

import { MatTableDataSource } from "@angular/material/table"
import { RDataViewSelectComponent, ViewType } from "../rdata-viewselect/rdata-view-select.component"

@Component({
    selector: "rdata-container",
    templateUrl: "./rdata-container.component.html",
    styleUrls: ["./rdata-container.component.scss"],
})
export class RDataContainerComponent<T extends ObjectWithId & Named & Statused & ExtraColumns> implements OnInit {

    @Input() repository!: BaseRepositoryService<T>
    @Input() columnNameMap: ColumnNameMap<T> = {}
    @Input() domConverter?: RDataDOMConverter<T>
    @Input() viewSelect?: RDataViewSelectComponent

    dataSource: MatTableDataSource<T> = new MatTableDataSource<T>()

    viewType: ViewType = "table"

    constructor() {
    }

    async ngOnInit() {
        this.viewSelect?.viewSelected.subscribe(result => {
            this.viewType = result
        })
        let data = await this.repository?.getAll()
        if (!data) {
            return
        }
        this.dataSource.data = data
    }
}

@Component({
    template: "{{ element[key] }}",
})
export class RDataDefaultCellComponent implements CustomRDataDOMComponent<any> {
    element!: any
    key!: keyof any
}

@Directive({
    selector: "[elementData]",
})
export class RDataElementDirective<T> {
    @Input("elementData") element!: T
    @Input("elementKey") key!: keyof (T & ExtraColumns)

    constructor(public viewContainerRef: ViewContainerRef) {
    }
}

/**
 * Maps keys of T to column names
 */
export type ColumnNameMap<T extends ObjectWithId & Named & Statused> = {
    [P in keyof T]?: string
}

export interface CustomRDataDOMComponent<T extends ObjectWithId & Named & Statused> {
    set element(item: T)

    set key(key: keyof T)
}

/**
 * Functional interface converting a given property of T to a readable string
 */
export type RDataDOMConverter<T extends ObjectWithId & Named & Statused> = {
    [P in keyof (T & ExtraColumns)]?: Type<CustomRDataDOMComponent<T>>
}

export interface ExtraColumns {
    action?: never
}

@Directive()
export abstract class RDataAbstractView<T extends ObjectWithId & Named & Statused> implements AfterViewInit {
    @Input() columnNameMap!: ColumnNameMap<T & ExtraColumns>
    @Input() domConverter?: RDataDOMConverter<T & ExtraColumns>
    @Input() dataSource!: MatTableDataSource<T>
    @ViewChildren("nameCell", { read: RDataElementDirective }) nameCells!: QueryList<RDataElementDirective<T>>
    @ViewChildren("customCell", { read: RDataElementDirective }) customCells!: QueryList<RDataElementDirective<T>>
    @ViewChildren("statusCell", { read: RDataElementDirective }) statusCells!: QueryList<RDataElementDirective<T>>
    @ViewChildren("actionCell", { read: RDataElementDirective }) actionCells?: QueryList<RDataElementDirective<T>>

    constructor(
        private componentFactoryResolver: ComponentFactoryResolver,
    ) {
    }

    ngAfterViewInit() {
        // the view select has to be double clicked. this is a feature, not a bug
        [
            this.nameCells,
            this.customCells,
            this.statusCells,
            this.actionCells,
        ].forEach(view => {
            if (view) {
                this.initCells(view.toArray())
            }
        })
    }

    initCells(containers: RDataElementDirective<T>[]) {
        for (let container of containers) {
            let componentType: Type<CustomRDataDOMComponent<T>> | undefined
            if (this.domConverter && (container.key as any) in this.domConverter) {
                componentType = this.domConverter[container.key]
            }
            const factory = this.componentFactoryResolver.resolveComponentFactory(componentType ?? RDataDefaultCellComponent)
            const component = container.viewContainerRef.createComponent(factory)
            component.instance.element = container.element
            component.instance.key = container.key
        }
    }
}
