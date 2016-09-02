package cosmos.processing

import scala.concurrent.{Future, ExecutionContext}

import cosmos.core.task._

private[processing] sealed trait OpStatus
private[processing] case object OpCompleted extends OpStatus
private[processing] case object OpFailed extends OpStatus


/**
 *
 */
private[processing] sealed trait TaskOp[From, To] {
  def init(param: From): Unit
  def forward(implicit ec: ExecutionContext): Future[(Option[To], OpStatus)]
}

private[processing] class ChainedTaskOp[From, Middle, To](nested: TaskOp[From, Middle], op: Middle => Future[To]) extends TaskOp[From, To]  {

  private var param: Option[Middle] = None

  def init(from: From): Unit = {
    nested.init(from)
  }

  def forward(implicit ec: ExecutionContext) = param match {
    case Some(d) =>
      op(d) map { res =>
        (Some(res), OpCompleted)
      } recover {
        case ex: Exception => (None, OpFailed)
      }

    case None =>
      nested.forward map { case (res, status) =>
        param = res
        (None, status)
      }
  }

}

private[processing] class StandaloneTaskOp[From, To](op: From => Future[To]) extends TaskOp[From, To] {

  private var param: Option[From] = None

  def init(from: From): Unit = {
    param = Some(from)
  }

  def forward(implicit ec: ExecutionContext) = param match {
    case Some(p) =>
      op(p) map { x =>
        (Some(x), OpCompleted)
      } recover {
        case ex: Exception => (None, OpFailed)
      }

    case None => Future.successful(None -> OpCompleted)

  }

}

private[processing] object TaskOp {

  implicit class StandaloneTaskOpOps[From, Middle](op1: From => Future[Middle]) {
    def +>[To](op2: Middle => Future[To]) = new ChainedTaskOp[From, Middle, To](new StandaloneTaskOp(op1), op2)
  }

  implicit class ChainedTaskOpOps[From, Middle](nested: TaskOp[From, Middle]) {
    def +>[To](op: Middle => Future[To]) = new ChainedTaskOp(nested, op)
  }

}