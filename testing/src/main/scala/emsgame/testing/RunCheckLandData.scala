package emsgame.testing

import GeoData.Implicits._
import com.vividsolutions.jts.geom.{MultiPolygon, Polygon}
/**
  * Created by pappmar on 02/02/2017.
  */
object RunCheckLandData {

  def main(args: Array[String]): Unit = {
    val data =
      GeoData
        .land
        .getFeatures
        .features()
        .asScala
        .map({ f =>
          f
            .getDefaultGeometry
            .asInstanceOf[MultiPolygon]
        })
        .count({ f =>
          f.polygons.size > 1
        })
//        .flatMap({ f =>
//          f
//            .polygons
//        })
//        .flatMap({ f =>
//          f.interiors
//        })
//        .size


    println(data)

  }

}

object RunLandMinMaxLon {

  def main(args: Array[String]): Unit = {


//    val (min, max) =
//      GeoData
//        .land
//        .getFeatures
//        .features()
//        .asScala
//        .map({ f =>
//          f
//            .getDefaultGeometry
//            .asInstanceOf[MultiPolygon]
//        })
//        .flatMap(_.polygons)
//        .map(_.getBoundary)



  }

}

object RunDumpLand {
  def main(args: Array[String]): Unit = {

    val limits =
      GeoData
        .water
        .getFeatures
        .features()
        .asScala
        .flatMap({ f =>
          f
            .getDefaultGeometry
            .asInstanceOf[MultiPolygon]
            .polygons
        })
        .flatMap({ p =>
          p
            .getCoordinates
            .map(c => (c.x, c.y))
        })
        .foldLeft((Double.MaxValue, Double.MinValue, Double.MaxValue, Double.MinValue))({ (acc, elem) =>
          val (x, y) = elem
          val (minx, maxx, miny, maxy) = acc
          (
            math.min(minx, x),
            math.max(maxx, x),
            math.min(miny, y),
            math.max(maxy, y)
          )
        })

    println(limits)
  }
}