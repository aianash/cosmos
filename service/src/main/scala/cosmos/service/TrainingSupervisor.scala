package cosmos.service

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.FromConfig

import cosmos.preprocessing._

/** Supervisor for traing requests
  */
class TrainingSupervisor extends Actor with ActorLogging {

  import context.dispatcher

  private val settings = TrainingSettings(context.system)

  private val cassie = context.actorOf(FromConfig.props, "cassie")

  private val eventPersistent = context.actorOf(EventPersistance.props(cassie), "eventpersistent")
  private val eventProcessor = context.actorOf(FromConfig.props(EventProcessor.props), "eventprocessor")
  private val modelTrainer = context.actorOf(FromConfig.props(ModelTrainer.props), "modeltrainer")

  private val schedular = context.actorOf(TrainingSchedular.props, "schedular")
  private val taskCreator = context.actorOf(TaskCreator.props(eventPersistent, eventProcessor, modelTrainer, schedular), "taskCreator")

  context watch cassie
  context watch eventPersistent
  context watch eventProcessor
  context watch modelTrainer
  context watch schedular
  context watch taskCreator

  def receive = {

    /* Message for status of a task from schedular */
    case (t: TrainingTask, ts: String) =>
      ts match {
        case "Completed" =>
          log.info("task for tokenid {} and pageid {} with taskid {} {}", t.tokenId, t.pageId, t.id.uuid, ts)
        case "Failed" =>
          log.warning("task for tokenid {} and pageid {} with taskid {} {}", t.tokenId, t.pageId, t.id.uuid, ts)
        case _ =>
          log.warning("task status for tokenid {} and pageid {} with taskid {} is unknown", t.tokenId, t.pageId, t.id.uuid)
      }

  }

}


object TrainingSupervisor {

  def props = Props(classOf[TrainingSupervisor])

}

