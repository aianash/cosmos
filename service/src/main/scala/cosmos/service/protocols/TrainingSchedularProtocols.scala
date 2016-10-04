package cosmos.service.protocols

import aianonymous.commons.core.protocols._

import cosmos.core.task._

sealed trait TrainingSchedulerMessages
case class NewTask(task: Task) extends TrainingSchedulerMessages with Replyable[Boolean]
