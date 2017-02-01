package emsgame.client.globe

import org.scalajs.dom

/**
  * Created by pappmar on 01/02/2017.
  */
object Painting {
  def apply(
    painter: Painter
  ) : Painting = {

    new Painting {
      var isPainting = false

      def requestPaint() = {
        if (!isPainting) {

          isPainting = true

          dom
            .window
            .setTimeout(
              { () =>
                painter.paint()

                isPainting = false
              },
              0
            )

        }

      }

    }

  }

}

trait Painting {
  def requestPaint() : Unit
}

trait Painter {
  def paint() : Unit
}

