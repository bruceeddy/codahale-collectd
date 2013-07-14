package mars

abstract sealed class Heading {
  def label: String = Heading.labels.get(this).get

  val clockwiseNext: Heading
  lazy val anticlockwiseNext = clockwiseNext.clockwiseNext.clockwiseNext

  val effectOnX: Int
  val effectOnY: Int
  def oneStepFrom(point: Point) = Point(point.x+effectOnX, point.y+effectOnY)
}

object Heading  {
  private lazy val labels: Map[Heading,String] = Map(
    North -> "N",
    South -> "S",
    East -> "E",
    West -> "W"
  )
  def labelledWith(label: String): Heading = labels.filter(_._2 == label).keys.head
  lazy val allLabels = labels.values.mkString
}

trait Vertical { val effectOnX = 0}
trait Horizontal { val effectOnY = 0}
case object North extends Heading with Vertical { val clockwiseNext = East; val effectOnY = 1}
case object South extends Heading with Vertical { val clockwiseNext = West; val effectOnY = -1}
case object East extends Heading with Horizontal  { val clockwiseNext = South; val effectOnX = 1}
case object West extends Heading with Horizontal { val clockwiseNext = North; val effectOnX = -1}

case class Point(x: Int, y: Int)

case class Position(point: Point, heading: Heading) {
  def asOutputString = point.x + " " + point.y + " " + heading.label
}

abstract sealed class Command
case object Move extends Command
case object Left extends Command
case object Right extends Command

case class Mission(startingPosition: Position, actions: Seq[Command])

