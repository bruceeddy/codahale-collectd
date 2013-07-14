package mars

trait Parser  {
  def parseInput(spec: String): Seq[Mission]
}

abstract class RoverMissions  {
  val rover: MarsRover
  def finalPositions(missions: Seq[Mission]) = missions.map(rover.finalPosition(_))
}

abstract class MarsRoverController {
  val parser: Parser
  val missions: RoverMissions

  def outcome(spec: String) : String = {
    val input: Seq[Mission] = parser.parseInput(spec)
    val finalPositions: Seq[Position] = missions.finalPositions(input)
    finalPositions.map(_.asOutputString).mkString("\n")
  }
}


abstract class MarsRover {
  def finalPosition(mission: Mission): Position
}

object ConcreteMarsRoverController extends MarsRoverController {
  val parser = MissionParser
  val missions = ConcreteRoverMissions
}
object ConcreteRoverMissions extends RoverMissions  {
  val rover = ConcreteMarsRover
}

object ConcreteMarsRover extends MarsRover {

  private def locationAfterExecutingCommand(currentPosition: Position, command: Command): Position = {
    val Position(point, heading) = currentPosition
    command match {
      case Move => Position(heading.oneStepFrom(point), heading)
      case Left => Position(point, heading.anticlockwiseNext)
      case Right => Position(point, heading.clockwiseNext)
    }
  }

  def finalPosition(mission: Mission) = {
    val Mission(startingPosition, missionCommands) = mission
    missionCommands.foldLeft(startingPosition)(locationAfterExecutingCommand)
  }
}


