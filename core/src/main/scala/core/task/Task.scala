package cosmos.core.task

import scala.concurrent.Future

case class TaskId(uuid: Long)

sealed trait TaskStatus
case object TaskCompleted extends TaskStatus
case object TaskFailed extends TaskStatus

trait Task {
  val id: TaskId
  def hasNext: Boolean
  def next: Future[TaskStatus]
}