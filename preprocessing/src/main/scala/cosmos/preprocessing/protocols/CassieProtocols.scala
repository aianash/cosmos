package cosmos.preprocessing.protocols

import aianonymous.commons.core.protocols._, Implicits._
import aianonymous.commons.events.PageEvents

sealed trait CassieProtocols
case class GetEvents(tokenId: Long, pageId: Long, startTime: Long, endTime: Long) extends CassieProtocols with Replyable[Seq[PageEvents]]
case class GetEventsCount(tokenId: Long, pageId: Long, startTime: Long, endTime: Long) extends CassieProtocols with Replyable[Long]
case class PersistResult(outfile: String) extends CassieProtocols with Replyable[Boolean]