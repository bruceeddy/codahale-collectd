package unit

import mars._
import mars.ConcreteMarsRover
import mars.Point
import mars.Position
import org.specs2.mutable.Specification


class MarsSpec extends Specification {
  "mars rover" should {

   "report its starting position if given an empty move sequence" in   {
      val p = Position(Point(1,2),North)
      ConcreteMarsRover.finalPosition(Mission(p, Nil)) should beEqualTo(p)
    }

    "report its position after a single move command" in {
      val start = Position(Point(0,0),North)
      val command = List(Move)

      ConcreteMarsRover.finalPosition(Mission(start,command)) should beEqualTo(Position(Point(0,1),North))
    }

    "report its position after a single rotate command" in {
      val start = Position(Point(0,0),North)
      val command = List(Left)

      ConcreteMarsRover.finalPosition(Mission(start,command)) should beEqualTo(Position(Point(0,0),West))
    }

    "report its position after a single rotate right command" in {
      val start = Position(Point(0,0),North)
      val command = List(Right)

      ConcreteMarsRover.finalPosition(Mission(start,command)) should beEqualTo(Position(Point(0,0),East))
    }

    "report its position after a single move command when starting heading is West" in {
      val start = Position(Point(2,2),West)
      val command = List(Move)

      ConcreteMarsRover.finalPosition(Mission(start,command)) should beEqualTo(Position(Point(1,2),West))
    }

    "report its position after a single move command when starting heading is East" in {
      val start = Position(Point(2,2),East)
      val command = List(Move)

      ConcreteMarsRover.finalPosition(Mission(start,command)) should beEqualTo(Position(Point(3,2),East))
    }

    "report its position after a single rotate command" in {
      val start = Position(Point(0,0),North)
      val command = List(Left)

      ConcreteMarsRover.finalPosition(Mission(start,command)) should beEqualTo(Position(Point(0,0),West))
    }

    "report its position after a single rotate right command when starting heading is East" in {
      val start = Position(Point(0,0),East)
      val command = List(Right)

      ConcreteMarsRover.finalPosition(Mission(start,command)) should beEqualTo(Position(Point(0,0),South))
    }

    "report its position after a single rotate left command when starting heading is East" in {
      val start = Position(Point(0,0),East)
      val command = List(Left)

      ConcreteMarsRover.finalPosition(Mission(start,command)) should beEqualTo(Position(Point(0,0),North))
    }

    "end up in the expected position after a sequence of commands, as given first in the problem description" in {
      val start = Position(Point(1,2),North)
      val commands = List(Left, Move, Left, Move, Left, Move, Left, Move, Move)

      ConcreteMarsRover.finalPosition(Mission(start,commands)) should beEqualTo(Position(Point(1,3),North))
    }

    "end up in the expected position after a sequence of commands, as given second in the problem description" in {
      val start = Position(Point(3,3),East)
      val commands = List(Move, Move, Right, Move, Move, Right, Move, Right, Right, Move)

      ConcreteMarsRover.finalPosition(Mission(start,commands)) should beEqualTo(Position(Point(5,1),East))
    }


  }
}


