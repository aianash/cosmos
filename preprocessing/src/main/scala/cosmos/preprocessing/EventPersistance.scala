package cosmos.preprocessing

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.pipe

import aianonymous.commons.core.protocols._, Implicits._

import cassie.core.protocols.events._


class EventPersistance(cassie: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher
  import protocols._

  context watch cassie

  def receive = {

    case PersistResult(outfile) => sender() ! true

    case message: EventProtocol => cassie forward message

  }

}


object EventPersistance {

  def props(cassie: ActorRef) = Props(classOf[EventPersistance], cassie)

}