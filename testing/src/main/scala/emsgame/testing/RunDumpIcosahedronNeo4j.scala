package emsgame.testing

import com.vividsolutions.jts.geom.Envelope
import emsgame.geom.Geo.{Coords, Poly}
import emsgame.geom.{Geo, Tesselation}
import org.neo4j.gis.spatial.SpatialDatabaseService
import org.neo4j.gis.spatial.filter.SearchIntersectWindow
import org.neo4j.gis.spatial.pipes.GeoPipeline
import voxels.Icosahedron

/**
  * Created by pappmar on 02/02/2017.
  */
object RunDumpIcosahedronNeo4j {

  def main(args: Array[String]): Unit = {


    val mesh =
      (0 until 4)
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


    import RunNeo4j._
    val spatialService = new SpatialDatabaseService(db)
    val layer = spatialService.getLayer(LandLayerName)
//    val spatialIndex = layer.getIndex
//    println(spatialIndex.count())


    try {

      triangles
        .flatMap({ case (_, normals) => normals })
        .foreach({ n =>
          val bounds = Geo.bounds(n)

          val env =
            new Envelope(
              bounds.min.lon,
              bounds.max.lon,
              bounds.min.lat,
              bounds.max.lat
            )

          val tx = db.beginTx()
          val res =
            GeoPipeline
              .startIntersectWindowSearch(layer, env)
              .toSpatialDatabaseRecordList
          println(res.size())
          tx.success()

        })


    } finally {
      db.shutdown()
    }




  }

}
