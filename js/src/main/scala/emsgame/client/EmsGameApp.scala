package emsgame.client

import emsgame.client.globe.Tesselation
import emsgame.client.layout.MainLayout
import emsgame.client.webgl.{EmsWebgl, EmsWebglLines, EmsWebglMesh}
import org.scalajs.dom
import org.scalajs.dom.raw.{UIEvent, WebGLRenderingContext}
import voxels.Icosahedron

import scala.scalajs.js.JSApp

/**
  * Created by pappmar on 30/01/2017.
  */
object EmsGameApp extends JSApp {


  override def main(): Unit = {
    val canvas = MainLayout.setup()

    val webgl = new EmsWebglMesh(
      canvas,
      Tesselation.tesselate(
        Tesselation.tesselate(
          Icosahedron.mesh
        )
      )
    )

    dom
      .window
      .addEventListener[UIEvent](
        "resize",
        { e:UIEvent =>
          webgl.setSize()
        }
      )

    webgl.setSize()
  }
}
