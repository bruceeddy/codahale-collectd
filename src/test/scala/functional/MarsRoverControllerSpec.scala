package functional

import mars.ConcreteMarsRoverController
import org.specs2.mutable.Specification

class MarsRoverControllerSpec extends Specification {

  "a mars rover controller" should {

    "respond with the correct final positions to an input string" in {
      val input =
        """5 5
          |1 2 N
          |LMLMLMLMM
          |3 3 E
          |MMRMMRMRRM
        """.stripMargin
      val expected =   """1 3 N
                         |5 1 E""".stripMargin

      ConcreteMarsRoverController.outcome(input) must beEqualTo(expected)
    }
  }
}