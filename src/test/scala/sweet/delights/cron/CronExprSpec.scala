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
//package sweet.delights.pipelines
//
//import org.scalatest.wordspec.AnyWordSpec
//import org.scalatest.matchers.should.Matchers
//
//class CronExprSpec extends AnyWordSpec with Matchers {
//
//  "CronExpr" should {
//    "extract hash" in {
//      CronToken("H") shouldBe CronToken.Hash
//    }
//
//    "extract wildcard" in {
//      CronToken("*") shouldBe CronToken.Wildcard
//    }
//
//    "fail on invalid" in {
//      an[IllegalArgumentException] should be thrownBy CronToken("foo")
//    }
//  }
//
//  "Minute" should {
//    "extract hash" in {
//      Minute("H") shouldBe CronToken.Hash
//    }
//
//    "extract wildcard" in {
//      Minute("*") shouldBe CronToken.Wildcard
//    }
//
//    "extract int within range" in {
//      Minute("0") shouldBe 0
//    }
//
//    "fail on out of range" in {
//      an[IllegalArgumentException] should be thrownBy Minute("61")
//    }
//
//    "fail on invalid" in {
//      an[IllegalArgumentException] should be thrownBy Minute("foo")
//    }
//  }
//
//  "Hour" should {
//    "extract hash" in {
//      Hour("H") shouldBe CronToken.Hash
//    }
//
//    "extract wildcard" in {
//      Hour("*") shouldBe CronToken.Wildcard
//    }
//
//    "extract int within range" in {
//      Hour("0") shouldBe 0
//    }
//
//    "fail on out of range" in {
//      an[IllegalArgumentException] should be thrownBy Hour("61")
//    }
//
//    "fail on invalid" in {
//      an[IllegalArgumentException] should be thrownBy Hour("foo")
//    }
//  }
//
//  "Day" should {
//    "extract hash" in {
//      Day("H") shouldBe CronToken.Hash
//    }
//
//    "extract wildcard" in {
//      Day("*") shouldBe CronToken.Wildcard
//    }
//
//    "extract int within range" in {
//      Day("1") shouldBe 1
//    }
//
//    "fail on out of range" in {
//      an[IllegalArgumentException] should be thrownBy Day("0")
//    }
//
//    "fail on invalid" in {
//      an[IllegalArgumentException] should be thrownBy Day("foo")
//    }
//  }
//
//  "Month" should {
//    "extract hash" in {
//      Month("H") shouldBe CronToken.Hash
//    }
//
//    "extract wildcard" in {
//      Month("*") shouldBe CronToken.Wildcard
//    }
//
//    "extract int within range" in {
//      Month("1") shouldBe java.time.Month.JANUARY
//    }
//
//    "fail on out of range" in {
//      an[IllegalArgumentException] should be thrownBy Month("0")
//    }
//
//    "fail on invalid" in {
//      an[IllegalArgumentException] should be thrownBy Month("foo")
//    }
//  }
//
//  "DayOfWeek" should {
//    "extract hash" in {
//      DayOfWeek("H") shouldBe CronToken.Hash
//    }
//
//    "extract wildcard" in {
//      DayOfWeek("*") shouldBe CronToken.Wildcard
//    }
//
//    "extract int within range" in {
//      DayOfWeek("1") shouldBe java.time.DayOfWeek.MONDAY
//    }
//
//    "fail on out of range" in {
//      an[IllegalArgumentException] should be thrownBy DayOfWeek("8")
//    }
//
//    "fail on invalid" in {
//      an[IllegalArgumentException] should be thrownBy DayOfWeek("foo")
//    }
//  }
//  def extractDay =
//    val Day(hash) = "H"
//    val Day(star) = "*"
//    val Day(int) = "1"
////    Assert.assertEquals(CronExpr.Hash, hash)
////    Assert.assertEquals(CronExpr.Star, star)
////    Assert.assertEquals(1, int)
////    Assert.assertEquals(None, Day.unapply("0"))
////    Assert.assertEquals(None, Day.unapply("32"))
////    Assert.assertEquals(None, Day.unapply("foo"))
//
//  def extractMonth =
//    val Month(hash) = "H"
//    val Month(star) = "*"
//    val Month(month) = "1"
////    Assert.assertEquals(CronExpr.Hash, hash)
////    Assert.assertEquals(CronExpr.Star, star)
////    Assert.assertEquals(java.time.Month.JANUARY, month)
////    Assert.assertEquals(None, Month.unapply("0"))
////    Assert.assertEquals(None, Month.unapply("13"))
////    Assert.assertEquals(None, Month.unapply("foo"))
//}
