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

import java.time.LocalDateTime

sealed abstract class CronExpr:

  def hasHash: Boolean = this match
    case CronExpr.Reboot | CronExpr.Manual => false
    case CronExpr.CronSpec(minutes, hours, days, months, dows) =>
      minutes.exists(_.hasHash)
        || hours.exists(_.hasHash)
        || days.exists(_.hasHash)
        || months.exists(_.hasHash)
        || dows.exists(_.hasHash)

  def matches(ldt: LocalDateTime, hashCode: Int = 0): Boolean = this match
    case CronExpr.Reboot | CronExpr.Manual => true
    case CronExpr.CronSpec(minutes, hours, days, months, dows) =>
      minutes.exists(_.matches(ldt.getMinute, hashCode))
        && hours.exists(_.matches(ldt.getHour, hashCode))
        && (days.exists(_.matches(ldt.getDayOfMonth, hashCode)) || dows.exists(_.matches(ldt.getDayOfWeek.getValue, hashCode)))
        && months.exists(_.matches(ldt.getMonthValue, hashCode))

  def withHash(hashCode: Int | String): CronExpr = this match
    case CronExpr.Reboot => this
    case CronExpr.Manual => this
    case CronExpr.CronSpec(minutes, hours, days, months, dows) =>
      CronExpr.CronSpec(
        minutes.map(_.withHash(hashCode)),
        hours.map(_.withHash(hashCode)),
        days.map(_.withHash(hashCode)),
        months.map(_.withHash(hashCode)),
        dows.map(_.withHash(hashCode))
      )

  override def toString: String = this match
    case CronExpr.Reboot => "@reboot"
    case CronExpr.Manual => "@manual"
    case CronExpr.CronSpec(minutes, hours, days, months, dows) =>
      val m = minutes.map(_.toString(Minute.range)).mkString(",")
      val h = hours.map(_.toString(Hour.range)).mkString(",")
      val d = days.map(_.toString(Day.range)).mkString(",")
      val mo = months.map(_.toString(Month.range)).mkString(",")
      val dw = dows.map(_.toString(DayOfWeek.range)).mkString(",")

      s"${m} ${h} ${d} ${mo} ${dw}"

object CronExpr:
  case object Reboot extends CronExpr
  case object Manual extends CronExpr
  case class CronSpec(
    minutes: List[CronTerm],
    hours: List[CronTerm],
    days: List[CronTerm],
    months: List[CronTerm],
    dows: List[CronTerm]
  ) extends CronExpr
