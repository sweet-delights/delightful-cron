package sweet.delights.cron

import java.time.MonthDay
import scala.util.Try
import CronToken.{Hash as H, Wildcard as `*`}

import scala.reflect.ClassTag

object Cron:
  lazy val yearly = Cron(H, H, H, H, `*`)
  lazy val monthly = Cron(H, H, H, `*`, `*`)
  lazy val weekly = Cron(H, H, `*`, `*`, H)
  lazy val daily = Cron(H, H, `*`, `*`, `*`)
  lazy val midnight = Cron(H, CronTerm.ofHour(H).withRange(0, 2, 1), `*`, `*`, `*`)
  lazy val hourly = Cron(H, `*`, `*`, `*`, `*`)
  lazy val reboot = CronExpr.Reboot
  lazy val manual = CronExpr.Manual

  def apply(
             minute: Minute | CronTerm,
             hour: Hour | CronTerm,
             day: Day | CronTerm,
             month: Month | CronTerm,
             dow: DayOfWeek | CronTerm
           ): CronExpr =
    def toList[T : ClassTag](v: T | CronTerm, of: T => CronTerm): List[CronTerm] = v match
      case c: CronTerm => c :: Nil
      case t: T => of(t) :: Nil
      case _ => throw IllegalArgumentException(s"Unknown type")

    CronExpr.CronSpec(
      toList[Minute](minute, CronTerm.ofMinute),
      toList[Hour](hour, CronTerm.ofHour),
      toList[Day](day, CronTerm.ofDay),
      toList[Month](month, CronTerm.ofMonth),
      toList[DayOfWeek](dow, CronTerm.ofDow)
    )
  
  def apply(s: String): CronExpr =
    val parser = CronParser()
    val result = parser.parse[CronExpr](parser.cron, s)
    
    val parsed = result match
      case parser.Success(result, _) => result
      case parser.Failure(msg, _) => throw IllegalArgumentException(msg)
      case parser.Error(msg, _) => throw IllegalArgumentException(msg)
    
    check(parsed)
  
  private def check(cron: CronExpr): CronExpr = cron match
    case CronExpr.Reboot => cron
    case CronExpr.Manual => cron
    case CronExpr.CronSpec(minutes, hours, days, months, dows) =>
      def termsValid(range: Range)(terms: List[CronTerm]) = terms.forall(term => range.contains(term.range.start) && range.contains(term.range.end))
      
      if (!termsValid(Minute.range)(minutes)) throw IllegalArgumentException("Invalid minute specification")
      if (!termsValid(Hour.range)(hours)) throw IllegalArgumentException("Invalid hour specification")
      if (!termsValid(Day.range)(days)) throw IllegalArgumentException("Invalid day specification")
      if (!termsValid(Month.range)(months)) throw IllegalArgumentException("Invalid month specification")
      if (!termsValid(DayOfWeek.range)(dows)) throw IllegalArgumentException("Invalid day of week specification")

      cron
