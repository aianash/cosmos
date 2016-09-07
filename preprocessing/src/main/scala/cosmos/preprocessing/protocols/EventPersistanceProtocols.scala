package cosmos.preprocessing.protocols

import aianonymous.commons.core.protocols._, Implicits._

sealed trait EventPersistanceProtocols
case class PersistResult(outfile: String) extends EventPersistanceProtocols with Replyable[Boolean]