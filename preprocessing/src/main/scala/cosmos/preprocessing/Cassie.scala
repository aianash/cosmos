package cosmos.preprocessing

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import akka.routing.FromConfig
import akka.util.Timeout

import aianonymous.commons.core.protocols._, Implicits._

import cassie.events.EventService


class Cassie extends Actor with ActorLogging {

  import context.dispatcher
  import protocols._

  val eventservice = context.actorOf(FromConfig.props(), name = "event-service")

  def receive = {
    case GetEvents(tokenId, pageId, startTime, endTime) =>
      implicit val timeout = Timeout(2 seconds)
      (eventservice ?= GetEvents(tokenId, pageId, startTime, endTime)) pipeTo sender()

    case GetEventsCount(tokenId, pageId, startTime, endTime) =>
      implicit val timeout = Timeout(2 seconds)
      (eventservice ?= GetEventsCount(tokenId, pageId, startTime, endTime)) pipeTo sender()

    case PersistResult(outfile) => sender() ! true

    case _ =>
  }

}

object Cassie {

  def props = Props(classOf[Cassie])

}