package io.github.suitougreentea.bofu2016bga

import processing.core.PApplet.*
import processing.core.PConstants.*
import processing.core.PGraphics

class Scene7 {
  val startBeat = 4f + 32f + 32f + 32f + 32f + 32f + 32f

  /**
   *    |
   *  -12
   *   21-
   *   |
   * ∖    /
   *  3  3
   *   **
   *   **
   *  3  3
   * /    ∖
   *
   *   ||
   *  *46*
   * -7**5-
   * -5**7-
   *  *64*
   *   ||
   *
   * 1: ↺ (+PI)
   *    |
   * -1:↻ (-PI)
   *    |
   * 2: ↺- (+PI)
   * -2:↻- (-PI)
   */
  val moveList = arrayOf(
          Pair(0f, arrayOf(
                  Triple(Pair(-1f, 1f), Pair(1f, 1f), 2),
                  Triple(Pair(4f, 2f), Pair(2f, 2f), -2)
          )),
          Pair(0.75f, arrayOf(
                  Triple(Pair(2f, -1f), Pair(2f, 1f), 1),
                  Triple(Pair(1f, 4f), Pair(1f, 2f), -1)
          )),
          Pair(2.5f, arrayOf(
                  Triple(Pair(-1f, -1f), Pair(0f, 0f), -2),
                  Triple(Pair(4f, -1f), Pair(3f, 0f), -2),
                  Triple(Pair(4f, 4f), Pair(3f, 3f), 2),
                  Triple(Pair(-1f, 4f), Pair(0f, 3f), 2)
          )),
          Pair(4f, arrayOf(
                  Triple(Pair(1f, -2f), Pair(1f, 0f), -1),
                  Triple(Pair(2f, 5f), Pair(2f, 3f), 1)
          )),
          Pair(4.75f, arrayOf(
                  Triple(Pair(5f, 1f), Pair(3f, 1f), 2),
                  Triple(Pair(-2f, 2f), Pair(0f, 2f), -2)
          )),
          Pair(6.5f, arrayOf(
                  Triple(Pair(2f, -2f), Pair(2f, 0f), 1),
                  Triple(Pair(1f, 5f), Pair(1f, 3f), -1)
          )),
          Pair(7.25f, arrayOf(
                  Triple(Pair(-2f, 1f), Pair(0f, 1f), -2),
                  Triple(Pair(5f, 2f), Pair(3f, 2f), 2)
          ))
  )

  fun draw(app: Main, frame: Int, beat: Float) {
    if (beat !in startBeat..startBeat + 32f) return

    with(app) {
      if(beat in startBeat..startBeat + 8f) {
        moveList.forEach {
          val (timing, arr) = it
          val t = cnorm(beat, startBeat + timing, startBeat + timing + 0.75f)
          val s = cube1(t)
          arr.forEach {
            val (sp, ep) = it
            val (sx, sy) = sp
            val (ex, ey) = ep
            val px = ex.toInt()
            val py = ey.toInt()
            val x = lerp(sx, ex, s) * 320f
            val y = lerp(sy, ey, s) * 180f
            /*noStroke()
            fill(opaq(pieceColors[py][px]))
            rect(x, y, 320f, 180f)*/
            image(res.glassxy[py][px], x, y, 320f, 180f)
          }
        }
      }
      if(beat in startBeat + 8f..startBeat + 16f) {
        moveList.forEach {
          val (timing, arr) = it
          val t = cnorm(beat, startBeat + 8f + timing, startBeat + 8f + timing + 0.75f)
          val s = cube1(t)
          arr.forEach {
            val (bx, by) = it.second
            val px = bx.toInt()
            val py = by.toInt()
            pushMatrix()
            translate((bx + 0.5f) * 320f, (by + 0.5f) * 180f)
            when(it.third) {
              1 -> rotateY(rotdp(s))
              -1 -> rotateY(rotdm(s))
              2 -> rotateX(rotdp(s))
              -2 -> rotateX(rotdm(s))
            }
            if(s <= 0.5f) {
              noStroke()
              fill(opaq(pieceColors[py][px]))
              image(res.glassxy[py][px], -160f, -90f, 320f, 180f)
            }
            else {
              val i = piecePosInv[Pair(px, py)] ?: 0
              image(res.pieceBg[i], -160f, -90f, 320f, 180f)
            }
            popMatrix()
          }
        }
      }
    }
  }
}

