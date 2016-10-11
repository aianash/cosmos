package cosmos.server.components

import akka.actor.ActorSystem

import aianonymous.commons.microservice.Component

import cosmos.service.TrainingSupervisor


case object TrainingComponent extends Component {

  val name = "cosmos-service"
  val runOnRole = "cosmos-service"

  def start(system: ActorSystem) = system.actorOf(TrainingSupervisor.props, name)

}
