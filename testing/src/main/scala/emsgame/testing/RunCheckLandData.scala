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
