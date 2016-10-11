package cosmos.actiongraph

import scala.concurrent.duration._
import scala.util.Random

import akka.actor.{Actor, ActorLogging, Props, ActorRef, ActorSystem}
import akka.routing.FromConfig
import akka.util.Timeout

import org.joda.time.{Duration, DateTime}

import play.api.libs.json._

import aianash.commons.events._

import aianonymous.commons.core.protocols._, Implicits._

case object SendRandomEvent

//
class Simulator extends Actor with ActorLogging {
  import context.dispatcher

  context.system.scheduler.schedule(10000 millis, 0 millis, self, SendRandomEvent)

  private val sampleEventSession =
    EventSession(
      tokenId = TokenId(1L),
      aianId = AianId(1L),
      sessionId = SessionId(1L),
      startTime = new DateTime,
      events = Seq.empty[TrackingEvent])

  private val website = WebPage(WebsiteId(1L), PageId(1L))

  private val homepageAction = Action(location = website, timeStamp = new DateTime, name = "", props = Json.obj())

  private val behavior1 =
    IndexedSeq(
      homepageAction.copy(
        name = "clicked-features",
        props = Json.obj(
          "feature" -> "aianash-behavior"
        )
      ),
      homepageAction.copy(
        name = "clicked-timeline",
        props = Json.obj(
          "type" -> "image"
        )
      ),
      homepageAction.copy(
        name = "clicked-onboard",
        props = Json.obj()
      ),
      homepageAction.copy(
        name = "subscribe",
        props = Json.obj(
          "type" -> "newsletter"
        )
      ),
      homepageAction.copy(
        name = "viewed-team",
        props = Json.obj()
      )
    )

  private val behavior2 =
    IndexedSeq(
      homepageAction.copy(
        name = "viewed-actionable-analytics",
        props = Json.obj(
          "feature" -> "behavior"
        )
      ),
      homepageAction.copy(
        name = "clicked-answers",
        props = Json.obj(
          "type" -> "image"
        )
      ),
      homepageAction.copy(
        name = "clicked-how-it-works",
        props = Json.obj()
      ),
      homepageAction.copy(
        name = "features",
        props = Json.obj(
          "type" -> "timeline"
        )
      ),
      homepageAction.copy(
        name = "viewed-team",
        props = Json.obj()
      ),
      homepageAction.copy(
        name = "subscribe",
        props = Json.obj(
          "type" -> "newsletter"
        )
      )
    )

  private val behaviors = Array(behavior1, behavior2)

  //
  def receive = {
    case SendRandomEvent =>
      context.parent ! NewEventSession(sampleEventSession.copy(events = sampleEvents()))
  }

  //
  private def sampleEvents() =
    behaviors(math.abs(Random.nextInt) % behaviors.size).toSeq

}


object Simulator {

  def props = Props(classOf[Simulator])

  def main(args: Array[String]) {

    val system = ActorSystem()
    system.actorOf(ActionGraphSupervisor.props, "supervisor")
  }
}