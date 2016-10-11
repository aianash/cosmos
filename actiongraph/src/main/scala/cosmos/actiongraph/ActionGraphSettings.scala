package cosmos.actiongraph

import scala.concurrent.duration._

import akka.actor.{ActorSystem, Extension, ExtensionId, ExtensionIdProvider, ExtendedActorSystem}

import com.typesafe.config.{Config, ConfigFactory}


class ActionGraphSettings(cfg: Config) extends Extension {

  private final val config: Config = {
    val config = cfg.withFallback(ConfigFactory.defaultReference)
    config.checkValid(ConfigFactory.defaultReference, "cosmos")
    config
  }

  val MAX_RUNNING_TASK = config.getInt("cosmos.actiongraph.schedular.max-running-task")
  val REFEATURIZE_FREQ = config.getInt("cosmos.actiongraph.refeaturize-freq")
  // private val SERVICEID = config.getLong("cosmos.actiongraphid")
  // private val DATACENTERID = config.getLong("cosmos.datacenterid")

}


object ActionGraphSettings extends ExtensionId[ActionGraphSettings] with ExtensionIdProvider {

  override def lookup = ActionGraphSettings

  override def createExtension(system: ExtendedActorSystem) =
    new ActionGraphSettings(system.settings.config)

  override def get(system: ActorSystem): ActionGraphSettings = super.get(system)

}