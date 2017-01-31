package emsgame.client.webgl

import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.WebGLRenderingContext._
import org.scalajs.dom.raw.{WebGLRenderingContext, WebGLShader}

import scala.scalajs.js
import scala.scalajs.js.typedarray.Float32Array

/**
  * Created by pappmar on 31/01/2017.
  */
class EmsWebgl(
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

  val triangleVertexColorBuffer = {
    val triangleVertexColorBuffer = gl.createBuffer()

    gl.bindBuffer(
      ARRAY_BUFFER,
      triangleVertexColorBuffer
    )

    val colors = js.Array(
      1.0, 0.0, 0.0, 1.0,
      0.0, 1.0, 0.0, 1.0,
      0.0, 0.0, 1.0, 1.0
    );


    gl.bufferData(
      ARRAY_BUFFER,
      new Float32Array(colors),
      STATIC_DRAW
    )

    triangleVertexColorBuffer
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
        |    varying vec4 vColor;
        |
        |    void main(void) {
        |        gl_FragColor = vColor;
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
        |    varying vec4 vColor;
        |
        |    void main(void) {
        |        gl_Position = vec4(aVertexPosition, 1.0);
        |        vColor = aVertexColor;
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
  
  val vertexColorAttribute = {
    val vertexColorAttribute = gl.getAttribLocation(shaderProgram, "aVertexColor")
    gl.enableVertexAttribArray(vertexColorAttribute)

    gl.bindBuffer(
      ARRAY_BUFFER,
      triangleVertexColorBuffer
    )

    gl.vertexAttribPointer(
      vertexColorAttribute,
      4,
      FLOAT,
      false,
      0,
      0
    )

    vertexColorAttribute
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

    gl.drawArrays(
      TRIANGLES,
      0,
      3
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
