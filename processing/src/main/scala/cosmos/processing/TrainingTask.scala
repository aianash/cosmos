package cosmos.processing

import scala.concurrent.Future

import akka.actor.ActorRef

import cosmos.core.task._

class TrainingTask(val id: TaskId, cassieclient: ActorRef, preprocessor: ActorRef, modeltrainer: ActorRef) extends Task {

  var i = 1

  def hasNext =
    if(i < 5) {
      i = i + 1
      true
    } else false

  def next = Future.successful(TaskCompleted)

}