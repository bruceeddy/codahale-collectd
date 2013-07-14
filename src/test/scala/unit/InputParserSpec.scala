package unit

import mars._
import mars.Point
import mars.Position
import org.specs2.mutable.Specification


class InputParserSpec extends Specification {


  "an input parser" should {

    "parse a single digit integer" in {
      MissionParser.parseAll(MissionParser.coord,"2").get must beEqualTo(2)
    }

    "parse a space separated pair of single digit integers as a Point" in {
      MissionParser.parseAll(MissionParser.point,"4 2").get must beEqualTo(Point(4,2))
    }

    "parse a single compass point letter as a Heading" in {
      MissionParser.parseAll(MissionParser.heading,"W").get must beEqualTo(West)
    }

    "parse a location line to a Position object" in {
      val input1 = "1 2 N"
      val input2 = "2 3 E"

      MissionParser.parseAll(MissionParser.position, input1).get should beEqualTo(Position(Point(1,2),North))
      MissionParser.parseAll(MissionParser.position, input2).get should beEqualTo(Position(Point(2,3),East))
    }

    "parse a pair of location-command lines as a Mission" in {
      val input1 =
        """1 2 N
          |LMLMLMLMM
        """.stripMargin
      MissionParser.parseAll(MissionParser.mission, input1).get should beEqualTo(Mission(Position(Point(1,2),North),Seq(Left, Move, Left, Move, Left, Move, Left, Move, Move)))

    }

    "parse a sequence of pairs of location-command lines as a sequence of Missions" in {
      val input1 =
        """1 2 N
          |LMLMLMLMM
          |3 3 E
          |MMRMMRMRRM
        """.stripMargin
      MissionParser.parseAll(MissionParser.missions, input1).get should beEqualTo(
        List(
          Mission(Position(Point(1,2),North),Seq(Left, Move, Left, Move, Left, Move, Left, Move, Move)),
          Mission(Position(Point(3,3),East),Seq(Move, Move, Right, Move, Move, Right, Move, Right, Right, Move))
        ))
    }

    "parse a sequence of pairs of location-command lines as a sequence of Missions, ignoring a size specification on the first line" in {
      val input1 =
        """5 5
          |1 2 N
          |LMLMLMLMM
          |3 3 E
          |MMRMMRMRRM
        """.stripMargin
      MissionParser.parseInput(input1) should beEqualTo(
        List(
          Mission(Position(Point(1,2),North),Seq(Left, Move, Left, Move, Left, Move, Left, Move, Move)),
          Mission(Position(Point(3,3),East),Seq(Move, Move, Right, Move, Move, Right, Move, Right, Right, Move))
        ))
    }
  }
}