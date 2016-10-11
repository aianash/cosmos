package cosmos.core.task

import scala.concurrent.{ExecutionContext, Future}

import akka.actor.{ActorSystem}

private[cosmos] case class TaskId(uuid: Long)

private[cosmos] sealed trait TaskStatus
private[cosmos] case object TaskCompleted extends TaskStatus
private[cosmos] case object TaskRemaining extends TaskStatus
private[cosmos] case object TaskFailed extends TaskStatus

private[cosmos] trait Task {
  type From
  type To
  val id: TaskId
  def taskOps: TaskOp[From, To]

  def next(implicit ex: ExecutionContext): Future[TaskStatus] = taskOps.forward map {
    case (None, OpCompleted)    => TaskRemaining
    case (Some(_), OpCompleted) => TaskCompleted
    case (_, OpFailed)          => TaskFailed
  } recover {
    case ex: Exception => TaskFailed
  }
}
