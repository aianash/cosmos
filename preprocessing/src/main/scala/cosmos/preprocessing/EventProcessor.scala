package cosmos.preprocessing

import java.io.File

import akka.actor.{Actor, ActorLogging, Props}

import aianonymous.commons.core.protocols._, Implicits._
import aianonymous.commons.events.PageEvents

/**
 * Protocols to communicate with EventProcessor
 */
sealed trait EventProcessorProtocol
case class ProcessEvents(events: Seq[PageEvents]) extends EventProcessorProtocol with Replyable[String]

/**
 * EventProcessor actor processor the events and generates input
 * file for training
 */
class EventProcessor extends Actor with ActorLogging {

  def receive = {
    case ProcessEvents(events: Seq[PageEvents]) => sender() ! "/tmp/input.dat"

    case _ =>
  }

}