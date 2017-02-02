package voxels

import emsgame.geom.Tesselation.{Face, Mesh}
import voxels.geometry.Vec3
import voxels.geometry.Geometry.gold

/**
 * Created by markus on 26/05/15.
 */
object Icosahedron {

  val NorthPoleIndex = 0
  val SouthPoleIndex = 6

  def isPolarIndex(idx: Int) = idx == NorthPoleIndex || idx == SouthPoleIndex

  def excludeCap(mesh: Mesh) : Mesh = {
    mesh.copy(
      faces =
        mesh
          .faces
          .filterNot(f => isPolarIndex(f.v0) || isPolarIndex(f.v1) || isPolarIndex(f.v2))
    )
  }


  val vertices = {
    val lat = math.atan(0.5)
    val sinLat = math.sin(lat)
    val cosLat = math.cos(lat)

    val pole =
      Vec3(0, 1, 0)

    val ring =
      (0 until 5)
        .map({ idx =>
          val deg = 2 * math.Pi * idx / 5
          Vec3(
            math.cos(deg) * cosLat ,
            sinLat,
            math.sin(deg) * cosLat
          )
        })

    val half = pole +: ring

    half
      .++(half.map(_.negate))
      .toVector
  }

  val facesStructure = {
    val cap =
      (0 until 5)
        .map({ idx =>
          List(
            0,
            idx + 1,
            (idx + 1) % 5 + 1
          )
        })

    val belt =
      (0 until 5)
        .map({ idx =>
          List(
            (idx + 3) % 5 + 1 + 6,
            (idx + 1) % 5 + 1,
            idx + 1
          )
        })


    cap
      .++(
        cap.map({
          case List(v0, v1, v2) =>
            List(v0+6, v2+6, v1+6)
        })
      )
      .++(
        belt
      )
      .++(
        belt.map({
          case List(v0, v1, v2) =>
            List(v0-6, v2+6, v1+6)
        })

      )


  }


//  val vertices: List[Vec3] =
//    Vec3 ( 0, 1, gold ) ::
//    Vec3 ( 0, -1, gold ) ::
//    Vec3 ( 0, -1, -gold ) ::
//    Vec3 ( 0, 1, -gold ) ::
//    Vec3 ( 1,  gold, 0 ) ::
//    Vec3 ( -1, gold, 0 ) ::
//    Vec3 ( -1, -gold, 0 ) ::
//    Vec3 ( 1, -gold, 0 ) ::
//    Vec3 ( gold, 0, 1 ) ::
//    Vec3 ( gold, 0, -1 ) ::
//    Vec3 ( -gold, 0, -1 ) ::
//    Vec3 ( -gold, 0, 1 ) :: Nil

//  val facesStructure =
//    List(
//      List( 0, 1, 8 ),
//      List( 0, 11, 1 ),
//      List( 2, 3, 9 ),
//      List( 2, 10, 3 ),
//      List( 4, 5, 0 ),
//      List( 4, 3, 5 ),
//      List( 6, 7, 1 ),
//      List( 6, 2, 7 ),
//      List( 8, 9, 4 ),
//      List( 8, 7, 9 ),
//      List( 10, 11, 5 ),
//      List( 10, 6, 11 ),
//      List( 0, 8, 4 ),
//      List( 0, 5, 11 ),
//      List( 1, 7, 8 ),
//      List( 1, 11, 6 ),
//      List( 2, 9, 7 ),
//      List( 2, 6, 10 ),
//      List( 3, 4, 9 ),
//      List( 3, 10, 5 )
//    )

  val mesh =
    Mesh(
      vertices =
        Icosahedron
          .vertices
//          .map(v => Vec3(v.x, v.y, v.z))
//          .toVector
          .map(_.normalize),
      faces =
        Icosahedron
          .facesStructure
          .toVector
          .map({
            case List(v0, v1, v2) =>
              Face(v0, v1, v2)
          })

    )
}
