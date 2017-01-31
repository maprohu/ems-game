package voxels

/**
  * Created by pappmar on 31/01/2017.
  */
object RunIcosahedron {

  def main(args: Array[String]): Unit = {

    val triangleEdges =
      Icosahedron
        .facesStructure
        .flatMap({
          case List(i1, i2, i3) =>
            Seq(
              (i1, i2),
              (i2, i3),
              (i3, i1)
            )
        })

    val edges =
      triangleEdges
//        .distinct
//        .toIndexedSeq

    println(
      edges
        .groupBy(o => Set(o._1, o._2))
        .mkString("\n")

    )
    println(
      edges.size
    )

//    println(s"edges (${edges.size}):")
//    println(edges.mkString("\n"))
//    println(edges.size)
//
//    val edgesByIndex =
//      edges
//        .zipWithIndex
//        .toMap


  }

}
