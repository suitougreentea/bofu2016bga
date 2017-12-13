package io.github.suitougreentea.bofu2016bga

class Scene6 {
  val startBeat = 4f + 32f + 32f + 32f + 32f + 32f

  fun draw(app: Main, frame: Int, beat: Float) {
    if (beat !in startBeat..startBeat + 32f) return

    with(app) {
      val t = cnorm(beat, startBeat + 14f, startBeat + 16f)
      (0..15).forEach {
        val (px, py) = piecePos[it]
        image(res.pieceBg[it], px * 320f, py * 180f, 320f, 180f, 0, 0, 1280, 720)
        noStroke()
        fill(alpha(pieceColors[py][px], 1f - t))
        rect(px * 320f, py * 180f, 320f, 180f)
      }
    }
  }
}

