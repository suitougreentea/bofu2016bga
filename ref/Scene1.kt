package io.github.suitougreentea.bofu2016bga

class Scene1 {
  val horizontalIndex = arrayOf(1, 3, 2, 0)
  val horizontalTiming = arrayOf(0f, 4f, 2f, 6f).map { it + 4f }
  val verticalTiming = arrayOf(0f, 4f, 2f, 6f).map { it + 12f }
  fun draw(app: Main, frame: Int, beat: Float) {
    with(app) {
      if (beat in 0f..32f) {
        (0..3).forEach {
          val s = horizontalTiming[it]
          val t = cnorm(beat, s, s + 2f)
          val i = cube1(t)
          noStroke()
          fill(opaq(pieceColors[it][horizontalIndex[it]]))
          rect(0f, it * 180f, i * 1280f, 180f)
        }

        (0..3).forEach {
          val s = verticalTiming[it]
          val t = cnorm(beat, s, s + 2f)
          val i = cube1(t)
          noStroke()

          fill(opaq(pieceColors[it][0]))
          rect(it * 320f, 0f, 320f, cnorm(i, 0f, 0.25f) * 180f)
          fill(opaq(pieceColors[it][1]))
          rect(it * 320f, 180f, 320f, cnorm(i, 0.25f, 0.5f) * 180f)
          fill(opaq(pieceColors[it][2]))
          rect(it * 320f, 360f, 320f, cnorm(i, 0.5f, 0.75f) * 180f)
          fill(opaq(pieceColors[it][3]))
          rect(it * 320f, 540f, 320f, cnorm(i, 0.75f, 1f) * 180f)
        }
      }
    }
  }
}

