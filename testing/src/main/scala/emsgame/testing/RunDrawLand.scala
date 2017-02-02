package emsgame.testing

import java.awt.geom.{AffineTransform, GeneralPath, Path2D}
import java.awt._
import javax.swing.{JFrame, JPanel}

import com.vividsolutions.jts.geom.{MultiPolygon, Polygon}
import org.opengis.feature.simple.SimpleFeature

/**
  * Created by pappmar on 02/02/2017.
  */
object RunDrawLand {

  def main(args: Array[String]): Unit = {

    val polys =
      GeoData
        .land
        .getFeatures
        .features()

    val it = new Iterator[SimpleFeature] {
      override def hasNext: Boolean = polys.hasNext
      override def next(): SimpleFeature = polys.next()
    }

    val geoms =
      it
//        .take(200000)
        .map({ poly =>
          poly
            .getDefaultGeometry
            .asInstanceOf[MultiPolygon]
            .getGeometryN(0)
            .asInstanceOf[Polygon]
            .getExteriorRing
            .getCoordinates
            .map({ c =>
              (c.x, c.y)
            })
        })
        .toVector

    val frame = new JFrame()
    frame.setExtendedState(Frame.MAXIMIZED_BOTH)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    val panel = new JPanel() {
      override def paint(g: Graphics): Unit = {
        val g2 = g.asInstanceOf[Graphics2D]

        val at = new AffineTransform()
        at.translate(getWidth/2, getHeight/2)
        at.scale(getWidth / 360.0, -getHeight / 180.0)

        g2.setColor(Color.BLUE)
        geoms.foreach({ points =>

          val polyline = new Path2D.Double()
          polyline.moveTo(points.head._1, points.head._2)
          points.tail.foreach({ p =>
            polyline.lineTo(p._1, p._2)
          })
          polyline.closePath()


          polyline.transform(at)

          g2.fill(polyline)

        })

      }
    }

    frame.setContentPane(panel)



    frame.setVisible(true)

  }

}
