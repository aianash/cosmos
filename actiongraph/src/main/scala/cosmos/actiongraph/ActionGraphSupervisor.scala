package cosmos.actiongraph

import scala.collection.mutable.{HashMap => MHashMap}
import scala.collection.concurrent.TrieMap
import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, Props, ActorRef}
import akka.routing.FromConfig
import akka.util.Timeout

import org.mongodb.scala._

import aianash.commons.events._
import cosmos.core.task._

import aianonymous.commons.core.protocols._, Implicits._

import dao.ActionGraphDAO


sealed trait ActionGraphProtocols
case class NewEventSession(session: EventSession)


//
class ActionGraphSupervisor extends Actor with ActorLogging {
  import context.dispatcher

  // private val settings = ActionGraphSettings(context.system)

  private val mongoClient = MongoClient("mongodb://localhost")
  private val mongodb = mongoClient.getDatabase("cosmos")
  private val dao = new ActionGraphDAO(mongodb)

  // private val cassie = context.actorOf(FromConfig.props, "cassie")
  private val persistence = context.actorOf(Persistence.props, "persistence")

  private val simulator = context.actorOf(Simulator.props, "simulator")

  private val actiongraphs = new TrieMap[TokenId, ActorRef]

  def receive = {

    case NewEventSession(session) =>
      implicit val timeout = Timeout(1 seconds)
      val actiongraph = getOrCreateActionGraphProcessor(session.tokenId)
      for(pairs <- persistence ?= UpdateAndGetSessionPairs(session)) {
        if(!pairs.isEmpty) actiongraph ! UpdateActionGraph(pairs)
      }

  }

  override def postStop() {
    mongoClient.close()
  }

  private def getOrCreateActionGraphProcessor(tokenId: TokenId) =
    actiongraphs getOrElseUpdate(tokenId, {
      val actiongraph = context.actorOf(ActionGraphProcessor.props(tokenId, dao), s"action-graph-${tokenId.tkuuid}")
      context watch actiongraph
      actiongraph
    })

}

object ActionGraphSupervisor {
  def props = Props(classOf[ActionGraphSupervisor])
}