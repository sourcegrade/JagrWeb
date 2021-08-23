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

import { Component } from "@angular/core"
import { Router } from "@angular/router"

@Component({
    selector: "app-default-nav",
    templateUrl: "./default-nav.component.html",
    styleUrls: ["./default-nav.component.scss"],
})
export class DefaultNavComponent {

    constructor(
        private router: Router,
    ) {
    }

    isActive(url: string) {
        if (url === "/") {
            return this.router.url === "/"
        }
        return this.router.url.startsWith(url)
    }
}
