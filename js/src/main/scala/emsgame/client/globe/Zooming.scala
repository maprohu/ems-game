package emsgame.client.globe

import org.scalajs.dom.WheelEvent
import org.scalajs.dom.html.Canvas

/**
  * Created by pappmar on 01/02/2017.
  */
object Zooming {


  val MinDistance = 0.01
  val MaxDistance = 3.0
  val ZoomFactor = 1.5

  def setupResult(
    canvas: Canvas,
    zoomer: Double => Unit
  ) = {
    var z = MaxDistance

    setupEvent(
      canvas,
      { d:Int =>
        z =
          math.min(
            math.max(
              z * math.pow(ZoomFactor, d),
              MinDistance
            ),
            MaxDistance
          )

        zoomer(z)
      }
    )

    zoomer(z)

  }

  def setupEvent(
    canvas: Canvas,
    zoomer: Int => Unit
  ) = {

    canvas
      .addEventListener(
        "wheel",
        { e:WheelEvent =>
          zoomer(
            math.signum(e.deltaY).asInstanceOf[Int]
          )
        }
      )
  }

}
