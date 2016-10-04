package cosmos.core.task

import scala.collection.JavaConversions._

import java.util.concurrent.{ConcurrentLinkedQueue, ConcurrentHashMap}

import akka.actor.{Actor, ActorLogging, Props}

import cosmos.core.task._


/** Maintain training jobs
  */
class TaskScheduler extends Actor with ActorLogging {

  import context.dispatcher
  import protocols._


  private val settings = TrainingSettings(context.system)

  private val supervisor = context.parent

  private val taskQueue = new ConcurrentLinkedQueue[Task]
  private val runMap = new ConcurrentHashMap[Long, Task]

  private val maxTaskRuns = settings.MAX_RUNNING_TASK


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


object TaskScheduler {

  def props = Props(classOf[TaskScheduler])

}