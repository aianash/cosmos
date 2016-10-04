package cosmos.core.task

import scala.concurrent.{Future, ExecutionContext}

import cosmos.core.task._

private[cosmos] sealed trait OpStatus
private[cosmos] case object OpCompleted extends OpStatus
private[cosmos] case object OpFailed extends OpStatus


/**
 * To chain functions of form
 * A => Future[B], B => Future[C], C => Future[D] etc
 *
 * It chains the functions and executes one function from left
 * when .forward method is called and returns the future.
 *
 * Usage
 * -----
 * def op1(a: Int): Future[String]
 * def op2(b: String): Future[Boolean]
 *
 * val taskops = (op1 _) +> (op2 _)
 * taskops.init(1)
 */
private[cosmos] sealed trait TaskOp[From, To] {
  def init(param: From): Unit
  def forward(implicit ec: ExecutionContext): Future[(Option[To], OpStatus)]
}

private[cosmos] class ChainedTaskOp[From, Middle, To](nested: TaskOp[From, Middle], op: Middle => Future[To]) extends TaskOp[From, To]  {

  private var param: Option[Middle] = None

  def init(from: From): Unit = {
    nested.init(from)
  }

  def forward(implicit ec: ExecutionContext) = param match {
    case Some(d) =>
      op(d) map { res =>
        Some(res) -> OpCompleted
      } recover {
        case ex: Exception => None -> OpFailed
      }

    case None =>
      nested.forward map { case (res, status) =>
        param = res
        None -> status
      }
  }

}

private[cosmos] class StandaloneTaskOp[From, To](op: From => Future[To]) extends TaskOp[From, To] {

  private var param: Option[From] = None

  def init(from: From): Unit = {
    param = Some(from)
  }

  def forward(implicit ec: ExecutionContext) = param match {
    case Some(p) =>
      op(p) map { x =>
        Some(x) -> OpCompleted
      } recover {
        case ex: Exception => None -> OpFailed
      }

    case None =>
      Future.successful(None -> OpCompleted)
  }

}

private[cosmos] object TaskOp {

  implicit class StandaloneTaskOpOps[From, Middle](op1: From => Future[Middle]) {
    def +>[To](op2: Middle => Future[To]) = new ChainedTaskOp[From, Middle, To](new StandaloneTaskOp(op1), op2)
  }

  implicit class ChainedTaskOpOps[From, Middle](nested: TaskOp[From, Middle]) {
    def +>[To](op: Middle => Future[To]) = new ChainedTaskOp(nested, op)
  }

}