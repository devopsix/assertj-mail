# Development Guidelines

## Requirements

* JDK 11 or newer (e.g. [OpenJDK](https://openjdk.org/))
* [Apache Maven](https://maven.apache.org/) 3.3.9 or newer

## Code Style

This project adheres to [Google Java Style](https://google.github.io/styleguide/javaguide.html) and uses
[Checkstyle](https://checkstyle.org/) for validating code style.

Run `mvn --activate-profiles=checkstyle verify` to execute Checkstyle.

Use [google_checks.xml](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml)
for configuring your IDE's inspection and formatting settings.

## Git Conventions

1. This project adheres to [Conventional Commits] conventions.
2. A linear commit history shall be maintained on the main branch.
   I.e. squash-merges should be used when merging pull requests.

## Static Code Analysis

This project uses [SpotBugs](https://spotbugs.github.io/) for static code analysis.

Run `mvn --activate-profiles=spotbugs verify` to execute SpotBugs.

## Testing

Run `mvn --activate-profiles=coverage verify` to execute all tests and to assert code coverage requirements are met.

## Building

Run `mvn --activate-profiles=checkstyle,spotbugs,coverage verify` to build all modules, execute all tests
and run all checks.

Run `mvn --activate-profiles=-examples install` to install the main artifacts into the local repository.

## Code Generation

The code for the assertj-mail-jakarta and assert-mail-examples-jakarta modules is “generated” from their non-Jakarta
counterparts by copying source files and substituting “jakarta.mail” for “javax.mail”.

## Checking for Dependency and Plug-in Updates

_Updates are being checked for automatically by [GitHub Dependabot](.github/dependabot.yml)._

Run `mvn versions:display-dependency-updates`
to check for dependency updates.

Run `mvn --activate-profiles=checkstyle,spotbugs,coverage,release -DprocessUnboundPlugins=true versions:display-plugin-updates`
to check for Maven plug-in updates.

## Release Process

This project uses [Release Please] for release management.

Release Please handles creation of release tags, GitHub releases and patching version numbers in `pom.xml` files.
A new release is cut by merging the pull request maintained by Release Please.
Building and deploying the actual Maven Artifacts for a release is the responsibility of the
[release.yaml workflow](./.github/workflows/release.yaml).

After a release has been made, Release Please will automatically create another pull request to update the version
numbers in `pom.xml` files to the next snapshot version
(see the [Maven strategy](https://github.com/googleapis/release-please/blob/main/docs/java.md)).
This snapshot “release pull request” has to be merged immediately.


[Conventional Commits]: https://www.conventionalcommits.org/
[Release Please]: https://github.com/googleapis/release-please
