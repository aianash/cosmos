package cosmos.actiongraph.dao

import org.mongodb.scala._

class ActionGraphDAO(db: MongoDatabase) {

  val graphcol = db.getCollection("graphs")
  val featurecol = db.getCollection("features")


}