package cosmos.actiongraph

import scala.collection.mutable.{Set => MSet}
import scala.collection.Set
import scala.collection.concurrent.TrieMap
import scala.util.Sorting

import scalaz._, Scalaz._
import scalaz.std.option._
import scalaz.syntax.monad._

import java.util.concurrent.atomic.AtomicLong

import play.api.libs.json._

import aianash.commons.events._

import dao.ActionGraphDAO


//
case class ActionGraph( tokenId: TokenId,
                        nodes: TrieMap[String, ActionGraph.Node],
                        edges: TrieMap[(String, String), ActionGraph.Edge]) {
  import ActionGraph._

  //
  def add(from: Action, to: Action) = {
    edges.get(from.name -> to.name) match {
      case Some(edge) => edge.incrementIfExist(from, to)
      case None => false
    }
  }

  //
  def containsNode(action: String) = nodes.contains(action)

  //
  def hasNodeFor(action: Action) =
    nodes.contains(action.name) && nodes(action.name).hasFeature(action)

  //
  def hasEdgeFor(from: Action, to: Action) =
    if(edges.contains(from.name -> to.name))
      (for {
        fromF <- nodes(from.name).feature(from)
        toF   <- nodes(to.name).feature(to)
      } yield edges(from.name -> to.name).hasTransition(fromF, toF)) getOrElse(false)
    else false

  //
  def update(gupdates: ActionGraph.Updates) {
    gupdates.nodes.foreach { newnode =>
      val gnode = nodes.getOrElseUpdate(newnode.name, Node.empty(newnode.name))
      for((feature, count) <- newnode.counts) {
        if(isNewFeature(feature))
          gnode.counts += stripNew(feature) -> count
        else gnode.counts(feature) addAndGet count.get
      }
      gnode.segments ++= newnode.segments
      gnode.props ++= newnode.props
    }

    gupdates.edges.foreach { newedge =>
      val from = nodes(newedge.from.name)
      val to = nodes(newedge.to.name)
      val gedge = edges.getOrElseUpdate(from.name -> to.name, Edge.empty(from, to))
      for(((ff, tf), count) <- newedge.transitions) {
        val _ff = if(isNewFeature(ff)) stripNew(ff) else ff
        val _tf = if(isNewFeature(tf)) stripNew(tf) else tf
        gedge.transitions += (_ff -> _tf) -> count
      }
    }
  }

  //
  private def isNewFeature(feature: Feature) =
    feature contains "__new__"

  private def stripNew(feature: Feature) =
    feature drop(8)

  //
  def cloneStructure() = {
    val _nNodes = nodes.foldLeft(TrieMap.empty[String, Node]) { (m, kv) =>
                    m += kv._1 -> kv._2.cloneStructure()
                  }
    val _nEdges = edges.foldLeft(TrieMap.empty[(String, String), Edge]) { case (m, (name, edge)) =>
                    m +=
                      (name ->
                        edge.cloneStructure(from = _nNodes(edge.from.name),
                                            to   = _nNodes(edge.to.name))
                      )
                  }

    this.copy(nodes = _nNodes, edges = _nEdges)
  }

  //
  override def toString() = {
    val strbuildr = StringBuilder.newBuilder
    strbuildr ++= """
Nodes
------------
    """
    for((_, node) <- nodes) {
      strbuildr ++= "\n\n"
      strbuildr ++= node.toString
    }

    strbuildr ++= """
Edges
--------
    """
    for((_, edge) <- edges) {
      strbuildr ++= "\n\n"
      strbuildr ++= edge.toString

    }
    strbuildr result
  }
}

//
object ActionGraph {

  type Feature = String

  //
  def empty(tokenId: TokenId) =
    ActionGraph(tokenId, TrieMap.empty[String, Node], TrieMap.empty[(String, String), Edge])

  //
  def load(tokenId: TokenId, dao: ActionGraphDAO) = empty(tokenId)


  //
  sealed trait NumericSegments {
    def segmentFor(value: JsNumber): Option[String]
  }

  //
  case class DoubleSegments(segments: Array[(Float, Float, String)]) extends NumericSegments {
    def segmentFor(value: JsNumber) =
      if(value.value.isDecimalDouble) {
        val v = value.value.toDouble
        segments.find(r => r._1 >= v && v <= r._2).map(_._3)
      } else None
  }

  //
  case class IntSegments(segments: Array[(Int, Int, String)]) extends NumericSegments {
    def segmentFor(value: JsNumber) =
      if(value.value.isValidInt) {
        val v = value.value.toInt
        segments.find(r => r._1 >= v && v <= r._2).map(_._3)
      } else None
  }

  //
  case class LongSegments(segments: Array[(Long, Long, String)]) extends NumericSegments {
    def segmentFor(value: JsNumber) =
      if(value.value.isValidLong) {
        val v = value.value.toLong
        segments.find(r => r._1 >= v && v <= r._2).map(_._3)
      } else None
  }

  //
  case class NewSegments() extends NumericSegments {
    private val data = TrieMap.empty[JsNumber, AtomicLong]
    def segmentFor(value: JsNumber) = {
      data.getOrElseUpdate(value, new AtomicLong) incrementAndGet()
      "__seg__".some
    }
  }

  //
  case class Node(
      name: String,
      counts: TrieMap[Feature, AtomicLong],
      props: MSet[String],
      segments: TrieMap[String, NumericSegments]) {

    private val newFeatures = MSet.empty[String]
    counts getOrElseUpdate("__*__", new AtomicLong)

    //
    def hasFeature(action: Action) =
      !feature(action).isEmpty

    //
    def feature(action: Action) =
      if(action.props.fields.isEmpty) "__*__".some
      else if(action.props.keys subsetOf props)
        getFeatureFromFields(action)
      else None

    def addProps(newprops: Set[String]) = props ++= newprops

    def addNewFeature(action: Action) = {
      val props = Sorting.stableSort(action.props.fields, (x: (String, JsValue)) => x._1)
      val featureBuildr = StringBuilder.newBuilder

      for((name, value) <- props) {
        value match {
          case JsBoolean(b) => featureBuildr ++= s":$b@$name"
          case JsString(s) => featureBuildr ++= s":$s@$name"
          case v: JsNumber =>
            val segment = NewSegments()
            segments += name -> segment
            featureBuildr ++= s":${segment.segmentFor(v)}@$name"
          case _ =>

        }
      }
      var feature = featureBuildr drop(1) result()
      newFeatures += feature
      feature = "__new__:" + feature
      counts += feature -> new AtomicLong
      feature
    }

    //
    private def getFeatureFromFields(action: Action): Option[Feature] = {
      val props = Sorting.stableSort(action.props.fields, (x: (String, JsValue)) => x._1)
      val featureBuildr = StringBuilder.newBuilder

      for((name, value) <- props) {
        value match {
          case JsBoolean(b) => featureBuildr ++= s":$b@$name"
          case JsString(s) => featureBuildr ++= s":$s@$name"
          case v: JsNumber =>
            segments.get(name).flatMap(_.segmentFor(v)) match {
              case Some(segment) => featureBuildr ++= s":$segment@$name"
              case None => return None
            }
          case _ =>
        }
      }
      val feature = featureBuildr.drop(1).result
      if(newFeatures contains feature)
        ("__new__:" + feature).some
      else feature.some
    }

    //
    def increment(feature: Feature) {
      if(feature equals "__*__")
        counts.getOrElseUpdate(feature, new AtomicLong) incrementAndGet()
      else
        counts.get(feature) foreach { _.incrementAndGet() }
    }

    //\
    def cloneStructure() =
      this.copy(counts =  counts.foldLeft(TrieMap.empty[Feature, AtomicLong]) { (m, kv) =>
                            m += kv._1 -> new AtomicLong
                          },
                props = props.clone())

    //
    override def toString() = {
      val strbuildr = StringBuilder.newBuilder
      strbuildr ++= s"[Node] [$name]"
      for((name, count) <- counts) {
        strbuildr ++= s"\n$count\t$name"
      }
      strbuildr result
    }
  }

  //
  object Node {
    //
    def empty(name: String) =
      Node(name     = name,
           counts   = TrieMap.empty[Feature, AtomicLong],
           props    = MSet.empty[String],
           segments = TrieMap.empty[String, NumericSegments])
  }

  //
  case class Edge(from: Node, to: Node, transitions: TrieMap[(Feature, Feature), AtomicLong]) {

    //
    def hasTransition(from: Feature, to: Feature) =
      transitions.contains(from -> to)

    //
    def incrementIfExist(fromA: Action, toA: Action) =
      (for {
        fromFeature <- from.feature(fromA)
        toFeature   <- to.feature(toA)
        count       <- transitions.get(fromFeature -> toFeature)
      } yield {
        count.incrementAndGet()
        from.increment(fromFeature)
        true
      }) getOrElse(false)

    //
    def addAndIncrement(fromFeature: Feature, toFeature: Feature) {
      from.increment(fromFeature)
      transitions.getOrElseUpdate(fromFeature -> toFeature, new AtomicLong) incrementAndGet()
    }

    //
    def cloneStructure(from: Node, to: Node) =
      this.copy(from        = from,
                to          = to,
                transitions = transitions.foldLeft(TrieMap.empty[(Feature, Feature), AtomicLong]) { (m, kv) =>
                  m += kv._1 -> new AtomicLong
                })

    //
    override def toString() = {
      val strbuildr = StringBuilder.newBuilder
      strbuildr ++= s"Edge(${from.name} -> ${to.name})"

      for(((from, to), count) <- transitions) {
        strbuildr ++= s"\n$count\t$from -> $to"
      }
      strbuildr result
    }
  }

  //
  object Edge {
    //
    def empty(from: Node, to: Node) =
      Edge(from        = from,
           to          = to,
           transitions = TrieMap.empty[(Feature, Feature), AtomicLong])
  }

  //
  case class Updates(nodes: Seq[Node], edges: Seq[Edge])
}
