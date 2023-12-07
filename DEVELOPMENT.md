# Requirements

* JDK 11 or newer (e.g. [OpenJDK](https://openjdk.org/))
* [Apache Maven](https://maven.apache.org/) 3.3.9 or newer

# Code Style

This project adheres to [Google Java Style](https://google.github.io/styleguide/javaguide.html) and uses
[Checkstyle](https://checkstyle.org/) for validating code style.

Run `mvn --activate-profiles=checkstyle verify` to execute Checkstyle.

Use [google_checks.xml](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml)
for configuring your IDE's inspection and formatting settings.

# Static Code Analysis

This project uses [SpotBugs](https://spotbugs.github.io/) for static code analysis.

Run `mvn --activate-profiles=spotbugs verify` to execute SpotBugs.

# Testing

Run `mvn --activate-profiles=coverage verify` to execute all tests and to assert code coverage requirements are met.

# Building

Run `mvn --activate-profiles=checkstyle,spotbugs,coverage verify` to build all modules, execute all tests
and run all checks.

Run `mvn --activate-profiles=-examples install` to install the main artifacts into the local repository.

# Code Generation

The code for the assertj-mail-jakarta and assert-mail-examples-jakarta modules is “generated” from their non-Jakarta
counterparts by copying source files and substituting “jakarta.mail” for “javax.mail”.

# Checking for Dependency Updates

Run `mvn versions:display-dependency-updates`
to check for dependency updates.

# Checking for Plug-in Updates

Run `mvn --activate-profiles=checkstyle,spotbugs,coverage,release versions:display-plugin-updates`
to check for Maven plug-in updates.
