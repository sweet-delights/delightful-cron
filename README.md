[![Build Status](https://travis-ci.com/sweet-delights/delightful-edifact.svg?branch=master)](https://travis-ci.com/sweet-delights/delightful-edifact)
[![Maven Central](https://img.shields.io/maven-central/v/org.sweet-delights/delightful-edifact_2.13.svg)](https://maven-badges.herokuapp.com/maven-central/org.sweet-delights/delightful-edifact_2.13)

`delightful-cron` is a library for parsing [Jenkins-like cron specifications](https://www.jenkins.io/doc/book/pipeline/syntax/#cron-syntax)
with the symbol `H` (Hash). It is able to compile such cron specs to [Quartz](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html) -compatble syntax.

## Quick Start

### SBT

In `build.sbt`, add:
```scala
libraryDependencies += "org.sweet-delights" %% "delightful-cron" % "0.0.1"
```

### Maven
```xml
<dependency>
  <groupId>org.sweet-delights</groupId>
  <artifactId>delightful-cron_3</artifactId>
  <version>0.0.1</version>
</dependency>
```

NB: `delightful-cron` is compiled against Scala 3 only.

### Code

Here is how to parse cron specifications or manually create one:

```scala
import sweet.delights.cron.Cron
import sweet.delights.cron.CronToken.{Hash => H, Wildcard => `*`}

val cron = Cron("H 12 * * *")
// or
val cron = Cron(H, 12, `*`, `*`, `*`)
```

### Apply hash value / convert to Quartz

In order to convert to Quartz, provide a hash value to be applied. A hash can be either a `String` or an `Int`: 

```scala
import sweet.delights.cron.Cron

val quartz = Cron("H 12 * * *").withHash("foo")
// or
val quartz = Cron("H 12 * * *").withHash(101574)
```

Either way, the cron spec is evaluated to `Cron("54 12 * * *")`.

## [License](LICENSE.md)

All files in `delightful-edifact` are under the GNU Lesser General Public License version 3.
Please read files [`COPYING`]("COPYING") and [`COPYING.LESSER`]("COPYING.LESSER") for details.

