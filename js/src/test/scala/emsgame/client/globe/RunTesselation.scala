package emsgame.client.globe

import emsgame.geom.Tesselation
import emsgame.geom.Tesselation.{Face, Mesh}
import voxels.Icosahedron
import voxels.geometry.Vec3

/**
  * Created by pappmar on 31/01/2017.
  */
object RunTesselation {

  def main(args: Array[String]): Unit = {

    val t =
      Tesselation.tesselate(
        Mesh(
          vertices =
            Vector(
              Vec3(0, 1, 1),
              Vec3(1, -1, 1),
              Vec3(-1, -1, 1)
            ).map(_.normalize),
          faces =
            Vector(
              Face(0, 1, 2)
            )
        )
      )

    println(t)
  }

}

object RunTesselateIcosahedron {
  def main(args: Array[String]): Unit = {
    val t =
      Tesselation.tesselate(
        Icosahedron.mesh
      )

    println(t)
    println(t.faces.size)
    println(t.vertices.size)
  }
}
