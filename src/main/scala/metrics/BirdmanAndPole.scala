
object BirdmanAndPole {

  type Birds = Int
  type Pole = (Birds, Birds)

  object Option {

    val landLeft: ((Birds, Pole) => Option[Pole]) = (n: Birds, pole: Pole) => pole match {
      case (left, right) if (Math.abs((left + n) - right) < 4) => Some((left + n, right))
      case _ => None
    }

    val landRight: ((Birds, Pole) => Option[Pole]) = (n: Birds, pole: Pole) => pole match {
      case (left, right) if (Math.abs(left - (right + n)) < 4) => Some((left + n, right))
      case _ => None
    }

    val example = landLeft(1,(0,0)).flatMap(landLeft(1,_)).flatMap(landRight(6,_)) 


  }

  object CurriedOption  {


  }

  object NoOption  {
    
  }

  object CurriedNoOption {

    val landLeft: (Birds) => (Pole) => Pole = (n: Birds) => (pole: Pole) => (pole._1, pole._2 + n)
    val landRight: (Birds) => (Pole) => Pole = (n: Birds) => (pole: Pole) => (pole._1 + n, pole._2)

    val example = (landLeft(1) compose landLeft(2)) ((0,0))
  }
}
