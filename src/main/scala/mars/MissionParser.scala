package mars

import scala.util.parsing.combinator.RegexParsers

object MissionParser extends RegexParsers with Parser {
  def coord: Parser[Int] = "[0-9]".r ^^ (_.toInt)

  def point: Parser[Point] = coord ~ coord ^^ {
    case x ~ y => Point(x, y)
  }

  def heading: Parser[Heading] = ("["+Heading.allLabels+"]").r ^^ ( Heading.labelledWith(_))

  def position: Parser[Position] = point ~ heading ^^ {
    case point ~ heading => Position(point, heading)
  }

  def commands: Parser[Seq[Command]] = "[MLR]*".r ^^ {
    _.toList.map(_ match {
      case 'M' => Move
      case 'L' => Left
      case 'R' => Right
    })
  }

  def mission: Parser[Mission] = position ~ commands ^^ {
    case position ~ commands => Mission(position, commands)
  }

  def missions: Parser[Seq[Mission]] = rep(mission)

  def inputText: Parser[Seq[Mission]] = point ~ missions ^^ {
    case _ ~ missions => missions
  }

  def parseInput(input: String): Seq[Mission] = parseAll(inputText, input).get
}