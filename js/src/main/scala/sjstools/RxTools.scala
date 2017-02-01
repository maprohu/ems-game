package sjstools

import rx.{Ctx, Rx, Var}

/**
  * Created by pappmar on 01/02/2017.
  */
object RxTools {

  def holder[T >: Null](
    value : => T,
    deps: Rx[_]*
  )(implicit ctx: Ctx.Owner) = {
    val h = new Holder[T](value)
    deps.foreach { d => h.dep(d) }
    h
  }

  class Holder[T >: Null](
    calc : => T
  ) {
    private var value = calc

    def dep[D](rxv: Rx[D])(implicit ctx: Ctx.Owner) = {
      rxv.foreach({ _ =>
        value = null
      })
    }

    def get : T = {
      if (value == null) {
        value = calc
      }
      value
    }

  }

}


