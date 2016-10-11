package cosmos.actiongraph

import scala.collection.mutable.{HashMap => MHashMap, Set => MSet}
import scala.util.Sorting
import scala.concurrent.{Future, ExecutionContext}
import scala.collection.concurrent.TrieMap

import akka.actor.{Actor, ActorLogging, Props}

import scalaz._, Scalaz._
import scalaz.std.option._
import scalaz.syntax.monad._

import play.api.libs.json._

import org.mongodb.scala._

import aianash.commons.events._

import dao.ActionGraphDAO

//
class Featurizer(_graph: ActionGraph) {
  import ActionGraph._

  val graph = _graph.cloneStructure()
  private var updateCount = 0

  //
  def materialize()(implicit ec: ExecutionContext) = {
    // [TODO] create segments
    Future(ActionGraph.Updates(graph.nodes.values.toSeq, graph.edges.values.toSeq))
  }

  //
  def add(from: Action, to: Action) = {
    val fromNode = graph.nodes.getOrElseUpdate(from.name, Node.empty(from.name))
    val toNode = graph.nodes.getOrElseUpdate(to.name, Node.empty(to.name))

    val fromFeature = getOrCreateFeature(fromNode, from)
    val toFeature = getOrCreateFeature(toNode, to)

    val edge = graph.edges.getOrElseUpdate(from.name -> to.name, Edge.empty(fromNode, toNode))
    edge.addAndIncrement(fromFeature, toFeature)

    updateCount += 1

    true
  }

  def shouldUpdate = updateCount != 0 && updateCount % 1000 == 0

  private def getOrCreateFeature(node: Node, action: Action) = {
    node.feature(action) match {
      case Some(feature) => feature
      case None =>
        node addProps action.props.keys
        node addNewFeature action
    }
  }
}