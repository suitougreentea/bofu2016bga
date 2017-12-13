package io.github.suitougreentea.bofu2016bga

import processing.core.PApplet.*
import processing.core.PConstants.*
import processing.core.PGraphics
import processing.core.PVector
import java.util.*

class Scene3(app: Main) : Scene2345(app, 1) {
  override val scroll1 = Pair(startBeat + 0f, startBeat + 16f)
  override val scroll2 = Pair(startBeat + 15f, startBeat + 30f)

  /**
   *  @
   *   @
   * @
   *
   *   @
   * @
   *  @
   *
   * @
   *
   *   @
   */
  val blink1 = arrayOf(
          arrayOf(Pair(4, 0), Pair(3, 2), Pair(2, 1)),
          arrayOf(Pair(4, 1), Pair(3, 0), Pair(2, 2)),
          arrayOf(Pair(4, 2), Pair(2, 0))
  )

  /**
   * @
   *   @
   *  @
   *
   *  @
   * @
   *   @
   *
   *    @
   *
   * @
   */
  val blink2 = arrayOf(
          arrayOf(Pair(4, 2), Pair(3, 0), Pair(2, 1)),
          arrayOf(Pair(4, 1), Pair(3, 2), Pair(2, 0)),
          arrayOf(Pair(4, 0), Pair(2, 2))
  )

  val beat30 = app.beatToFrame(startBeat + 30f).toInt()

  var panelState: List<List<Triple<PVector, PVector, PVector>>> = Array(3, { Array(3, { Triple(PVector(0f, 0f, 0f), PVector(0f, 0f, 0f), PVector(0f, 0f, 0f)) }).toList() }).toList()

  fun draw(app: Main, frame: Int, beat: Float) {
    if(beat !in startBeat - 1f..startBeat + 33f) return
    updateBuffer(app, frame, beat)

    with(app) {
      when {
        beat in startBeat..startBeat + 15f -> {
          image(g1, 0f, 0f)

          val (px, py) = piecePos[2]
          val t = cnorm(beat, startBeat, startBeat + 1f)
          val s = cube1(t)
          pushMatrix()
          translate(640f, 360f)
          rotateZ(PI)
          scale(lerp(0.4f, 1.0f, s))
          translate(-(px + 0.5f) * 1280f, -(py + 0.5f) * 720f)
          drawPieces(app, px, py)
          noStroke()
          fill(alpha(pieceColors[py][px], inv(s)))
          rect(px * 1280f, py * 720f, 1280f, 720f)
          popMatrix()
        }
        beat in startBeat + 15f..startBeat + 16f -> {
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
        beat in startBeat + 16f..startBeat + 26f -> {
          image(g2, 0f, 0f)
        }
        beat in startBeat + 26f..startBeat + 28f -> {
          val (px, py) = piecePos[3]
          val t = cnorm(beat, startBeat + 26f, startBeat + 27.5f)
          val s = cube1(t)
          pushMatrix()
          translate(640f, 360f)
          rotateX(-s * PI)
          scale(lerp(1.0f, 0.4f, s))
          translate(-(px + 0.5f) * 1280f, -(py + 0.5f) * 720f)

          noStroke()
          fill(opaq(pieceColors[py][px]))
          if(s <= 0.5f) {
            image(g2, px * 1280f, py * 720f)
          } else {
            rect(px * 1280f, py * 720f, 1280f, 720f)
          }
          popMatrix()
        }
        beat in startBeat + 28f..startBeat + 30f -> {
          val (px, py) = piecePos[3]
          pushMatrix()
          translate(640f, 360f)
          rotateZ(PI)
          scale(0.4f)
          translate(-(px + 0.5f) * 1280f, -(py + 0.5f) * 720f)

          noStroke()
          fill(opaq(pieceColors[py][px]))
          rect(px * 1280f, py * 720f, 1280f, 720f)

          (0..2).forEach {
            val start = startBeat + 28f + it * 1 / 3f
            if(beat >= start) {
              val t = cnorm(beat, start, start + 1.5f)
              val s = quad1(t)
              blink1[it].forEach { e ->
                val (px2, py2) = e
                noStroke()
                fill(alpha(pieceColorsExpanded[py2 + 1][px2 + 1], inv(s)))
                rect(px2 * 1280f, py2 * 720f, 1280f, 720f)
              }
            }
          }

          (0..2).forEach {
            val start = startBeat + 28f + (it + 4) * 1 / 3f
            if(beat >= start) {
              blink2[it].forEach { e ->
                val (px2, py2) = e
                noStroke()
                fill(opaq(pieceColorsExpanded[py2 + 1][px2 + 1]))
                rect(px2 * 1280f, py2 * 720f, 1280f, 720f)
              }
            }
          }

          popMatrix()
        }
      }
      if(frame == beat30) {
        initPanelState(app)
      }
      if(beat in startBeat + 30f..startBeat + 32f) {
        pushMatrix()
        translate(640f, 360f)
        scale(0.4f)
        (0..2).forEach { iy ->
          (0..2).forEach { ix ->
            val (pos, vel, rot) = panelState[iy][ix]
            pushMatrix()
            translate(pos.x, pos.y, pos.z)
            val dr = frame - beat30
            rotateX(rot.x * dr)
            rotateY(rot.y * dr)
            rotateZ(rot.z * dr)
            noStroke()
            fill(opaq(pieceColorsExpanded[3 - iy][5 - ix]))
            rect(-640f, -360f, 1280f, 720f)
            popMatrix()
          }
        }
        popMatrix()
        panelState = panelState.mapIndexed { iy, ey ->
          ey.mapIndexed { ix, e ->
            val (pos, vel, rot) = e
            val npos = pos.add(vel)
            val nvel = vel.add(PVector(0f, 9f, 0f))
            Triple(npos, nvel, rot)
          }
        }
      }
    }
  }

  fun initPanelState(app: Main) {
    app.randomSeed(236934618L)
    panelState = panelState.mapIndexed { iy, ey -> ey.mapIndexed { ix, e ->
      val pos = PVector((ix - 1) * 1280f, (iy - 1f) * 720f, 0f)
      val vel = PVector(
              app.random((ix - 1) * 50f - 10f, (ix - 1) * 50f + 10f),
              app.random(0f, 0f),
              app.random(-20f, -5f))
      val rot = PVector(
              app.random(-0.1f, 0.1f),
              app.random(-0.1f, 0.1f),
              app.random(-0.1f, 0.1f))
      Triple(pos, vel, rot)
    } }
  }

}

