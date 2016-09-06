package cosmos.processing

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.FromConfig

import cosmos.preprocessing._
import cosmos.core.task._

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
    case (t: Task, ts: String) =>
      log.info("task with taskid {} {}", t.id.uuid, ts)

  }

}


object TrainingSupervisor {

  def props = Props(classOf[TrainingSupervisor])

}

