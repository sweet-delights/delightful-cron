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

import scala.util.parsing.combinator.*
import CronToken.{Hash, Wildcard}
import sweet.delights.cron.Cron

class CronParser extends RegexParsers:
  override def skipWhitespace: Boolean = false
  
  type Term = (List[String], Option[Range])

  def int: Parser[Range] = """\d+""".r ^^ { i => i.toInt to i.toInt }
  def step: Parser[Int] = """/""" ~> """\d+""".r ^^ { _.toInt }
  def range: Parser[Range] = "(" ~> ( """\d+""".r ~ ( "-" ~> """\d+""".r ) <~ ")" ) ^^ {
    case start ~ end => start.toInt to end.toInt
  }
  
  def hashTerm(default: Range): Parser[CronTerm] = "H" ~> range.? ~ step.? ^^ {
    case range ~ step =>
      val r = range.getOrElse(default)
      val rs = r.start to r.end by step.getOrElse(1)
      CronTerm(Some(Hash), rs)
  }
  
  def wildcardTerm(default: Range): Parser[CronTerm] = "*" ~> step.? ^^ {
    case step => CronTerm(Some(Wildcard), step.map(default.start to default.end by _).getOrElse(default))
  }
  
  def intTerm: Parser[CronTerm] = int ^^ { range => CronTerm(None, range) }
  def rangeTerm: Parser[CronTerm] = range ~ step.? ^^ {
    case range ~ step =>
      val rs = range.start to range.end by step.getOrElse(1)
      CronTerm(None, rs)
  }

  def term(default: Range): Parser[CronTerm] = hashTerm(default) | wildcardTerm(default) | intTerm | rangeTerm

  def yearly: Parser[CronExpr] = ( "@yearly" | "@annually" ) ^^ { _ => Cron.yearly }
  def monthly: Parser[CronExpr] = "@monthly" ^^ { _ => Cron.monthly }
  def weekly: Parser[CronExpr] = "@weekly" ^^ { _ => Cron.weekly }
  def daily: Parser[CronExpr] = "@daily" ^^ { _ => Cron.daily }
  def midnight: Parser[CronExpr] = "@midnight" ^^ { _ => Cron.midnight }
  def hourly: Parser[CronExpr] = "@hourly" ^^ { _ => Cron.hourly }
  def manual: Parser[CronExpr] = "@manual" ^^ { _ => Cron.manual }
  def reboot: Parser[CronExpr] = "@reboot" ^^ { _ => Cron.reboot }
  
  def minute: Parser[List[CronTerm]] = repsep(term(Minute.range), ",")
  def hour: Parser[List[CronTerm]] = repsep(term(Hour.range), ",")
  def day: Parser[List[CronTerm]] = repsep(term(Day.range), ",")
  def month: Parser[List[CronTerm]] = repsep(term(Month.range), ",")
  def dow: Parser[List[CronTerm]] = repsep(term(DayOfWeek.range), ",")
  
  def spec: Parser[CronExpr] = minute ~ (whiteSpace ~> hour) ~ (whiteSpace ~> day) ~ (whiteSpace ~> month) ~ (whiteSpace ~> dow) ^^ {
    case m ~ h ~ d ~ mo ~ w => CronExpr.CronSpec(m, h, d, mo, w)
  }
  
  def cron: Parser[CronExpr] = yearly | monthly | weekly | daily | midnight | hourly | manual | reboot | spec