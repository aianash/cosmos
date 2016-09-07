package cosmos.processing

import scala.concurrent.duration._
import scala.util.{Success, Failure}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import aianonymous.commons.core.protocols.Implicits._
import aianonymous.commons.core.services.{UUIDGenerator, NextId}

import cassie.core.protocols.events._

import cosmos.core.task._
import cosmos.processing.protocols._


/** Traing task creator
  */
class TaskCreator(eventPersistent: ActorRef, eventProcessor: ActorRef,
  modelTrainer: ActorRef, schedular: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher
  import TaskCreator.CreateTask

  private val settings = TrainingSettings(context.system)

  // TODO: handle for all different pageid tasks
  private var startTime = 0L
  private var endTime = 0L
  private var retainStTime = false
  private val tokenId = 123456L
  private val pageId = 2L

  private val uuid = context.actorOf(UUIDGenerator.props(settings.SERVICEID, settings.DATACENTERID))

  context watch eventPersistent
  context watch eventProcessor
  context watch modelTrainer
  context watch schedular
  context watch uuid

  context.system.scheduler.schedule(settings.CREATETASK_START_DELAY,
                                    settings.CREATETASK_INTERVAL,
                                    self,
                                    CreateTask)

  def receive = {

    case CreateTask =>
      if(retainStTime) {
        retainStTime = false
        endTime = System.currentTimeMillis
      } else {
        startTime = endTime
        endTime = System.currentTimeMillis
      }

      implicit val timeout = Timeout(3 seconds)
      (eventPersistent ?= GetEventsCount(tokenId, pageId, startTime, endTime)) andThen {
        case Success(count) =>
          if(count >= settings.CREATETASK_MIN_EVENT_COUNT) {
            (uuid ?= NextId("trainingtaskid")) foreach {
              case Some(id) =>
                val task = new TrainingTask(TaskId(id), tokenId, pageId, startTime, endTime,
                context.system, eventPersistent, eventProcessor, modelTrainer)
                schedular ! NewTask(task)
                log.info("Task with taskid {} created for interval ({}, {})", id, startTime, endTime)

              case None =>
                log.warning("Failed to get task id for task creation of interval ({}, {})", startTime, endTime)
            }
          } else {
            log.info("Event count criteria not fulfilled for task creation of interval ({}, {})", startTime, endTime)
            retainStTime = true
          }

        case Failure(count) =>
          log.warning("Failed to get event count for task creation of interval ({}, {})", startTime, endTime)
      }
  }

}


object TaskCreator {

  def props(eventPersistance: ActorRef, eventProcessor: ActorRef, modelTrainer: ActorRef, schedular: ActorRef) =
    Props(classOf[TaskCreator], eventPersistance, eventProcessor, modelTrainer, schedular)

  case object CreateTask

}

