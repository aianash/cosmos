package cosmos.actiongraph

import scala.concurrent.Future

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ActorSystem}

import scalaz._, Scalaz._
import scalaz.std.option._
import scalaz.syntax.monad._

import org.mongodb.scala._

import aianash.commons.events._

import cosmos.core.task._

import dao.ActionGraphDAO


case class UpdateActionGraph(pairs: Seq[(Action, Action)])

//
class ActionGraphProcessor(tokenId: TokenId, dao: ActionGraphDAO) extends Actor with ActorLogging {

  import context.dispatcher

  // private val settings = ActionGraphSettings(context.system)

  private val graph = ActionGraph.load(tokenId, dao)
  private var featurizer = new Featurizer(graph)
  private var isUpdating = false

  private var numReceived = 0

  //
  def receive = {
    //
    case UpdateActionGraph(pairs) =>
      pairs foreach { case (from, to) =>
        graph.add(from, to) || isUpdating || featurizer.add(from, to)
      }
      numReceived += 1
      if(numReceived % 1000 == 0)
        println(graph.toString)

      if(featurizer.shouldUpdate && !isUpdating) {
        println("UPDATING GRAPH")
        println(featurizer.graph.toString)

        isUpdating = true
        for(gupdates <- featurizer.materialize()) {
          graph.update(gupdates)
          featurizer = new Featurizer(graph)
          isUpdating = false
        }
      }
  }

  //
  def preRestart() {
  //   featurizer.persist()
  //   graph.persist()
  }

  //
  override def postStop() {
    // featurizer.persist()
    // graph.persist()
  }

}

//
object ActionGraphProcessor {

  //
  def props(tokenId: TokenId, dao: ActionGraphDAO) =
    Props(new ActionGraphProcessor(tokenId, dao))
    // Props(classOf[ActionGraphProcessor], tokenId, dao)
}