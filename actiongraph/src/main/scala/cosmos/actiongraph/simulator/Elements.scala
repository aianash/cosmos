package cosmos.actiongraph.simulator

import scala.collection.mutable.{HashMap => MHashMap}
import play.api.libs.json._

//
trait Website {

  val pages = MHashMap.empty[String, Webpage]
  val sections = MHashMap.empty[String, Section]

  //
  def page(name: String, props: Props, sections: Section*) = {
    val page = Webpage(name, props, sections.toIndexedSeq)
    pages += name -> page
    page
  }

  //
  def props(props: Prop*) = Props(props.toSet)

  //
  def noprops: Props = Props(Set.empty)

  //
  def noaction: Action = NoAction

  //
  def section(name: String, props: Props, action: Action, elements: Element*) = {
    val section = Section(name, props, action, elements.toIndexedSeq)
    sections += name -> section
    section
  }

  //
  def element(name: String, action: Action, props: Props) =
    Element(name, props, action)

  //
  def singlechoices(choices: String*) =
    SingleChoice(choices.toSet)

  //
  def multichoices(choices: String*) =
    MultiChoices(choices.toSet)

  //
  def rangechoices(from: Int, to: Int) =
    RangeChoices(from, to)

  //
  implicit def pairCh2Props(pair: (String, Choices)) =
    Prop(pair._1, pair._2)

  //
  implicit def strPair2Props(pair: (String, String)) =
    Prop(pair._1, StaticChoice(pair._2))

  //
  implicit def pair2Props(pair: (String, AnyVal)) =
    pair._2 match {
      case i: Int    => Prop(pair._1, StaticChoice(i))
      case f: Float  => Prop(pair._1, StaticChoice(f))
      case d: Double => Prop(pair._1, StaticChoice(d))
      case l: Long   => Prop(pair._1, StaticChoice(l))
      case c: Char   => Prop(pair._1, StaticChoice(c))
      case s: Short  => Prop(pair._1, StaticChoice(s))
      case b: Byte   => Prop(pair._1, StaticChoice(b))
    }
}

//
case class Webpage(name: String, props: Props, sections: IndexedSeq[Section])

//
case class Section(name: String, props: Props, action: Action, elements: IndexedSeq[Element])

//
sealed trait Action
case class GoToPage(webpage: String, params: Props = Props(Set.empty)) extends Action
case class GoToSection(section: String) extends Action
case class Click(props: Props) extends Action
case class AddToCart() extends Action
case class Submit(props: Props) extends Action
case object NoAction extends Action

//
case class Element(name: String, props: Props, action: Action)

//
case class NavGraph() {}

//
object NavGraph {}

//
sealed trait Choices {
  type T
  def sample: T
}

//
case class RangeChoices(from: Int, to: Int) extends Choices {
  type T = Int
  def sample = from
}

//
case class MultiChoices(choices: Set[String]) extends Choices {
  type T = Set[String]
  def sample = choices
}

//
case class SingleChoice(choices: Set[String]) extends Choices {
  type T = String
  def stateful() = this
  def sample = choices.head
}

//
case class StaticChoice[V](value: V) extends Choices {
  type T = V
  def sample = value
}

//
case class Props(props: Set[Prop])
case class Prop(name: String, choices: Choices)