package cosmos.processing

import scala.concurrent.duration._

import akka.actor.{ActorSystem, Extension, ExtensionId, ExtensionIdProvider, ExtendedActorSystem}

import com.typesafe.config.{Config, ConfigFactory}


class TrainingSettings(cfg: Config) extends Extension {

  private final val config: Config = {
    val config = cfg.withFallback(ConfigFactory.defaultReference)
    config.checkValid(ConfigFactory.defaultReference, "cosmos")
    config
  }

  private[processing] val CREATETASK_MIN_EVENT_COUNT = config.getInt("cosmos.processing.createtask.min-event-count")
  private[processing] val CREATETASK_INTERVAL = config.getLong("cosmos.processing.createtask.task-interval").milliseconds

  private[processing] val MAX_RUNNING_TASK = config.getInt("cosmos.processing.schedular.max-running-task")
  private[processing] val SERVICEID = config.getLong("cosmos.serviceid")
  private[processing] val DATACENTERID = config.getLong("cosmos.datacenterid")

}


object TrainingSettings extends ExtensionId[TrainingSettings] with ExtensionIdProvider {

  override def lookup = TrainingSettings

  override def createExtension(system: ExtendedActorSystem) =
    new TrainingSettings(system.settings.config)

  override def get(system: ActorSystem): TrainingSettings = super.get(system)

}