package emsgame.client.globe

import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.html.Canvas

import scala.scalajs.js

/**
  * Created by pappmar on 01/02/2017.
  */
object Rotation {

  val MaxRotateX = math.Pi * 0.5 * 0.9

  def setup(
    canvas: Canvas,
    rotator: (Int, Int) => Unit
  ) = {

    canvas
      .addEventListener[MouseEvent](
      "mousedown", { _: MouseEvent =>

        val dragListener: js.Function1[MouseEvent, _] = { e: MouseEvent =>
          rotator(
            e.asInstanceOf[js.Dynamic].movementX.asInstanceOf[Int],
            e.asInstanceOf[js.Dynamic].movementY.asInstanceOf[Int]
          )
        }

        dom
          .document
          .addEventListener[MouseEvent](
          "mousemove",
          dragListener
        )

        lazy val upListener: js.Function1[MouseEvent, _] = { _: MouseEvent =>
          dom
            .document
            .removeEventListener(
              "mousemove",
              dragListener
            )

          dom
            .document
            .removeEventListener(
              "mouseup",
              upListener
            )
        }

        dom
          .document
          .addEventListener[MouseEvent](
          "mouseup",
          upListener
        )

      }
    )

  }

}
