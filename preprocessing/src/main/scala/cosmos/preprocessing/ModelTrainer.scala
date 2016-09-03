package cosmos.preprocessing

import akka.actor.{Actor, ActorLogging, Props}

import aianonymous.commons.core.protocols._, Implicits._

/**
 * Protocols to communicate with ModelTrainer
 */
sealed trait ModelTrainerProtocol
case class TrainModel(infile: String) extends ModelTrainerProtocol with Replyable[String]

/**
 * Actor for model training
 */
class ModelTrainer extends Actor with ActorLogging {

  def receive = {
    case TrainModel(infile) => sender() ! "/tmp/output.dat"

    case _ =>
  }

}