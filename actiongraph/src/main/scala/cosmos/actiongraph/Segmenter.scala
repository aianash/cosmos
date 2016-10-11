package cosmos.actiongraph

import akka.actor.{Actor, ActorLogging, Props}


class Segmenter extends Actor with ActorLogging {

  def receive = {
    case "Notify number of events for tokenId" =>
  }
}

object Segmenter {
  def props = Props(classOf[Segmenter])
}