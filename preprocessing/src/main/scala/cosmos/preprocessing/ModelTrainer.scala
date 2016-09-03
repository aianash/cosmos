package cosmos.preprocessing

import akka.actor.{Actor, ActorLogging, Props}


class ModelTrainer extends Actor with ActorLogging {

  import protocols._

  def receive = {
    case TrainModel(infile) => sender() ! "/tmp/output.dat"

    case _ =>
  }

}