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

import { ActivatedRoute } from "@angular/router"
import { Component, OnInit } from "@angular/core"
import { Submission, SubmissionService } from "../../service/submission.service"

@Component({
    selector: "app-submission-detail",
    templateUrl: "./submission-detail.component.html",
    styleUrls: ["./submission-detail.component.scss"],
})
export class SubmissionDetailComponent implements OnInit {
    /*

      submission: Submission = {
        id: "61269f92473519d71a26a896",
        name: "Submission for übung H03",
        info: {
          assignmentId: "H03",
          studentId: "ab12cdef",
          firstName: "First Name",
          lastName: "Last Name",
        },
        uploads: [
          {
            id: "61269f92473519d71a26a896",
            version: new Version(1, 0, 0),
            submissionId: "61269f92473519d71a26a896",
            jobs: [
              {
                batchId: "61269f92473519d71a26a894",
                jobId: 1,
              },
              {
                batchId: "61269f92473519d71a26a891",
                jobId: 5,
              },
              {
                batchId: "61269f92473519d71a26a892",
                jobId: 3,
              },
              {
                batchId: "61269f92473519d71a26a898",
                jobId: 53,
              },

            ],
          },
          {
            id: "61269f92473519d71a26a897",
            version: new Version(1, 1, 0),
            submissionId: "61269f92473519d71a26a896",
            jobs: [
              {
                batchId: "61269f92473519d71a26a894",
                jobId: 1,
              },
              {
                batchId: "61269f92473519d71a26a891",
                jobId: 5,
              },
              {
                batchId: "61269f92473519d71a26a892",
                jobId: 3,
              },
              {
                batchId: "61269f92473519d71a26a898",
                jobId: 53,
              },

            ],
          },

        ],
        status: "success",
      }
    */

    submission: Submission = {
        id: "",
        owner: {
            id: "",
            studentId: "",
            firstName: "",
            lastName: "",
        },
        name: "",
        info: {
            assignmentId: "",
            studentId: "",
            firstName: "",
            lastName: "",
        },
        uploads: [],
        status: "success",
    }

    uploadColumns: string[] = [
        "version",
        "jobs",
    ]

    constructor(
        private route: ActivatedRoute,
        private submissionService: SubmissionService,
    ) {
        this.route.params.subscribe(params => {
            this.submissionService.get(params["id"]).then(result => {
                console.log(result)
                if (result) {
                    this.submission = result
                }
            })
        })
    }

    ngOnInit() {
    }
}
