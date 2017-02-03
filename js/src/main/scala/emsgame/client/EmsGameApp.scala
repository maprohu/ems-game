package emsgame.client

import emsgame.client.globe._
import emsgame.client.layout.MainLayout
import emsgame.client.webgl.{EmsWebgl, EmsWebglLines, EmsWebglMesh}
import emsgame.geom.Tesselation
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.{UIEvent, WebGLRenderingContext}
import voxels.Icosahedron

import scala.scalajs.js.JSApp

/**
  * Created by pappmar on 30/01/2017.
  */
object EmsGameApp extends JSApp {


  override def main(): Unit = {
    import rx.Ctx.Owner.Unsafe._
    val canvas = MainLayout.setup()

    val painter = new EmsWebglMesh(
      canvas,
      Tesselation.tesselate(
        Icosahedron.mesh,
        2
      )
    )

    val painting = Painting(painter)

    Zooming.setupResult(
      canvas,
      { z =>
        painter.zoomDistance() = z
        painting.requestPaint()
      }
    )

    Rotation.setup(
      canvas,
      { (x, y) =>
        val rpp = painter.radPerPixel.now

        val rotx =
          math.max(
            math.min(
              painter.rotateX.now + (y * rpp),
              Rotation.MaxRotateX
            ),
            -Rotation.MaxRotateX
          )
        painter.rotateX() = rotx

        painter.rotateY() = painter.rotateY.now + (x * rpp / math.cos(rotx))


        painting.requestPaint()
      }
    )

    Resizing.setupEvent(
      canvas,
      { (w, h) =>
        painter.canvasSize() = (w, h)
        painting.requestPaint()
      }
    )

    painting.requestPaint()

  }
}
