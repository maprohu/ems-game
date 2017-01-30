package emsgame.client.layout

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLStyleElement


/**
  * Created by pappmar on 30/01/2017.
  */
object MainLayout {

  def reset() = {
    import scalacss.Defaults._
    import scalacss.ScalatagsCss._

    val styles = new StyleSheet.Standalone {
      import dsl._

      "html, body" - (
        margin.`0`,
        padding.`0`,
        border.`0`,
        width(100.%%),
        height(100.%%),
        overflow.hidden
      )

    }

    dom
      .document
      .head
      .appendChild(
        styles
          .render[scalatags.JsDom.TypedTag[HTMLStyleElement]]
          .render
      )
  }

  def setup() = {
    reset()

    import scalatags.JsDom.all._

    val cnv =
      canvas(
        width := 100.pct,
        height := 100.pct,
        margin := 0.px
      ).render

    dom
      .document
      .body
      .appendChild(
        cnv
      )

    cnv
  }

}
