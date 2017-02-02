package emsgame.server

import java.awt.image.BufferedImage

/**
  * Created by pappmar on 02/02/2017.
  */
object Rendering {

  case class Config(
    tileWidth: Int = 256,
    tileHeight: Int = 256
  )



  def draw(
    width: Int,
    height: Int
  ) = {

    val img = new BufferedImage(
      width,
      height,
      BufferedImage.TYPE_INT_RGB
    )

    val g2 = img.createGraphics()





  }


}
