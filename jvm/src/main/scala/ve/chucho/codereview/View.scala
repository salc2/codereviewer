package ve.chucho.codereview

import scalatags.Text.all
import scalatags.Text.all._

/**
  * Created by chucho on 9/22/16.
  */
object View {

  private val `material-lite.js` = "assets/1.2.1/material.min.js"
  private val frontendApp = "app/codereview.js"

  val index = html(
    all.head(
      script(src := `material-lite.js`, `type` := "text/javascript"),
      script(src := frontendApp, `type` := "text/javascript")
    ),
    body(
      script(
        """
          |ve.chucho.codereview.CodeReviewApp().main()
        """.stripMargin)
    )
  ).toString

}
