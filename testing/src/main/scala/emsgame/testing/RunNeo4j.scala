package emsgame.testing

import java.io.File

import org.neo4j.gis.spatial.ShapefileImporter
import org.neo4j.graphdb.factory.GraphDatabaseFactory

/**
  * Created by pappmar on 06/02/2017.
  */
object RunNeo4j {

  lazy val db = new GraphDatabaseFactory().newEmbeddedDatabase(
    new File("../ems-game/local/neo4j")
  )
  val LandLayerName = "land"

  def main(args: Array[String]): Unit = {

    val importer = new ShapefileImporter(db)
    importer.importFile(
      "../ems-game/local/land-polygons-complete-4326/land_polygons.shp",
      LandLayerName
    )

    db.shutdown()

  }

}

