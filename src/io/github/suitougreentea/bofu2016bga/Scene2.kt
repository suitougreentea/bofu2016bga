package io.github.suitougreentea.bofu2016bga

import processing.core.PApplet.*
import processing.core.PConstants.*
import processing.core.PGraphics

/**
 * [36, 68)
 * [68, 100)
 * [100, 132)
 * [132, 164)
 *
 * 画像の横幅は1728なのでOffsetは[0, 448]
 */

// TODO: offset
class Scene2(app: Main): Scene2345(app, 0) {
  fun draw(app: Main, frame: Int, beat: Float) {
    if(beat !in startBeat - 1f..startBeat + 33f) return
    updateBuffer(app, frame, beat)

    with(app) {
      when {
        beat < startBeat -> {
        }
        beat in startBeat..startBeat + 3f -> {
          pushMatrix()
          val t = cnorm(beat, startBeat, startBeat + 1.5f)
          val s = cube1(t)
          val (cx1, cy1) = cameraPos[index].first
          val (cx2, cy2) = cameraPos[index].second
          val cx = lerp(cx1, cx2, s) * 1280f
          val cy = lerp(cy1, cy2, s) * 720f

          val ta = cnorm(beat, startBeat + 1.5f, startBeat + 3f)
          val sa = cube1(ta)
          translate(640f, 360f)
          scale(lerp(0.4f, 1.0f, sa))
          translate(-cx, -cy)
          if(beat in startBeat..startBeat + 1.5f) drawPieces(app) else drawPieces(app, skipPiece1[index].first, skipPiece1[index].second)

          popMatrix()

          if (beat in startBeat + 1.5f..startBeat + 3f) {
            val t = cnorm(beat, startBeat + 1.5f, startBeat + 3f)
            val s = cube1(t)
            pushMatrix()
            translate(640f, 360f)
            rotateY(PI - PI * sa)
            scale(lerp(0.4f, 1.0f, sa))
            translate(-640f, -360f)
            if (sa <= 0.5) {
              val (ix, iy) = skipPiece1[index]
              fill(opaq(pieceColors[iy][ix]))
              noStroke()
              rect(0f, 0f, 1280f, 720f)
            } else {
              image(g1, 0f, 0f)
            }
            popMatrix()
          }
        }
        beat in startBeat + 3f..startBeat + 15.5f -> {
          image(g1, 0f, 0f)
        }
        beat in startBeat + 15.5f..startBeat + 16.5f -> {
          val t = cnorm(beat, startBeat + 15.5f, startBeat + 16.5f)
          val s = cube1(t)
          pushMatrix()
          translate(0f, s * 720f)
          //translate(-s * 1280f, 0f)
          image(g1, 0f, 0f)
          translate(0f, -720f)
          //translate(1280f, 0f)
          image(g2, 0f, 0f)
          popMatrix()
        }
        beat in startBeat + 16.5f..startBeat + 29.5f -> {
          image(g2, 0f, 0f)
        }
        beat in startBeat + 29.5f..startBeat + 31f -> {
          pushMatrix()
          val t = cnorm(beat, startBeat + 29.5f, startBeat + 31f)
          val s = cube1(t)
          val (sx, sy) = skipPiece2[index]
          val cx = sx * 1280f + 640f
          val cy = sy * 720f + 360f

          translate(640f, 360f)
          scale(lerp(1.0f, 0.4f, s))
          translate(-cx, -cy)
          drawPieces(app, skipPiece2[index].first, skipPiece2[index].second)
          popMatrix()

          pushMatrix()
          translate(640f, 360f)
          rotateY(PI * s)
          scale(lerp(1.0f, 0.4f, s))
          translate(-640f, -360f)
          if (s <= 0.5) {
            image(g2, 0f, 0f)
          } else {
            val (ix, iy) = skipPiece1[index]
            fill(opaq(pieceColors[iy][ix]))
            noStroke()
            rect(0f, 0f, 1280f, 720f)
          }
          popMatrix()
        }
        beat in startBeat + 31f..startBeat + 32f -> {
          // 2, 1を中心とした回転がしたい
          val t = cnorm(beat, startBeat + 31f, startBeat + 32f)
          val s = pow1(t, 2.5f)
          pushMatrix()
          translate(640f * 1.4f, 360f * 1.4f)
          scale(0.5f)
          rotateZ(PI * s)
          (0..5).forEach { ix ->
            (0..5).forEach { iy ->
              noStroke()
              val color = if (ix in 1..4 && iy in 1..4) pieceColors[iy - 1][ix - 1] else pieceColorsExpanded[iy][ix]
              fill(opaq(color))
              val ox = (ix - 3) * 1280f
              val oy = (iy - 2) * 720f
              rect(ox, oy, 1280f, 720f)
            }
          }
          popMatrix()
        }
        else -> {
        }
      }
    }
  }
}

