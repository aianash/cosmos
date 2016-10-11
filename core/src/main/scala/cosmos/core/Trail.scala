// package cosmos.core


// // select(action).where(prop eqs propVal)
// // includes(action).with(prop eqs propVal)
// // includes(action).with(prop eqs propVal)
// // fork.at(action)
// //
// // {
// //  select: <action name>
// //  where: [[prop-name, prop-val-type, operator, prop-vals]],
// //  includes: {
// //    <action-name>: [],
// //    <action-name>: [],
// //    ...
// //  },
// //  fork: <action-name>
// // }
// //
// // {
// //  isfork:
// //  timeline: [
// //    {
// //      name: <action-name>
// //      props: { <prop-name>: <values> },
// //      divergedFrom: <action-name>,
// //      new: Long
// //      drop: Long
// //      total: Long
// //    }
// //  ],
// //  timeseries: []
// // }


// trait EPValue
// case class EPBoolean(value: Boolean) extends EPValue
// case class EPChar(value: Char) extends EPValue
// case class EPString(value: String) extends EPValue
// case class EPInt(value: Int) extends EPValue
// case class EPLong(value: Long) extends EPValue
// case class EPDouble(value: Double) extends EPValue

// trait PropertyClause
// case class Eq[V <: EPValue](property: String, value: V) extends PropertyClause
// case class Lt[V <: EPValue](property: String, value: V) extends PropertyClause
// case class Lte[V <: EPValue](property: String, value: V) extends PropertyClause
// case class Gt[V <: EPValue](property: String, value: V) extends PropertyClause
// case class Gte[V <: EPValue](property: String, value: V) extends PropertyClause
// case class In[V <: EPValue](property: String, values: Seq[V]) extends PropertyClause
// case class Bt[V <: EPValue](property: String, from: V, to: V, fromIncl: Boolean, toIncl: Boolean) extends PropertyClause


// trait ActionClause
// case class Select(action: String, where: Seq[PropertyClause])

// case class Segment()
// case class Formula()

// case class Segemented(segments: Seq[Segment])
// case class Categorical(categories: Seq[String])
// case class Formulaic(formula: Formula)

// /**
//  * Graph as nodes and each node has properties
//  * each property has values that are either
//  * Segmented (eg. in case of conitnuous data like numbers)
//  * Categorical (specified with exact categorical values in string)
//  * Formulae (ie there is a formula to provide probability with target node prop)
//  * takes with value directly
//  */

// trait Node {
//   def action: String
//   def prob(property: String, value: EPValue)
//   def prob(clause: PropertyClause)
// }

// trait Edge {
//   def from: Node
//   def to: Node
//   def transistion(name: String, name: String)
// }

// case class Graph(nodes: Set[Nodes], edges: Set[Edge]) {
//   def select(clause: ActionClause): OperableGraph
// }

// // case class OperableGraph(nodes: Set[Nodes], edges: Set[Edge], target: Node) {

// // }