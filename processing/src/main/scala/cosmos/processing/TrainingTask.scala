package cosmos.processing

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorRef

import cosmos.core.task._


class TrainingTask(val id: TaskId, cassie: ActorRef, preprocessor: ActorRef, modeltrainer: ActorRef) extends Task {

  import TaskOp._

  private val taskOps = (task1 _) +> (task2 _)
  taskOps.init("My name is neeraj")

  private var isCompleted = false

  def next = taskOps.forward map {
    case (None, OpCompleted)    => TaskRemaining
    case (Some(_), OpCompleted) => TaskCompleted
    case (_, OpFailed)          => TaskFailed
  } recover {
    case ex: Exception => TaskFailed
  }

  def task1(a: String) = Future.successful {
    a.length
  }

  def task2(b: Int) = Future.successful {
    b == 0
  }

}


