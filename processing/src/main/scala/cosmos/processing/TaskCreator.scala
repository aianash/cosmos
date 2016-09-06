package cosmos.processing

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import aianonymous.commons.core.protocols.Implicits._
import aianonymous.commons.core.services.{UUIDGenerator, NextId}

import cassie.core.protocols.events._

import cosmos.core.task._
import cosmos.processing.protocols._


/** Traing job creator
  */
class TaskCreator(eventPersistent: ActorRef, eventProcessor: ActorRef,
  modelTrainer: ActorRef, schedular: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher
  import TaskCreator.CreateJob

  private val settings = TrainingSettings(context.system)

  // TODO: handle for all different pageid jobs
  private var startTime = 0L
  private var endTime = 0L
  private val tokenId = 123456L
  private val pageId = 2L

  private implicit val timeout = Timeout(3 seconds)
  private val uuid = context.actorOf(UUIDGenerator.props(settings.SERVICEID, settings.DATACENTERID))

  context watch eventPersistent
  context watch eventProcessor
  context watch modelTrainer
  context watch schedular
  context watch uuid

  context.system.scheduler.schedule(settings.CREATETASK_START_DELAY,
                                    settings.CREATETASK_INTERVAL,
                                    self,
                                    CreateJob)

  def receive = {

    case CreateJob =>
      startTime = endTime
      endTime = System.currentTimeMillis

      (eventPersistent ?= GetEventsCount(tokenId, pageId, startTime, endTime)) map { count =>
        if(count > settings.CREATETASK_MIN_EVENT_COUNT) {
          (uuid ?= NextId("trainingtaskid")) map {
            case Some(id) =>
              val task = new TrainingTask(TaskId(id), tokenId, pageId, startTime, endTime,
               context.system, eventPersistent, eventProcessor, modelTrainer)
              schedular ! NewTask(task)
            case None =>
          }
        }
      }
  }

}


object TaskCreator {

  def props(eventPersistance: ActorRef, eventProcessor: ActorRef, modelTrainer: ActorRef, schedular: ActorRef) =
    Props(classOf[TaskCreator], eventPersistance, eventProcessor, modelTrainer, schedular)

  case object CreateJob

}

