import sbt.Keys.javacOptions
import sbtrelease.ReleaseStateTransformations.{
  checkSnapshotDependencies,
  commitNextVersion,
  commitReleaseVersion,
  inquireVersions,
  pushChanges,
  runClean,
  runTest,
  setNextVersion,
  setReleaseVersion,
  tagRelease
}
import sbtrelease.{versionFormatError, Version}

import java.util.regex.Pattern

val dottyVersion = "3.0.0"

lazy val root = project
  .in(file("."))
  .settings(
    organization := "org.sweet-delights",
    name := "delightful-cron",
    homepage := Option(url("https://github.com/sweet-delights/delightful-cron")),
    licenses := List("GNU Lesser General Public License Version 3" -> url("https://www.gnu.org/licenses/lgpl-3.0.txt")),
    description := "delightful-cron is a library to parse Jenkins-like cron specifications with H symbols",
    scmInfo := Option(ScmInfo(url("https://github.com/sweet-delights/delightful-cron"), "scm:git@github.com:sweet-delights/delightful-cron.git")),
    developers := List(
      Developer(
        id = "pgrandjean",
        name = "Patrick Grandjean",
        email = "pgrandjean.github.com@gmail.com",
        url = url("https://github.com/pgrandjean")
      )
    ),
    scalaVersion := "3.1.2",
    libraryDependencies ++= Seq(
      // "sweet.delights" %% "delightful-extractors" % "0.0.2-SNAPSHOT",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
      "org.scalatest"          %% "scalatest-shouldmatchers" % "3.2.11"   % "test",
      "org.scalatest"          %% "scalatest-wordspec"       % "3.2.11"   % "test",
      "org.scalatestplus"      %% "scalacheck-1-15"          % "3.2.11.0" % "test"
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature"
    ),
    Compile / javacOptions ++= Seq(
      "-source",
      "1.8",
      "-target",
      "1.8"
    ),
    publishMavenStyle := true,
    publishTo := Some {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        "snapshots" at nexus + "content/repositories/snapshots"
      else
        "releases" at nexus + "service/local/staging/deploy/maven2"
    },
    releaseVersion := { ver =>
      val bumpedVersion = Version(ver)
        .map { v =>
          suggestedBump.value match {
            case Version.Bump.Bugfix => v.withoutQualifier.string
            case _ => v.bump(suggestedBump.value).withoutQualifier.string
          }
        }
        .getOrElse(versionFormatError(ver))
      bumpedVersion
    },
    releaseNextVersion := { ver =>
      Version(ver).map(_.withoutQualifier.bump.string).getOrElse(versionFormatError(ver)) + "-SNAPSHOT"
    },
    releaseCommitMessage := s"[sbt-release] setting version to ${(ThisBuild / version).value}",
    bugfixRegexes := List(s"${Pattern.quote("[patch]")}.*").map(_.r),
    minorRegexes := List(s"${Pattern.quote("[minor]")}.*").map(_.r),
    majorRegexes := List(s"${Pattern.quote("[major]")}.*").map(_.r),
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("publishSigned"),
      setNextVersion,
      commitNextVersion,
      releaseStepCommand("sonatypeRelease"),
      pushChanges
    ),
    scalafmtOnCompile := true
  )
