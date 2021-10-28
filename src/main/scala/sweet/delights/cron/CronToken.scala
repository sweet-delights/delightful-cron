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
