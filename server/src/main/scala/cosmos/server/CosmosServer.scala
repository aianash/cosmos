package cosmos.server

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem

import aianonymous.commons.microservice.Microservice


object CosmosServer {

  import components._

  def main(args: Array[String]) {

    val config = ConfigFactory.load("cosmos")
    val system = ActorSystem(config.getString("cosmos.actorSystem"), config)
    Microservice(system).start(IndexedSeq(TrainingComponent))

  }

}