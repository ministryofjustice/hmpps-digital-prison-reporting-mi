# hmpps-digital-prison-reporting-mi
[![repo standards badge](https://img.shields.io/badge/dynamic/json?color=blue&style=flat&logo=github&label=MoJ%20Compliant&query=%24.result&url=https%3A%2F%2Foperations-engineering-reports.cloud-platform.service.justice.gov.uk%2Fapi%2Fv1%2Fcompliant_public_repositories%2Fhmpps-digital-prison-reporting-mi)](https://operations-engineering-reports.cloud-platform.service.justice.gov.uk/public-github-repositories.html#hmpps-digital-prison-reporting-mi "Link to report")
[![CircleCI](https://circleci.com/gh/ministryofjustice/hmpps-digital-prison-reporting-mi/tree/main.svg?style=svg)](https://circleci.com/gh/ministryofjustice/hmpps-digital-prison-reporting-mi)
[![Docker Repository on Quay](https://quay.io/repository/hmpps/hmpps-digital-prison-reporting-mi/status "Docker Repository on Quay")](https://quay.io/repository/hmpps/hmpps-digital-prison-reporting-mi)
[![API docs](https://img.shields.io/badge/API_docs_-view-85EA2D.svg?logo=swagger)](https://hmpps-digital-prison-reporting-mi-dev.hmpps.service.justice.gov.uk/webjars/swagger-ui/index.html?configUrl=/v3/api-docs)

This project is generated from ministryofjustice/hmpps-template-kotlin

Requires Java 17 or above

#### CODEOWNER

- Team : [hmpps-digital-prison-reporting](https://github.com/orgs/ministryofjustice/teams/hmpps-digital-prison-reporting)
- Email : digitalprisonreporting@digital.justice.gov.uk

## Overview

Provides a front end for Management Information Visualisation and Presentation

## Local Development

This project uses gradle which is bundled with the repository and also makes use
of

- [micronaut](https://micronaut.io/) - for compile time dependency injection
- [lombok](https://projectlombok.org/) - to reduce boilerplate when creating data classes
- [jacoco](https://docs.gradle.org/current/userguide/jacoco_plugin.html) - for test coverage reports



## Testing

> **Note** - test coverage reports are enabled by default and after running the
> tests the report will be written to build/reports/jacoco/test/html

### Unit Tests

The unit tests use JUnit5 and Mockito where appropriate. Use the following to
run the tests.

```
    ./gradlew clean test
```

### Integration Tests

```
    TBD
```

### Acceptance Tests

```
    TBD
```

## Contributing

Please adhere to the following guidelines when making contributions to the
project.

### Documentation

- Keep all code commentary and documentation up to date

### Branch Naming

- Use a JIRA ticket number where available
- Otherwise a short descriptive name is acceptable

### Commit Messages

- Prefix any commit messages with the JIRA ticket number where available
- Otherwise use the prefix `NOJIRA`

### Pull Requests

- Reference or link any relevant JIRA tickets in the pull request notes
- At least one approval is required before a PR can be merged

## TODO

- Modify the Dependabot file to suit the [dependency manager](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuration-options-for-the-dependabot.yml-file#package-ecosystem) you plan to use and for [automated pull requests for package updates](https://docs.github.com/en/code-security/supply-chain-security/keeping-your-dependencies-updated-automatically/enabling-and-disabling-dependabot-version-updates#enabling-dependabot-version-updates). Dependabot is enabled in the settings by default.
- Ensure as many of the [GitHub Standards](https://github.com/ministryofjustice/github-repository-standards) rules are maintained as possibly can.
