package io.github.suitougreentea.bofu2016bga

import processing.core.PApplet.*
import processing.core.PConstants.*
import processing.core.PGraphics

open class Scene2345(app: Main, val index: Int) {
  val g1 = app.createGraphics(1280, 720)
  val g2 = app.createGraphics(1280, 720)
  val i1 = app.res.pieceBg[index * 2]
  val i2 = app.res.pieceBg[index * 2 + 1]
  val startBeat = 36f + index * 32f

  @Deprecated("")
  val cameraPos = arrayOf(
          Pair(Pair(2f, 2f), Pair(1.5f, 1.5f)),
          Pair(Pair(1.5f, 0.5f), Pair(2.5f, 1.5f)),
          Pair(Pair(3.5f, 1.5f), Pair(2.5f, 2.5f)),
          Pair(Pair(2.5f, 3.5f), Pair(1.5f, 2.5f))
  )

  val skipPiece1 = app.piecePos.filterIndexed { i, pair -> i % 2 == 0 }
  val skipPiece2 = app.piecePos.filterIndexed { i, pair -> i % 2 == 1 }

  open val scroll1 = Pair(startBeat + 2f, startBeat + 16.5f)
  open val scroll2 = Pair(startBeat + 15.5f, startBeat + 31f)
  open val offset1 = 0f
  open val offset2 = 0f

  fun updateBuffer(app: Main, frame: Int, beat: Float) {
    with(app) {
      hint(ENABLE_DEPTH_TEST)
      with(g1) {
        val bt = cnorm(beat, scroll1.first, scroll1.second)
        val bs = bt * -300f
        beginDraw()
        clear()
        noStroke()
        iimage(i1, bs, 0f)

        drawPieceText(app, g1, beat, bs, 300f, 100f, 0)

        endDraw()
      }

      with(g2) {
        val bt = cnorm(beat, scroll2.first, scroll2.second)
        val bs = bt * -300f
        beginDraw()
        clear()
        noStroke()
        iimage(i2, bs, 0f)

        drawPieceText(app, g2, beat, bs, 300f, 100f, 1)

        endDraw()
      }
    }
  }

  fun slide(x: Float) = exp(-7 * (x - 1f))

  fun drawPieceText(app: Main, g: PGraphics, beat: Float, bs: Float, ox: Float, oy: Float, i: Int) {
    with(app) {
      with(g) {
        val sb = startBeat + i * 14f
        val pt = cnorm(beat, sb + 4f, sb + 5f)
        val ct1 = cnorm(beat, sb + 6f, sb + 7f)
        val ct2 = cnorm(beat, sb + 7f, sb + 8f)

        // 12拍で180px, inは1拍なので[0, 13]
        val ps = ox + slide(pt) + bs * 0.5f
        image(res.line, ps - 100f, oy + 70f)
        image(res.piece, ps, oy)
        image(res.pieceNumber[index * 2 + i], ps + 85f, oy - 80f)

        val cs1 = ox + 20f + slide(ct1) + bs * 0.5f
        val cs2 = ox + 30f + slide(ct2) + bs * 0.5f
        image(res.conversation1[index * 2 + i], cs1, oy + 80f)
        image(res.conversation2[index * 2 + i], cs2, oy + 130f)
      }
    }
  }

  fun drawPieces(app: Main) {
    drawPieces(app, null, null)
  }

  fun drawPieces(app: Main, excludeX: Int?, excludeY: Int?) {
    with(app) {
      (0..5).forEach { ix ->
        (0..5).forEach { iy ->
          if (ix - 1 != excludeX || iy - 1 != excludeY) {
            noStroke()
            val color = if (ix in 1..4 && iy in 1..4) pieceColors[iy - 1][ix - 1] else pieceColorsExpanded[iy][ix]
            fill(opaq(color))
            val ox = (ix - 1) * 1280f
            val oy = (iy - 1) * 720f
            rect(ox, oy, 1280f, 720f)
          }
        }
      }
    }
  }
}

