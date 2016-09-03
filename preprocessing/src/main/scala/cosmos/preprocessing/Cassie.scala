package cosmos.preprocessing

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import akka.routing.FromConfig
import akka.util.Timeout

import aianonymous.commons.core.protocols._, Implicits._
import aianonymous.commons.events.PageEvents

import cassie.events.EventService

/**
 * Protocols to communicate with Cassie
 */
sealed trait CassieProtocols
case class GetEvents(tokenId: Long, pageId: Long, startTime: Long, endTime: Long) extends CassieProtocols with Replyable[Seq[PageEvents]]
case class GetEventsCount(tokenId: Long, pageId: Long, startTime: Long, endTime: Long) extends CassieProtocols with Replyable[Long]
case class PersistResult(outfile: String) extends CassieProtocols with Replyable[Boolean]
/**
 * Cassie actor which uses EventService to fetch the events
 */
class Cassie extends Actor with ActorLogging {

  import context.dispatcher

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

  final val name = "cassie"

  def props = Props(classOf[Cassie])

}