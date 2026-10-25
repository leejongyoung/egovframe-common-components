# egovframe-common-components: project context

## Role, scope, and public entry points

`README.md` describes reusable eGovFrame MVC application components. `src/main/java/egovframework/com/`
contains shared infrastructure, collaboration, security, authentication, system management,
user support, integration, statistics, and utilities. `src/main/webapp/` exposes UI resources;
`src/main/resources/` holds mappings and configuration. `script/` and `src/script/` provide
schema/data scripts for eight supported database families. Preserve existing routes, forms,
Java service contracts, SQL mappings, database semantics, and configurable authorization behavior.

## Build and verification evidence

- `pom.xml` packages a WAR, inheriting `org.egovframe.web:egovframe-web-config-parent:5.0.1`.
  Dependencies use Maven Central, the eGovFrame Maven repository, and a project-local library path.
- `.github/workflows/maven.yml` targets `main`, uses JDK 17, and runs
  `mvn -B verify --file pom.xml -DskipTests`.
- `.github/workflows/fork-ci.yml` targets `work`, uses JDK 17, and runs
  `mvn -B -ntp verify --file pom.xml -DskipTests`. This packages/verifies while skipping tests;
  it establishes no database, browser, login, or authorization behavior coverage.
- `README.md` documents IDE import, Maven project update, local database/authentication configuration
  in `src/main/resources/egovframework/egovProps/globals.properties`, and Run on Server.
  Database setup, servlet container, and suitable local configuration are required for runtime checks.
  Read affected tests and POM profiles before selecting regression commands.
- README security guidance requires replacing public sample cryptographic values and accounts
  before deployment. Never copy real deployment values into maintenance artifacts.

## Contribution and submission baseline

`AGENTS.md` defines `main` as the upstream mirror and `work` as fork integration. The actual
parent is `eGovFramework/egovframe-common-components`; fetch its current default branch and
record the exact SHA. Preserve existing submission branches and open PRs.
The existing `scripts/open_upstream_pr.sh` builds disposable submission heads from `work`;
its denylist now includes `.specify` and `.omp` as required by fork instructions. This excludes
files but does not remove tooling commits from history: do not invoke it for the new workflow.
Instead create a separate upstream-based submission worktree and stage only application changes,
as required by [the constitution](constitution.md). Adoption uses the explicitly authorized
work-only push path and creates no PR. Existing fork review and CI policy remains in force for
subsequent application contributions.

## Exploration limits

No `.codegraph/` index was present; evidence comes from targeted README, AGENTS, POM, CI, and
submission-helper reads. No index installation or application execution was performed.


## Maintenance process

Follow [constitution.md](constitution.md) for the domain-card, assessment, planning, verification,
and submission gates. Commands are installed under `.omp/commands/`; read them as agent
instructions. No application build or test was run for tool adoption. Scaffold checks establish
tool installation only, not application correctness.
