package cosmos.preprocessing.protocols

import aianonymous.commons.core.protocols._, Implicits._

sealed trait ModelTrainerProtocol
case class TrainModel(infile: String) extends ModelTrainerProtocol with Replyable[String]