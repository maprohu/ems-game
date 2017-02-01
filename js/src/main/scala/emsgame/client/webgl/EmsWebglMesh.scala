package emsgame.client.webgl

import emsgame.client.globe.{Globe, Painter, Projection, Zooming}
import emsgame.geom.Tesselation.Mesh
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.WebGLRenderingContext._
import org.scalajs.dom.raw.{WebGLRenderingContext, WebGLShader}
import rx.{Ctx, Rx, Var}
import sjstools.RxTools
import subspace.{Matrix4x4, Orientation, Quaternion, Vector3}

import scala.scalajs.js.typedarray.{Float32Array, Uint16Array}
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.typedarray.TypedArrayBufferOps._

/**
  * Created by pappmar on 31/01/2017.
  */
class EmsWebglMesh(
  canvas: Canvas,
  mesh: Mesh
)(implicit
  ctx: Ctx.Owner
) extends Painter {

  val gl =
    canvas
      .getContext("webgl")
      .asInstanceOf[WebGLRenderingContext]

  val rotateY = Var(0.0)
  val rotateX = Var(0.0)
  val zoomDistance = Var(1.0)
  val canvasSize = Var((canvas.width, canvas.height))

  val radPerPixel = Rx {
    val cs = canvasSize()
    val d = zoomDistance()
    val (_, height) = cs

    (d * Projection.TanFovHalf) / (height / 2.0)
  }

  val mvMatrixTypedBuffer = {
    val mvMatrixBuffer = Matrix4x4.identity.allocateBuffer
    val mvMatrixTypedBuffer = mvMatrixBuffer.typedArray()
    val mvMatrix = RxTools.holder(
      {
        val mRotY =
          Matrix4x4.forRotation(
            Quaternion.forAxisAngle(
              Orientation.y,
              rotateY.now.toFloat
            )
          )

        val mRotX =
          Matrix4x4.forRotation(
            Quaternion.forAxisAngle(
              Orientation.x,
              rotateX.now.toFloat
            )
          )

        val mTrans =
          Matrix4x4
            .forTranslation(
              Vector3(0, 0, -(Globe.Radius + zoomDistance.now).toFloat)
            )

        (mTrans * mRotX * mRotY)
          .updateBuffer(
            mvMatrixBuffer
          )

        gl.uniformMatrix4fv(
          mvMatrixUniform,
          false,
          mvMatrixTypedBuffer
        )
      },
      zoomDistance,
      rotateY,
      rotateX
    )

    { () =>
      mvMatrix.get
    }
  }

  val pMatrixTypedBuffer = {
    val mvMatrixBuffer = Matrix4x4.identity.allocateBuffer
    val mvMatrixTypedBuffer = mvMatrixBuffer.typedArray()
    val mvMatrix = RxTools.holder(
      {
        val (width, height) = canvasSize.now

        gl.viewport(
          0,
          0,
          width,
          height
        )

        val pMatrix = Matrix4x4.forPerspective(
          Projection.FieldOfView,
          width.toFloat / height,
          Zooming.MinDistance.toFloat / 2,
          100f
        )

        pMatrix
          .updateBuffer(
            mvMatrixBuffer
          )

        gl.uniformMatrix4fv(
          pMatrixUniform,
          false,
          mvMatrixTypedBuffer
        )
      },
      canvasSize
    )

    { () =>
      mvMatrix.get
    }
  }


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


  override def paint(): Unit = {
    drawScene()
  }

  def drawScene() = {
    pMatrixTypedBuffer()
    mvMatrixTypedBuffer()

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





}
