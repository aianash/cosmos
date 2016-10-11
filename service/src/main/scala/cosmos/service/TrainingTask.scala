package cosmos.service

import scala.concurrent.duration._
import scala.concurrent.Future

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout

import aianonymous.commons.core.protocols._, Implicits._
import aianash.commons.events._

import cassie.core.protocols.events._

import cosmos.core.task._
import cosmos.preprocessing.protocols._


case class TrainingTask(
    val id          : TaskId,
    tokenId         : Long,
    pageId          : Long,
    startTime       : Long,
    endTime         : Long,
    system          : ActorSystem,
    eventPersistent : ActorRef,
    eventprocessor  : ActorRef,
    modeltrainer    : ActorRef
  ) extends Task {

  type From = Unit
  type To = Boolean

  import TaskOp._
  import system.dispatcher

  val taskOps = (todo1 _) +> (todo _)
  taskOps.init(Unit)

  def todo1(a: Unit) =
    Future(true)

  def todo(a: Boolean) =
    Future(true)

  // val taskOps = (fetchEvents _) +> (processEvents _) +> (trainModel _) +> (presistResult _)
  // taskOps.init(Unit)

  // def fetchEvents(a: Unit): Future[Seq[PageEvents]] = {
  //   implicit val timeout = Timeout(2 seconds)
  //   eventPersistent ?= GetEvents(tokenId, pageId, startTime, endTime)
  // }

  // def processEvents(events: Seq[PageEvents]): Future[String] = {
  //   implicit val timeout = Timeout(2 seconds)
  //   eventprocessor ?= ProcessEvents(events)
  // }

  // def trainModel(inputfile: String): Future[String] = {
  //   implicit val timeout = Timeout(2 seconds)
  //   modeltrainer ?= TrainModel(inputfile)
  // }

  // def presistResult(outputfile: String): Future[Boolean] = {
  //   implicit val timeout = Timeout(2 seconds)
  //   eventPersistent ?= PersistResult(outputfile)
  // }

}
