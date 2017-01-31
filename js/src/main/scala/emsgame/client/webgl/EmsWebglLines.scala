package emsgame.client.webgl

import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.WebGLRenderingContext._
import org.scalajs.dom.raw.{WebGLRenderingContext, WebGLShader}

import scala.scalajs.js
import scala.scalajs.js.typedarray.{Float32Array, Uint16Array}

/**
  * Created by pappmar on 31/01/2017.
  */
class EmsWebglLines(
  canvas: Canvas
) {

  val gl =
    canvas
      .getContext("webgl")
      .asInstanceOf[WebGLRenderingContext]

  val triangleVertexPositionBuffer = {
    val triangleVertexPositionBuffer = gl.createBuffer()

    gl.bindBuffer(
      ARRAY_BUFFER,
      triangleVertexPositionBuffer
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

    triangleVertexPositionBuffer
  }

  val lineVertexIndexBuffer = {
    val lineVertexIndexBuffer = gl.createBuffer()

    gl.bindBuffer(
      ELEMENT_ARRAY_BUFFER,
      lineVertexIndexBuffer
    )

    val vertices = js.Array(
      0, 1,
      1, 2,
      2, 0
    )

    gl.bufferData(
      ELEMENT_ARRAY_BUFFER,
      new Uint16Array(vertices),
      STATIC_DRAW
    )

    lineVertexIndexBuffer
  }


  def checkShader(
    shader: WebGLShader
  ): Unit = {
    if (
      !gl
        .getShaderParameter(
          shader,
          COMPILE_STATUS
        )
        .asInstanceOf[Boolean]
    ) {
      dom.console.error(
        gl.getShaderInfoLog(
          shader
        )
      )
    }

  }

  val shaderProgram = {
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
    checkShader(fragmentShader)
    gl.attachShader(
      shaderProgram,
      fragmentShader
    )

    val vertexShader = gl.createShader(VERTEX_SHADER)
    gl.shaderSource(
      vertexShader,
      """
        |    attribute vec3 aVertexPosition;
        |    attribute vec4 aVertexColor;
        |
        |    void main(void) {
        |        gl_Position = vec4(aVertexPosition, 1.0);
        |    }
      """.stripMargin
    )
    gl.compileShader(vertexShader)
    checkShader(vertexShader)
    gl.attachShader(
      shaderProgram,
      vertexShader
    )

    gl.linkProgram(shaderProgram)
    gl.useProgram(shaderProgram)

    shaderProgram


  }

  val vertexPositionAttribute = {
    val vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "aVertexPosition")
    gl.enableVertexAttribArray(vertexPositionAttribute)

    gl.bindBuffer(
      ARRAY_BUFFER,
      triangleVertexPositionBuffer
    )

    gl.vertexAttribPointer(
      vertexPositionAttribute,
      3,
      FLOAT,
      false,
      0,
      0
    )

    vertexPositionAttribute
  }
  
  def drawScene() = {
    gl.viewport(
      0,
      0,
      gl.canvas.width,
      gl.canvas.height
    )

    import WebGLRenderingContext._
    gl.clearColor(0, 0, 0, 1)
    gl.clear(
      COLOR_BUFFER_BIT
    )

    gl.drawElements(
      LINES,
      6,
      UNSIGNED_SHORT,
      0
    )

  }


  def setSize() = {
    val width = canvas.clientWidth;
    val height = canvas.clientHeight;
    if (canvas.width != width ||
      canvas.height != height) {

      canvas.asInstanceOf[js.Dynamic].width = width;
      canvas.asInstanceOf[js.Dynamic].height = height;

      drawScene()
    }
  }



}
