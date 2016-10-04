package cosmos.core.task

import scala.concurrent.Future

private[cosmos] case class TaskId(uuid: Long)

private[cosmos] sealed trait TaskStatus
private[cosmos] case object TaskCompleted extends TaskStatus
private[cosmos] case object TaskRemaining extends TaskStatus
private[cosmos] case object TaskFailed extends TaskStatus

private[cosmos] trait Task {
  val id: TaskId
  def next: Future[TaskStatus]
}
