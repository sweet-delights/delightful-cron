// This file is part of delightful-cron.
//
// delightful-edifact is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
package sweet.delights.cron

import java.time.{LocalDate, LocalDateTime, LocalTime}
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.Prop.{forAll, forAllNoShrink, throws}
import org.scalacheck.Shrink
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import CronSymbol.{Hash => H, Wildcard => `*`}

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

class CronSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks:

  lazy val epoch = LocalDateTime.of(LocalDate.ofEpochDay(0), LocalTime.MIDNIGHT)

  lazy val minListSize = 1

  lazy val maxListSize = 5

  val genLocalDateTime = for {
    rand <- Gen.choose(0, Int.MaxValue)
  } yield epoch.plusMinutes(rand)

  val genCronSymbol = Gen.oneOf(CronSymbol.Hash, CronSymbol.Wildcard)

  def genRange(default: Range) = for {
    start <- Gen.choose(default.start, default.end)
    end <- Gen.choose(start, default.end)
    step <- Gen.choose(1, math.max(end - start, 1))
  } yield Range.inclusive(start, end, step)

  def genCronTerm(default: Range): Gen[(CronTerm, String)] = for {
    symbol <- genCronSymbol
    keep <- Arbitrary.arbitrary[Boolean]
    range <- genRange(default)
  } yield Option(symbol).filter(_ => keep) match
    case Some(CronSymbol.Wildcard) if range.step > 1 => (CronTerm(Some(CronSymbol.Wildcard), Range(default.start, default.end, range.step)), s"*/${range.step}")
    case Some(CronSymbol.Wildcard) => (CronTerm(Some(CronSymbol.Wildcard), default), "*")
    case Some(CronSymbol.Hash) if range == default => (CronTerm(Some(CronSymbol.Hash), default), "H")
    case Some(CronSymbol.Hash) if range.start == default.start && range.end == default.end => (CronTerm(Some(CronSymbol.Hash), range), s"H/${range.step}")
    case Some(CronSymbol.Hash) if range.step > 1 => (CronTerm(Some(CronSymbol.Hash), range), s"H(${range.start}-${range.end})/${range.step}")
    case Some(CronSymbol.Hash) => (CronTerm(Some(CronSymbol.Hash), range), s"H(${range.start}-${range.end})")
    case None if range.start == range.end => (CronTerm(None, Range(range.start, range.end, 1)), s"${range.start}")
    case None if range.step > 1 => (CronTerm(None, range), s"(${range.start}-${range.end})/${range.step}")
    case None => (CronTerm(None, range), s"(${range.start}-${range.end})")

  val genMinute = genCronTerm(Minute.range)
  val genHour = genCronTerm(Hour.range)
  val genDay = genCronTerm(Day.range)
  val genMonth = genCronTerm(Month.range)
  val genDow = genCronTerm(DayOfWeek.range)

  val genReboot = Gen.const((CronExpr.Reboot, "@reboot"))
  val genManual = Gen.const((CronExpr.Manual, "@manual"))
  val genCronSpec = for {
    minutes <- Gen.listOfN(maxListSize, genMinute).suchThat(_.size >= minListSize)
    hours <- Gen.listOfN(maxListSize, genHour).suchThat(_.size >= minListSize)
    days <- Gen.listOfN(maxListSize, genDay).suchThat(_.size >= minListSize)
    months <- Gen.listOfN(maxListSize, genMonth).suchThat(_.size >= minListSize)
    dows <- Gen.listOfN(maxListSize, genDow).suchThat(_.size >= minListSize)
  } yield (
    CronExpr.CronSpec(
      minutes.map(_._1),
      hours.map(_._1),
      days.map(_._1),
      months.map(_._1),
      dows.map(_._1)
    ),
    List(
      minutes.map(_._2).mkString(","),
      hours.map(_._2).mkString(","),
      days.map(_._2).mkString(","),
      months.map(_._2).mkString(","),
      dows.map(_._2).mkString(",")
    ).mkString(" ")
  )

  val genCronExpr = Gen.oneOf(genReboot, genManual, genCronSpec)

  implicit val noShrinking: Shrink[(CronExpr, String)] = Shrink(_ => Stream.empty)

  "Cron" should {
    "parse predefined specs" in {
      Cron("@yearly") shouldBe Cron.yearly
      Cron("@annually") shouldBe Cron.yearly
      Cron("@monthly") shouldBe Cron.monthly
      Cron("@weekly") shouldBe Cron.weekly
      Cron("@daily") shouldBe Cron.daily
      Cron("@midnight") shouldBe Cron.midnight
      Cron("@hourly") shouldBe Cron.hourly
      Cron("@manual") shouldBe Cron.manual
      Cron("@reboot") shouldBe Cron.reboot
    }

    "parse random cron spec from date" in {
      forAll(genLocalDateTime) { ldt =>
        val spec = s"${ldt.getMinute} ${ldt.getHour} ${ldt.getDayOfMonth} ${ldt.getMonthValue} ${ldt.getDayOfWeek.getValue}"
        val expected = Cron(ldt.getMinute, ldt.getHour, ldt.getDayOfMonth, ldt.getMonthValue, ldt.getDayOfWeek.getValue)
        Cron(spec) shouldBe expected
      }
    }

    "parse random cron spec" in {
      forAll(genCronExpr) { case (term, str) =>
        Cron(str) === term
      }
    }

    "raise exception on invalid cron specs" in {
      val invalidSpecs = Table(
        "spec",
        "dummy",
        "H H * * ?"
      )

      forAll(invalidSpecs) { spec =>
        an[IllegalArgumentException] should be thrownBy {
          Cron(spec)
        }
      }
    }

    "match all wildcards" in {
      forAll(genLocalDateTime) { ldt =>
        val cron = Cron(`*`, `*`, `*`, `*`, `*`)
        cron.matches(ldt) shouldBe true
      }
    }

    "match random scron spec from date" in {
      forAll(genLocalDateTime) { ldt =>
        val cron = Cron(ldt.getMinute, ldt.getHour, ldt.getDayOfMonth, ldt.getMonthValue, ldt.getDayOfWeek.getValue)
        cron.matches(ldt) shouldBe true
      }
    }

    "replace Hash symbol with actual value" in {
      Cron("H H H H H").withHash("foo") shouldBe Cron("54 6 19 7 6")
      Cron("H/5 0 1 1 0").withHash("foo") shouldBe Cron("55 0 1 1 0")
      Cron("H(12-15) 0 1 1 0").withHash("foo") shouldBe Cron("14 0 1 1 0")
      Cron("H(12-22)/5 0 1 1 0").withHash("foo") shouldBe Cron("12 0 1 1 0")
      Cron("* * * * *").withHash("foo") shouldBe Cron("* * * * *")
    }

    "detect hashes" in {
      Cron("H 0 1 1 0").hasHash shouldBe true
      Cron("0 0 1 1 0").hasHash shouldBe false
    }

    "stringify" in {
      forAll(genCronExpr) { case (term, str) =>
        Cron(str).toString === str
      }
    }
  }
