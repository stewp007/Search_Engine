---
name: Project Request
about: Request verification of project functionality.
title: Project Request v#
labels: ''
assignees: josecorella

---

Fill in the information below. 

  - **Name:** FULL_NAME
  - **Last Request:** ISSUE_NUMBER
  - **Last Review:** PULL_REQUEST

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

----

#### First-Time Setup

After creating your private project repository, you need to perform this one-time setup:

  1. Go to `.github/ISSUE_TEMPLATE` in your repository.

  2. Modify all of the templates to replace `FULL_NAME` with your full name.

  3. Modify all of the templates to replace `GITHUB_USER` with your **Github** username (not your USF or CS usernames).

  4. Delete the first-time setup text from all of the issue templates (including the hints below).

Here are some additional hints for using issues and pull requests:

  - You can [check the checkboxes](https://help.github.com/articles/about-task-lists/) and [apply labels](https://help.github.com/en/articles/applying-labels-to-issues-and-pull-requests) AFTER saving the issue.

  - Do not forget to change the issue title *and* command to include the project release number. If there are any mistakes, you will be required to fix those mistakes before the issue will be closed.

  - If this is your first release, use `N/A` to indicate there is not a previous release.

  - When in doubt, see the [template issues](https://github.com/usf-cs212-fall2019/template-project/issues?utf8=%E2%9C%93&q=is%3Aissue) for an example of how everything should look.

:warning: **This text should not appear on your issue. If so, you need to still modify the issue template.**
