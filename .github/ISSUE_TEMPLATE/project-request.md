---
name: Project Request
about: Request verification of project functionality.
title: Project Request v#
labels: ''
assignees: josecorella

---

Fill in the information below. 

  - **Name:** Stewart Powell
  - **Last Request:** N/A
  - **Last Review:** N/A

The **Last Request** [should link to the issue](https://help.github.com/en/github/writing-on-github/autolinked-references-and-urls) you last created for project verification, or use `N/A` if this is your first project request. The **Last Review** [should link to the pull request](https://help.github.com/en/github/writing-on-github/autolinked-references-and-urls) from your last code review, or use `N/A` if you have not yet had a code review.

#### Project Command

```
/home/public/cs212/project --user GITHUB_USER --org GITHUB_ORG PROJECT_VERSION
```

#### Request Type

Indicate your request type from the choices below. Choose **exactly one** option:

  - [ ] I need my project functionality grade updated on Canvas. (Use the `verify` label for this issue.)
  - [ ] I need my project functionality verified, but do not need a code review appointment or my grade updated. (Use the `quick-verify` label for this issue.)

  - [ ] I need to schedule a code review appointment for this project version. (Use the `review` label for this issue.)
  - [ ] I need a quick review of this project version. (Use the `quick-review` label for this issue. This option requires pre-approval!)

Each issue should have exactly 1 label (for the request type) and exactly 1 milestone (for the project number).

#### Verification Checklist

I verify that the following is true:

  - [ ] I created a properly named project release (e.g. `v1.0.0`).
  - [ ] I tested this release by running the `project` script on the CS lab computers.
  - [ ] I verified the issue title is correct and includes the release number (e.g `Project Request v1.0.0`)
  - [ ] I verified the `FULL_NAME`, `ISSUE_NUMBER`, `PULL_REQUEST`, `GITHUB_USER`, `GITHUB_ORG`, and `PROJECT_VERSION` fields have been updated with the correct information.
  - [ ] I verified the issue is properly assigned (e.g. `josecorella`).
  - [ ] I verified this issue has the `verify`, `quick-verify`, `review`, or `quick-review` label.
  - [ ] I verified this issue belongs to the correct milestone (e.g. `project1`).

:warning: Failure to complete all of the steps above will result in this issue being closed without verification!

