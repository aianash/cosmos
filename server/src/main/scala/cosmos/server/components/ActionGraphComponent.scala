package cosmos.server.components

import akka.actor.ActorSystem

import aianonymous.commons.microservice.Component

import cosmos.actiongraph.ActionGraphSupervisor


case object ActionGraphComponent extends Component {

  val name = "cosmos-action-graph"
  val runOnRole = "cosmos-action-graph"

  def start(system: ActorSystem) = system.actorOf(ActionGraphSupervisor.props, name)
}