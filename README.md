[![Build Status](https://github.com/sweet-delights/delightful-cron/actions/workflows/scala.yml/badge.svg)](https://github.com/sweet-delights/delightful-cron/actions/workflows/scala.yml)
[![Maven Central](https://img.shields.io/maven-central/v/org.sweet-delights/delightful-cron_3.svg)](https://maven-badges.herokuapp.com/maven-central/org.sweet-delights/delightful-cron_3)

`delightful-cron` is a library for parsing [Jenkins-like cron specifications](https://www.jenkins.io/doc/book/pipeline/syntax/#cron-syntax)
with the symbol `H` (Hash). It is able to compile such cron specs to a normalized form, compatible with [crontab.guru](https://crontab.guru/) 's syntax.

## Quick Start

### SBT

In `build.sbt`, add:
```scala
libraryDependencies += "org.sweet-delights" %% "delightful-cron" % "0.1.1"
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
import sweet.delights.cron.Cron._
import sweet.delights.cron.CronSymbol.{Hash => H, Wildcard => `*`}

val cron = "H 12 * * *".toCron
// or
val cron = Cron("H 12 * * *")
// or
val cron = Cron(H, 12, `*`, `*`, `*`)
```

### Apply hash value / convert to normalized form

In order to convert to a normalized form, provide a hash value to be applied. A hash can be either a `String` or an `Int`: 

```scala
import sweet.delights.cron.Cron

val normalized = "H 12 * * *".toCron.withHash("foo")
// or
val normalized = "H 12 * * *".toCron.withHash(101574)
```

Either way, the cron spec is evaluated to `"54 12 * * *"`.

### REST API

`delightful-cron` is available as a REST API, with two mandatory parameters:
- `cron`: a cron specification
- `hash`: either a integer between -2147483648 and 2147483647 or a string

Example making a `curl` call for cron spec `H H * * *` and hash `foo`:
```bash
% curl -G --data-urlencode 'cron=H H * * *' --data-urlencode 'hash=foo' 'https://sweet-delights.azurewebsites.net/api/delightful-cron'
54 6 * * *
```

Example with an integer hash:
```bash
% curl -G --data-urlencode 'cron=H H * * *' --data-urlencode 'hash=1234567890' 'https://sweet-delights.azurewebsites.net/api/delightful-cron'
30 18 * * *
```

## [License](LICENSE.md)

All files in `delightful-cron` are under the GNU Lesser General Public License version 3.
Please read files [`COPYING`]("COPYING") and [`COPYING.LESSER`]("COPYING.LESSER") for details.

