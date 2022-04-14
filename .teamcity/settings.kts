import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.2"

project {
    description = "Contains all other projects"

    features {
        buildReportTab {
            id = "PROJECT_EXT_1"
            title = "Code Coverage"
            startPage = "coverage.zip!index.html"
        }
    }

    cleanup {
        baseRule {
            preventDependencyCleanup = false
        }
    }

    subProject(Netology)
}


object Netology : Project({
    name = "netology"

    vcsRoot(Netology_GitGithubComDotsenkoisExampleTeamcityGitRefsHeadsMaster)

    buildType(Netology_Build)

    params {
        text("name", "Ilya", readOnly = true, allowEmpty = true)
    }
})

object Netology_Build : BuildType({
    name = "Build"

    artifactRules = "+:target/*.jar"

    vcs {
        root(Netology_GitGithubComDotsenkoisExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {
            name = "mvn clean test"

            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "mvn clean deploy"

            conditions {
                equals("teamcity.build.branch", "master")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "maven_settings"
        }
    }

    triggers {
        vcs {
            branchFilter = "+:master"
        }
    }
})

object Netology_GitGithubComDotsenkoisExampleTeamcityGitRefsHeadsMaster : GitVcsRoot({
    name = "git@github.com:dotsenkois/example-teamcity.git#refs/heads/master"
    url = "git@github.com:dotsenkois/example-teamcity.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = uploadedKey {
        userName = "git"
        uploadedKey = "git"
    }
    param("secure:password", "")
})
