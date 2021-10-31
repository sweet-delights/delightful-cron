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

enum CronToken:
  case Hash
  case Wildcard

object CronToken:
  def apply(s: String): CronToken = unapply(s).getOrElse {
    throw IllegalArgumentException(s"""Invalid CronExpr "${s}"""")
  }

  def unapply(s: String): Option[CronToken] = s match
    case "H" => Some(Hash)
    case "*" => Some(Wildcard)
    case _ => None
