package cosmos.preprocessing

import java.io.File

import akka.actor.{Actor, ActorLogging, Props}

import aianonymous.commons.core.protocols._, Implicits._
// import aianonymous.commons.events.PageEvents


class EventProcessor extends Actor with ActorLogging {

  import protocols._

  def receive = {

    // case ProcessEvents(events: Seq[PageEvents]) => sender() ! "/tmp/input.dat"

    case _ =>

  }

}


object EventProcessor {

  def props = Props(classOf[EventProcessor])

}