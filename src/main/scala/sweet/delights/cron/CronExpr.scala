package sweet.delights.cron

import java.time.LocalDateTime

sealed abstract class CronExpr:

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
    case CronExpr.CronSpec(minutes, hours, days, months, dows) => CronExpr.CronSpec(
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
      s"${minutes.mkString(",")} ${hours.mkString(",")} ${days.mkString(",")} ${months.mkString(",")} ${dows.mkString(",")}"

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
