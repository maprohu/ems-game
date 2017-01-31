package emsgame.client.webgl

import emsgame.client.globe.Tesselation.Mesh
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.WebGLRenderingContext._
import org.scalajs.dom.raw.{WebGLRenderingContext, WebGLShader}
import subspace.Matrix4x4

import scala.scalajs.js
import scala.scalajs.js.typedarray.{Float32Array, Uint16Array}
import scala.scalajs.js.JSConverters._

/**
  * Created by pappmar on 31/01/2017.
  */
class EmsWebglMesh(
  canvas: Canvas,
  mesh: Mesh
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

    val vertices =
      mesh
        .vertices
        .flatMap({ v =>
          Iterable(v.x, v.y, v.z)
        })
        .toJSArray

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

    val vertices =
      mesh
        .faces
        .flatMap({ f =>
          import f._
          Iterable(
            v0, v1,
            v1, v2,
            v2, v0
          )
        })
        .toJSArray

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
        |    uniform mat4 uMVMatrix;
        |    uniform mat4 uPMatrix;
        |
        |    void main(void) {
        |        gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);
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

  val pMatrixUniform = {
    gl.getUniformLocation(shaderProgram, "uPMatrix")
  }

  val mvMatrixUniform = {
    gl.getUniformLocation(shaderProgram, "uMVMatrix")
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
    val vpWidth =
      gl.canvas.width
    val vpHeight =
      gl.canvas.height
    gl.viewport(
      0,
      0,
      vpWidth,
      vpHeight
    )

    val pMatrix = Matrix4x4.forPerspective(
      scala.math.Pi.toFloat/3f,
      vpWidth.toFloat / vpHeight,
      0.001f,
      1000f
    )

    gl.uniformMatrix4fv(
      pMatrixUniform,

    )

    import WebGLRenderingContext._
    gl.clearColor(0, 0, 0, 1)
    gl.clear(
      COLOR_BUFFER_BIT
    )

    gl.drawElements(
      LINES,
      mesh.faces.size * 6,
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
