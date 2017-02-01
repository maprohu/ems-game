package emsgame.client.globe

import subspace.Matrix4x4

/**
  * Created by pappmar on 01/02/2017.
  */
object Projection {

  val FieldOfView = scala.math.Pi.toFloat/3f

  val TanFovHalf = math.tan( FieldOfView / 2 )

}
