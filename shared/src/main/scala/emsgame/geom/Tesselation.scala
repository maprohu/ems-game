package emsgame.geom

import voxels.geometry.Vec3

object Geo {

  case class Bounds(
    min: Coords,
    max: Coords
  )

  case class Coords(
    lon: Double,
    lat: Double
  )

  case class Poly(
    coords: Iterable[Coords]
  ) {
    val normals = {
      val needsSplit =
        coords
          .scanLeft((180.0, -180.0))({ (acc, elem) =>
            val (min, max) = acc
            (
              math.min(min, elem.lon),
              math.max(max, elem.lon)
            )
          })
          .exists({
            case (min, max) =>
              max - min > 180
          })

      if (needsSplit) {

        Iterable(
          coords
            .map({ c =>
              if (c.lon < 0) c.copy(c.lon + 360)
              else c
            }),
          coords
            .map({ c =>
              if (c.lon > 0) c.copy(c.lon - 360)
              else c
            })
        )

      } else {
        Iterable(coords)
      }



    }
  }

  def bounds(coords: Iterable[Coords]) = {
    coords
      .foldLeft(
        Bounds(
          min = Coords(Double.MaxValue, Double.MaxValue),
          max = Coords(Double.MinValue, Double.MinValue)
        )
      )({ (acc, elem) =>
        Bounds(
          min = Coords(
            lat = math.min(elem.lat, acc.min.lat),
            lon = math.min(elem.lon, acc.min.lon)
          ),
          max = Coords(
            lat = math.max(elem.lat, acc.min.lat),
            lon = math.max(elem.lon, acc.min.lon)
          )
        )
      })
  }




}

object Tesselation {
  type Vertex = Vec3
  case class Face(
    v0: Int,
    v1: Int,
    v2: Int
  ) {
    lazy val indices = Vector(v0, v1, v2)
  }

  case class Edge(
    v0: Int,
    v1: Int
  ) {
    val undirected = {
      if (v0 < v1) this
      else Edge(v1, v0)
    }
  }

  def createMidPoint(
    v0: Vertex,
    v1: Vertex
  ) : Vertex = {
    (v0 + v1).normalize
  }

  case class Mesh(
    vertices: Vector[Vertex],
    faces: Vector[Face]
  ) {


  }

  def tesselate(
    mesh: Mesh,
    count: Int = 1
  ) = {
    (0 until count)
      .foldLeft(mesh)({ (acc, _) => tesselateSingle(acc) })

  }

  def tesselateSingle(
    mesh: Mesh
  ) : Mesh = {
    import mesh._
    val facesWithEdges =
      faces
        .map({ f =>
          import f._
          (
            f,
            Edge(v0, v1),
            Edge(v1, v2),
            Edge(v2, v0)
          )
        })

    val (undirectedEdges, midPoints) =
      facesWithEdges
        .flatMap({
          case (_, e0, e1, e2) =>
            Iterable(e0, e1, e2)
        })
        .map(_.undirected)
        .distinct
        .map({ e =>
          val midPoint = createMidPoint(
            vertices(e.v0),
            vertices(e.v1)
          )

          (e, midPoint)
        })
        .unzip

    val undirectedEdgeToMidpointIndex =
      undirectedEdges
        .zipWithIndex
        .toMap
        .mapValues(_ + vertices.size)


    Mesh(
      vertices = vertices ++ midPoints,
      faces =
        facesWithEdges
          .flatMap({
            case (f, e0, e1, e2) =>
              import f._
              val m0 = undirectedEdgeToMidpointIndex(e0.undirected)
              val m1 = undirectedEdgeToMidpointIndex(e1.undirected)
              val m2 = undirectedEdgeToMidpointIndex(e2.undirected)
              Iterable(
                Face(v0, m0, m2),
                Face(v1, m1, m0),
                Face(v2, m2, m1),
                Face(m0, m1, m2)
              )
          })
    )







  }

}
