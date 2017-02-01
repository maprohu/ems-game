package emsgame.testing

import java.io.File

import org.geotools.data.DataStoreFinder

import scala.collection.JavaConversions._

/**
  * Created by pappmar on 01/02/2017.
  */
object RunLandPolygons {

  def main(args: Array[String]): Unit = {
    val featureSource = load

    val collection =
      featureSource
        .getFeatures

    println(collection.size())
  }

  lazy val load = {
//    val file = new File("/dl/emsgame/land-polygons-complete-4326/land_polygons.shp")
    val file = new File("/dl/emsgame/water-polygons-split-4326/water_polygons.shp")

    val dataStore =
      DataStoreFinder
        .getDataStore(
          Map(
            "url" -> file.toURI.toURL
          )
        )

    val typeName =
      dataStore
        .getTypeNames
        .head

    dataStore
      .getFeatureSource(typeName)



  }

}
