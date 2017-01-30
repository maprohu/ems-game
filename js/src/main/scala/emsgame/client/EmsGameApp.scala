package emsgame.client

import emsgame.client.layout.MainLayout
import org.scalajs.dom
import org.scalajs.dom.raw.{UIEvent, WebGLRenderingContext}

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.typedarray.Float32Array

/**
  * Created by pappmar on 30/01/2017.
  */
object EmsGameApp extends JSApp {
  override def main(): Unit = {
    val canvas = MainLayout.setup()

    val gl =
      canvas
        .getContext("webgl")
        .asInstanceOf[WebGLRenderingContext]

    dom
      .window
      .addEventListener[UIEvent](
        "resize",
        { e:UIEvent =>
          gl.viewport(
            0,
            0,
            canvas.width,
            canvas.height
          )
        }
      )

    import WebGLRenderingContext._
    gl.clearColor(0, 0, 0, 1)
    gl.clear(
      COLOR_BUFFER_BIT
    )

    val pos = gl.createBuffer()
    gl.bindBuffer(
      ARRAY_BUFFER,
      pos
    )

    val vertices = js.Array(
      0.0,  1.0,  0.0,
      -1.0, -1.0,  0.0,
      1.0, -1.0,  0.0
    )

    gl.bufferData(
      ARRAY_BUFFER,
      new Float32Array(vertices),
      STATIC_DRAW
    )

    val shaderProgram = gl.createProgram()

    val fragmentShader = gl.createShader(FRAGMENT_SHADER)
    gl.shaderSource(
      fragmentShader,
      """
        |    precision mediump float;
        |
        |    void main(void) {
        |        gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
        |    }
      """.stripMargin
    )
    gl.compileShader(fragmentShader)
    gl.attachShader(
      shaderProgram,
      fragmentShader
    )

    val vertexShader = gl.createShader(VERTEX_SHADER)
    gl.shaderSource(
      vertexShader,
      """
        |    attribute vec2 aVertexPosition;
        |
        |    void main(void) {
        |        gl_Position = vec4(aVertexPosition, 1.0, 1.0);
        |    }
      """.stripMargin
    )
    gl.compileShader(vertexShader)
    gl.attachShader(
      shaderProgram,
      vertexShader
    )

    gl.linkProgram(shaderProgram)

    gl.useProgram(shaderProgram)

    gl.drawArrays(
      TRIANGLES,
      0,
      3
    )




  }
}
