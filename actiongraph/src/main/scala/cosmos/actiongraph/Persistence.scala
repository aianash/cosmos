package cosmos.actiongraph

import scala.concurrent.Future

import akka.actor.{Actor, Props, ActorLogging}
import akka.pattern.pipe

import scalaz._, Scalaz._
import scalaz.std.option._
import scalaz.syntax.monad._

import aianash.commons.events._

import aianonymous.commons.core.protocols._, Implicits._


//
sealed trait PersistenceProtocol
case class UpdateAndGetSessionPairs(session: EventSession) extends PersistenceProtocol with Replyable[Seq[(Action, Action)]]


//
class Persistence extends Actor with ActorLogging {

  import context.dispatcher

  def receive = {
    //
    case UpdateAndGetSessionPairs(EventSession(tokenId, aianId, sessionId, startTime, events)) =>
      val actions =
        events.flatMap(_ match {
          case a: Action => a.some
          case _ => None
        })

      val pairsF =
        (for(latestO <- getAndUpdateLatest(tokenId, aianId, sessionId, actions.last))
          yield latestO match {
            case Some(latest) =>
              (latest +: actions).sliding(2)
            case None =>
              if(actions.length > 1) actions.sliding(2)
              else Seq.empty
          }) map {_.map(s => s(0) -> s(1)).toSeq}

      pairsF foreach { pairs => insertActionPairs(tokenId, aianId, sessionId, pairs) }
      pairsF pipeTo sender()
  }

  //
  private def getAndUpdateLatest(tokenId: TokenId, aianId: AianId, sessionId: SessionId, action: Action) = {
    Future.successful(action.some)
  }

  //
  private def insertActionPairs(tokenId: TokenId, aianId: AianId, sessionId: SessionId, pairs: Seq[(Action, Action)]) {}
}

//
object Persistence {
  def props = Props(classOf[Persistence])
}