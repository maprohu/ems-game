package emsgame.testing

import java.nio.file.Files
import java.sql.DriverManager

import org.h2gis.ext.H2GISExtension


/**
  * Created by pappmar on 02/02/2017.
  */
object RunH2Gis {
  import H2GisData._



  def main(args: Array[String]): Unit = {

    val conn = connect()

    val stm = conn.createStatement()

    stm.executeQuery(
      s"""CALL SHPREAD('/git/ems-game/local/land-polygons-complete-4326/land_polygons.shp', '${LandTable}')"""
    )
    stm.execute(
      s"""CREATE SPATIAL INDEX ON ${LandTable}(${GeomColumn})"""
    )

    stm.executeQuery(
      s"""CALL SHPREAD('/git/ems-game/local/water-polygons-split-4326/water_polygons.shp', '${WaterTable}')"""
    )
    stm.execute(
      s"""CREATE SPATIAL INDEX ON ${WaterTable}(${GeomColumn})"""
    )

    conn.close()



  }

}


object RunRecreateH2Gis {
  import H2GisData._

  def main(args: Array[String]): Unit = {
    import ammonite.ops._
    rm(Path(DBPath, pwd) / up)

    val conn = connect()
    H2GISExtension.load(conn)
    conn.close()
  }
}

object H2GisData {

  val DBPath = "./local/h2gis/db"

  val LandTable = "LAND"
  val WaterTable = "WATER"
  val GeomColumn = "the_geom"

  def connect() = {
    org.h2.Driver.load()
    DriverManager.getConnection(
      s"jdbc:h2:file:${DBPath}",
      "sa",
      "sa"
    )
  }



}