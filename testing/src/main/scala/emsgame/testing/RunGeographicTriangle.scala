package emsgame.testing

import com.vividsolutions.jts.geom.GeometryFactory
import emsgame.geom.Tesselation.Face
import org.geotools.factory.{BasicFactories, CommonFactoryFinder}
import org.geotools.geometry.{GeometryBuilder, GeometryFactoryFinder}
import org.opengis.filter.FilterFactory
import voxels.Icosahedron

/**
  * Created by pappmar on 01/02/2017.
  */
object RunGeographicTriangle {

  def main(args: Array[String]): Unit = {

    val m =
      Icosahedron
        .mesh

    val f =
      m.faces(13)

    def vectors(f: Face) = {
      List(
        m.vertices(f.v0),
        m.vertices(f.v1),
        m.vertices(f.v2)
      )
    }

    val vs =
      vectors(f)


    val coords =
      vs
        .map({ v0 =>
          (
            math.toDegrees(v0.azimuth) + 360,
            math.toDegrees(v0.elevation1)
          )
        })

    println(
      coords
        .mkString("\n")
    )


    val ff = CommonFactoryFinder.getFilterFactory


    val fs =
      GeoData
        .land

    val gd =
      fs
        .getSchema
        .getGeometryDescriptor



    val geom =
      gd
        .getLocalName

//    val gf =
//      BasicFactories
//        .getDefault
//        .getGeometryFactory(
//          gd.getCoordinateReferenceSystem
//        )

    val gb = new GeometryBuilder(
      gd.getCoordinateReferenceSystem
    )

    println(
      gb.getCoordinateReferenceSystem.getCoordinateSystem.getDimension
    )

    val filter =
      ff.contains(
        geom,
        gb.createPoint(
          0, 0
        )
      )

//    val result =
//      fs
//        .getFeatures(
//        )

//    println(result.size())
//    println(
//      result.features().next()
//    )


//    val result =
//      fs
//        .getFeatures(
//          ff.intersects(
//            geom,
//            gb.createSurfaceBoundary(
//              gb.createPointArray(
//                coords
//                  .flatMap({ case (x, y) =>
//                    Iterable(x, y)
//                  })
//                  .toArray
//              )
//
//            )
//          )
//        )
//
//    println(result.size())

    val feat =
      fs
        .getFeatures
        .features()
        .next()

    println(feat)

    println(
      filter.evaluate(feat)
    )




  }

}
