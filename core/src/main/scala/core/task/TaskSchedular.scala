package cosmos.core.task

import scala.collection.JavaConversions._

import java.util.concurrent.{ConcurrentLinkedQueue, ConcurrentHashMap}

import akka.actor.{Actor, ActorLogging, Props}

import aianonymous.commons.core.protocols._, Implicits._

private[cosmos] sealed trait TrainingSchedulerMessages
private[cosmos] case class NewTask(task: Task) extends TrainingSchedulerMessages with Replyable[Boolean]


/** Maintain training jobs
  */
private[cosmos] class TaskScheduler(maxTaskRuns: Int) extends Actor with ActorLogging {

  import context.dispatcher

  private val supervisor = context.parent

  private val taskQueue = new ConcurrentLinkedQueue[Task]
  private val runMap = new ConcurrentHashMap[Long, Task]


  def receive = {

    case NewTask(task: Task) =>
      taskQueue.add(task)
      if(runMap.size < maxTaskRuns) execute()

  }

  private def execute() {
    if(!taskQueue.isEmpty) {
      val task = taskQueue.poll
      runMap.put(task.id.uuid, task)
      task.next foreach {
        case TaskRemaining =>
          runMap.remove(task.id.uuid)
          taskQueue.add(task)
          execute()
        case TaskCompleted =>
          runMap.remove(task.id.uuid)
          supervisor ! (task, "Completed")
          execute()
        case TaskFailed =>
          runMap.remove(task.id.uuid)
          supervisor ! (task, "Failed")
          execute()
      }
    }
  }

}


private[cosmos] object TaskScheduler {

  def props(maxTaskRuns: Int) = Props(classOf[TaskScheduler], maxTaskRuns)

}