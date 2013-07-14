package unit

import org.specs2.mutable.Specification


class MarsSpec extends Specification {

  case class MarsRover(x: Int, y: Int) {
    def position = (x,y)
  }

  "mars rover" should {

   "report its position" in   {
      MarsRover(0,0).position should beEqualTo((0,0))
    }


  }
}
