package io.github.suitougreentea.bofu2016bga

import processing.core.PApplet.*
import processing.core.PConstants.*
import processing.core.PGraphics
import processing.core.PVector
import java.util.*

class Scene5(app: Main) : Scene2345(app, 3) {
  val pieceSize = 5f
  val piecesWidth = 32 * 8
  val piecesHeight = 18 * 8

  override val scroll1 = Pair(startBeat + 0f, startBeat + 16f)
  override val scroll2 = Pair(startBeat + 15f, startBeat + 30f)

  fun draw(app: Main, frame: Int, beat: Float) {
    if (beat !in startBeat - 1f..startBeat + 33f) return
    updateBuffer(app, frame, beat)

    with(app) {
      if (beat in startBeat..startBeat + 1.5f) {
        val (px, py) = piecePos[6]
        image(g1, 0f, 0f)
        noStroke()
        fill(opaq(pieceColors[py][px]))
        (0..piecesHeight - 1).forEach { iy ->
          (0..piecesWidth - 1).forEach { ix ->
            val t = cnorm(beat, startBeat, startBeat + 1.5f)
            val s = cube1(t)
            val sx = ix * pieceSize
            val sy = iy * pieceSize
            val ex = 1400f
            val ey = 800f
            val v = PVector(ex - sx, ey - sy)
            val p = mag(1400f, 800f) / v.mag()
            val nv = v.mult(p)
            val nex = sx + nv.x
            val ney = sy + nv.y
            val x = lerp(sx, nex, s)
            val y = lerp(sy, ney, s)
            rect(x, y, pieceSize, pieceSize)
          }
        }
      }

      if(beat in startBeat + 1.5f..startBeat + 15f) image(g1, 0f, 0f)
      if(beat in startBeat + 15f..startBeat + 16f) {
        /**
         * |
         * |↖
         *  ----
         */
        val t = cnorm(beat, startBeat + 15f, startBeat + 16f)
        val s = cube1(t)
        val rad = s * PI / 2
        val radiusE = (height / 2f) / tan(PI / 6) + 640f
        val radiusT = 640f
        val cx = 640f
        val cy = 360f
        val cz = -640f
        val ex = cx - radiusE * sin(rad)
        val ez = cz + radiusE * cos(rad)
        val tx = cx - radiusT * sin(rad)
        val tz = cz + radiusT * cos(rad)
        camera(ex, cy, ez, tx, cy, tz, 0f, 1f, 0f)
        image(g1, 0f, 0f)
        beginShape()
        texture(g2)
        vertex(0f, 0f, -1280f, 0f, 0f)
        vertex(0f, 720f, -1280f, 0f, 720f)
        vertex(0f, 720f, 0f, 1280f, 720f)
        vertex(0f, 0f, 0f, 1280f, 0f)
        endShape()
        camera()
      }
      if(beat in startBeat + 16f..startBeat + 28.5f) image(g2, 0f, 0f)
      if(beat in startBeat + 28.5f..startBeat + 31f) {
        val (px, py) = piecePos[7]
        val t = cnorm(beat, startBeat + 28.5f, startBeat + 30f)
        val s = cube1(t)
        val s2 = pow1(t, 2.3f)
        val cx = lerp((px + 0.5f) * 1280f, 2f * 1280f, s2)
        val cy = lerp((py + 0.5f) * 720f, 2f * 720f, s2)
        pushMatrix()
        translate(640f, 360f)
        scale(lerp(1f, 0.25f, s))
        translate(-cx, -cy)
        drawPieces(app, px, py)

        translate((px + 0.5f) * 1280f, (py + 0.5f) * 720f)
        rotateY(s * PI)
        if(s <= 0.5f) {
          image(g2, -640f, -360f)
        } else {
          noStroke()
          fill(opaq(pieceColors[py][px]))
          rect(-640f, -360f, 1280f, 720f)
        }

        popMatrix()
      }
    }
  }
}
