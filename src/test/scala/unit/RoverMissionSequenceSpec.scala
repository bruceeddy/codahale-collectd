package unit

import mars._
import mars.Mission
import mars.Point
import mars.Position
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito


class RoverMissionSequenceSpec extends Specification with Mockito {

  "a rover mission sequence" should {

    "apply a single mission" in {

        object test extends RoverMissions {
          val rover = mock[MarsRover]
        }

        val mission = mock[Mission]
        test.finalPositions(Seq(mission))

        there was one(test.rover).finalPosition(mission)

    }

    "apply a sequence of missions" in {

      val missions = (1 to 10).map(n => mock[Mission].as("mission"+n)).toArray

      object test extends RoverMissions {
        val rover = mock[MarsRover]
      }

      test.finalPositions(missions)
      there was
        one(test.rover).finalPosition(missions.head) andThen
        one(test.rover).finalPosition(missions.head)
    }

    "apply the sequence of missions given in the problem description" in {
      val mission1 = Mission(Position(Point(1,2),North), List(Left, Move, Left, Move, Left, Move, Left, Move, Move))
      val mission2 = Mission(Position(Point(3,3),East),List(Move, Move, Right, Move, Move, Right, Move, Right, Right, Move))

      val finalPositions = ConcreteRoverMissions.finalPositions(List(mission1, mission2))
      finalPositions must beEqualTo(List(Position(Point(1,3),North), Position(Point(5,1),East)))
    }


  }

}
