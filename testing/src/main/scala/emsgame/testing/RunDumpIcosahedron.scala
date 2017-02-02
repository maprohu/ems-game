package emsgame.testing

import voxels.Icosahedron

/**
  * Created by pappmar on 02/02/2017.
  */
object RunDumpIcosahedron {

  def main(args: Array[String]): Unit = {

    println(
      math.toDegrees(math.atan2(0.0, -0.0))
    )

    val mesh = Icosahedron.mesh


    mesh
      .vertices
      .foreach({ v =>
        println(s"${v.x} ${v.z} ${math.toDegrees(v.azimuth)}")

      })

  }

}
