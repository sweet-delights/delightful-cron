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

import java.time.{LocalDateTime, MonthDay}
import scala.reflect.ClassTag
import scala.util.Try

case class CronTerm(
  symbol: Option[CronSymbol],
  range: Range
):

  def hasHash: Boolean = symbol match
    case Some(CronSymbol.Hash) => true
    case _ => false

  def matches(i: Int, hashCode: Int = 0): Boolean = this match
    case CronTerm(Some(CronSymbol.Hash), range) =>
      withHash(hashCode).matches(i)

    case CronTerm(_, range) =>
      range.contains(i)

  def withHash(hashCode: Int | String): CronTerm = this match
    case CronTerm(None, _) | CronTerm(Some(CronSymbol.Wildcard), _) => this
    case CronTerm(Some(CronSymbol.Hash), range) =>
      if (range.size == 1) CronTerm(None, range)
      else
        val h = hashCode match
          case i: Int => i
          case s: String => s.hashCode

        val n = range.end - range.start + 1
        val m = (h % n + n) % n + range.start
        val r = range.find(_ >= m).getOrElse(range.start)
        CronTerm(None, r to r)

  def withRange(range: Range) = this.copy(range = range)

  def withRange(start: Int = range.start, end: Int = range.end, step: Int = range.step) =
    this.copy(range = start to end by step)

  private[cron] def toString(orig: Range): String = symbol match
    case Some(CronSymbol.Hash) =>
      if (range == orig) "H"
      else if (range.start == orig.end) s"H/${range.step}"
      else if (range.step == orig.step) s"H(${range.start}-${range.end})"
      else s"H(${range.start}-${range.end})/${range.step}"

    case Some(CronSymbol.Wildcard) =>
      if (range.step == orig.step) "*"
      else s"*/${range.step}"

    case None =>
      val rng = if (range.start == range.end) s"${range.start}" else s"(${range.start}-${range.end})"
      val stp = if (range.step == 1) "" else s"/${range.step}"

      s"${rng}${stp}"

type Minute = Int | CronSymbol
type Hour = Int | CronSymbol
type Day = Int | CronSymbol
type Month = Int | CronSymbol
type DayOfWeek = Int | CronSymbol

object CronTerm:
  def ofMinute(v: Minute): CronTerm = of[Minute](v, Minute.range)
  def ofHour(v: Minute): CronTerm = of[Hour](v, Hour.range)
  def ofDay(v: Minute): CronTerm = of[Day](v, Day.range)
  def ofMonth(v: Hour): CronTerm = of[Month](v, Month.range)
  def ofDow(v: DayOfWeek): CronTerm = of[DayOfWeek](v, DayOfWeek.range)

  private def of[T](v: T, default: Range): CronTerm = v match
    case i: Int => CronTerm(None, i to i)
    case t: CronSymbol => CronTerm(Some(t), default)

trait CronProperties[T: ClassTag]:
  def range: Range
  def check(i: Int): Boolean = range.contains(i)

object Minute extends CronProperties[Minute]:
  lazy val range = 0 to 59

object Hour extends CronProperties[Hour]:
  lazy val range = 0 to 23

object Day extends CronProperties[Day]:
  lazy val range = 1 to 31
  def checkWithMonth(i: Int, month: java.time.Month): Boolean = Try(MonthDay.of(month, i)).isSuccess

object Month extends CronProperties[Month]:
  lazy val range = 1 to 12

object DayOfWeek extends CronProperties[DayOfWeek]:
  lazy val range = 0 to 7
