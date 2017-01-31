package sjstools.osm

/**
  * Created by pappmar on 31/01/2017.
  */
object RunOsmClient {

  def main(args: Array[String]): Unit = {

    val point = LatLonPoint(51.51202,0.02435,17)
    val tile = point.toTile
    // ==> Tile(65544,43582,17)
    val uri = tile.toURI
    // ==> http://tile.openstreetmap.org/17/65544/43582.png

    println(uri)

    println(point)
    println(tile)
    println(tile.toLatLon)
    println(tile.toLatLon.toTile)

  }

}
