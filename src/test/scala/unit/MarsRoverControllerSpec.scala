package unit

import mars._
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito

class MarsRoverControllerSpec extends Specification with Mockito {

   "a mars rover controller" should {

     "return a correctly formatted string of the final positions of the missions" in {

       object test extends MarsRoverController {
         val parser = mock[Parser]
         val missions = mock[RoverMissions]
       }
       val ignored: Seq[Mission] = Seq()
       test.parser.parseInput(any) returns ignored

       test.missions.finalPositions(any) returns Seq(
         Position(Point(1,2),North),
         Position(Point(3,4),South),
         Position(Point(5,6),East))
       test.outcome("") must beEqualTo(
         """1 2 N
           |3 4 S
           |5 6 E""".stripMargin)
     }
   }
 }