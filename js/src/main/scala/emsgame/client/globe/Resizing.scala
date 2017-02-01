package emsgame.client.globe

import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.{UIEvent, WebGLRenderingContext}

import scala.scalajs.js

/**
  * Created by pappmar on 01/02/2017.
  */
object Resizing {

  def setupEvent(
    canvas: Canvas,
    sizer: (Int, Int) => Unit
  ) = {
    def doSize() = {
      val width = canvas.clientWidth;
      val height = canvas.clientHeight;
      if (canvas.width != width ||
        canvas.height != height) {

        canvas.asInstanceOf[js.Dynamic].width = width;
        canvas.asInstanceOf[js.Dynamic].height = height;

        sizer(width, height)
      }
    }

    dom
      .window
      .addEventListener[UIEvent](
      "resize",
      { _:UIEvent =>
        doSize()
      }
    )

    doSize()
  }

}
