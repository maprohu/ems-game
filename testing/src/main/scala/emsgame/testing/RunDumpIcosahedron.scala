package emsgame.testing

import emsgame.geom.Geo.{Coords, Poly}
import emsgame.geom.{Geo, Tesselation}
import voxels.Icosahedron

/**
  * Created by pappmar on 02/02/2017.
  */
object RunDumpIcosahedron {

  def main(args: Array[String]): Unit = {


    val mesh =
      (0 until 5)
        .foldLeft(
          Icosahedron.mesh
        )({ (acc, _) =>
          Tesselation.tesselate(
            acc
          )
        })

//    mesh
//      .vertices
//      .foreach({ v =>
//        println(s"${v.x} ${v.z} ${math.toDegrees(v.azimuth)}")
//      })


    val nocap =
      Icosahedron
        .excludeCap(mesh)

    val vecs =
      nocap
        .vertices
        .map({ v =>
          Coords(math.toDegrees(v.azimuth), math.toDegrees(v.elevation1))
        })

    val triangles =
      nocap
        .faces
        .map({ f =>
          f
            .indices
            .map(vecs)
        })
        .map({ coords =>
          (coords, Poly(coords).normals)
        })

//    println(triangles.mkString("\n"))


    import H2GisData._

    val conn = connect()

    try {
      val stm =
        conn.prepareStatement(
          s"""select
             |  count(*)
             |from
             |  ${LandTable}
             |where
             |  ${GeomColumn} && ST_MakeEnvelope(?, ?, ?, ?)
         """.stripMargin
        )

      triangles
        .flatMap({ case (_, normals) => normals })
        .foreach({ n =>
          val bounds = Geo.bounds(n)

          // http://www.h2gis.org/docs/1.3/ST_MakeEnvelope/
          // xmin ymin xmax ymax
          stm.setDouble(1, bounds.min.lon)
          stm.setDouble(2, bounds.min.lat)
          stm.setDouble(3, bounds.max.lon)
          stm.setDouble(4, bounds.max.lat)

          val r = stm.executeQuery()

          r.next()
          println(
            r.getInt(1)
//            Iterator
//              .continually(r.next())
//              .takeWhile(identity)
//              .size
          )
        })


    } finally {
      conn.close()
    }




  }

}
