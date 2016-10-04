package cosmos.service

import scala.concurrent.duration._
import scala.util.{Success, Failure}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import aianonymous.commons.core.protocols.Implicits._
import aianonymous.commons.core.services.{UUIDGenerator, NextId}

import cassie.core.protocols.events._

import cosmos.core.task._
import cosmos.service.protocols._


/** Traing task creator
  */
class TaskCreator(eventPersistent: ActorRef, eventProcessor: ActorRef,
  modelTrainer: ActorRef, schedular: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher
  import TaskCreator.CreateTask

  private val settings = TrainingSettings(context.system)

  // TODO: handle for all different pageid tasks
  private var lastUptoTime = System.currentTimeMillis
  private val tokenId = 123456L
  private val pageId = 2L

  private val uuid = context.actorOf(UUIDGenerator.props(settings.SERVICEID, settings.DATACENTERID))

  context watch eventPersistent
  context watch eventProcessor
  context watch modelTrainer
  context watch schedular
  context watch uuid

  context.system.scheduler.schedule(settings.CREATETASK_INTERVAL,
                                    settings.CREATETASK_INTERVAL,
                                    self,
                                    CreateTask)

  def receive = {

    case CreateTask =>
      var endTime = System.currentTimeMillis

      implicit val timeout = Timeout(3 seconds)
      val cntNTimeF =
      (eventPersistent ?= GetEventsCount(tokenId, pageId, lastUptoTime, endTime))
        .map(_ -> lastUptoTime)
        .andThen {
          case Success((count, startTime)) =>
          if(count >= settings.CREATETASK_MIN_EVENT_COUNT) lastUptoTime = endTime
          else log.info("Event count criteria not fulfilled for task creation of interval ({}, {}). Count is {}",
              startTime, endTime, count)

          case Failure(ex) =>
            log.warning("Failed to get event count of interval ({}, {}) for task creation", lastUptoTime, endTime)
            lastUptoTime = endTime;
        }

      for {
        (count, startTime) <- cntNTimeF
        uuidO <- (uuid ?= NextId("trainingtaskid")) if count >= settings.CREATETASK_MIN_EVENT_COUNT
      } {
        uuidO match {
          case Some(uuid) =>
            val task = TrainingTask(TaskId(uuid), tokenId, pageId, startTime, endTime,
                                    context.system, eventPersistent, eventProcessor, modelTrainer)
            schedular ! NewTask(task)
            log.info("Task with taskid {} created for interval ({}, {})", uuid, startTime, endTime)

          case None =>
            log.warning("Failed to get taskid for task creation of interval ({}, {})", startTime, endTime)
        }
      }
  }

}


object TaskCreator {

  def props(eventPersistance: ActorRef, eventProcessor: ActorRef, modelTrainer: ActorRef, schedular: ActorRef) =
    Props(classOf[TaskCreator], eventPersistance, eventProcessor, modelTrainer, schedular)

  case object CreateTask

}