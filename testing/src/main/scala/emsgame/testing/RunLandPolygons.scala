package emsgame.testing

import java.io.File

import com.vividsolutions.jts.geom.{GeometryCollection, MultiPolygon, Polygon}
import org.geotools.data.DataStoreFinder
import org.geotools.data.simple.{SimpleFeatureCollection, SimpleFeatureIterator, SimpleFeatureSource}
import org.opengis.feature.simple.SimpleFeature

import scala.collection.JavaConversions._

/**
  * Created by pappmar on 01/02/2017.
  */
object RunLandPolygons {

  def main(args: Array[String]): Unit = {
    val featureSource = GeoData.land

    val collection =
      featureSource
        .getFeatures

    println(collection.size())
  }


}

object GeoData {


  def loadFromFile(file : File) = {

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


  // (LON, LAT)
  // lon: -180 -> 180
  // lat: -90 -> 90

  lazy val land = {
    loadFromFile(
      new File("local/land-polygons-complete-4326/land_polygons.shp")
    )
  }

  lazy val water = {
    loadFromFile(
      new File("local/water-polygons-split-4326/water_polygons.shp")
    )
  }

  def iterator(it: SimpleFeatureIterator) = {
    new Iterator[SimpleFeature] {
      override def hasNext: Boolean = it.hasNext
      override def next(): SimpleFeature = it.next()
    }
  }

  object Implicits {
    implicit class IteratorOps(it: SimpleFeatureIterator) {
      def asScala = GeoData.iterator(it)
    }

    implicit class MultiPolygonOps(c: MultiPolygon) {
      def polygons = {
        (0 until c.getNumGeometries)
          .map({ idx => c.getGeometryN(idx).asInstanceOf[Polygon]})
      }
    }

    implicit class PolygonOps(c: Polygon) {
      def interiors = {
        (0 until c.getNumInteriorRing)
          .map({ idx => c.getInteriorRingN(idx)})
      }
    }

  }

}


