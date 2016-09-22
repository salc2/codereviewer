package ve.chucho.codereview

import scalatags.Text.all
import scalatags.Text.all._

/**
  * Created by chucho on 9/22/16.
  */
object View {

  private val `material-lite.js` = "assets/1.2.1/material.min.js"

  val index = html(
    all.head(
      script(src := `material-lite.js`, `type` := "text/javascript")
    ),
    body()
  ).toString

}
