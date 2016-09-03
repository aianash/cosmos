package cosmos.processing

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout

import aianonymous.commons.core.protocols._, Implicits._
import aianonymous.commons.events._

import cosmos.core.task._
import cosmos.preprocessing._


case class TrainingTask(
    val id         : TaskId,
    tokenId        : Long,
    pageId         : Long,
    startTime      : Long,
    endTime        : Long,
    system         : ActorSystem,
    cassie         : ActorRef,
    eventprocessor : ActorRef,
    modeltrainer   : ActorRef
  ) extends Task {

  import TaskOp._

  private val taskOps = (fetchEvents _) +> (processEvents _) +> (trainModel _) +> (presistResult _)
  taskOps.init(Unit)

  def next = taskOps.forward map {
    case (None, OpCompleted)    => TaskRemaining
    case (Some(_), OpCompleted) => TaskCompleted
    case (_, OpFailed)          => TaskFailed
  } recover {
    case ex: Exception => TaskFailed
  }

  def fetchEvents(a: Unit): Future[Seq[PageEvents]] = {
    implicit val timeout = Timeout(2 seconds)
    cassie ?= GetEvents(tokenId, pageId, startTime, endTime)
  }

  def processEvents(events: Seq[PageEvents]): Future[String] = {
    implicit val timeout = Timeout(2 seconds)
    eventprocessor ?= ProcessEvents(events)
  }

  def trainModel(inputfile: String): Future[String] = {
    implicit val timeout = Timeout(2 seconds)
    modeltrainer ?= TrainModel(inputfile)
  }

  def presistResult(outputfile: String): Future[Boolean] = {
    implicit val timeout = Timeout(2 seconds)
    cassie ?= PersistResult(outputfile)
  }

}
