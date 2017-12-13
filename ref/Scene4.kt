package io.github.suitougreentea.bofu2016bga

import processing.core.PApplet.*
import processing.core.PConstants.*
import processing.core.PGraphics
import processing.core.PVector
import java.util.*

class Scene4(app: Main) : Scene2345(app, 2) {
  val pieceSize = 80f
  val piecesWidth = 16
  val piecesHeight = 9
  // 0: / 1:∖
  var pieceType = Array(piecesHeight, { Array(piecesWidth, { 0 }) })
  var vertexPosOffset = Array(piecesHeight + 1, { Array(piecesWidth + 1, { Pair(0f, 0f) } ) })
  var pieceState = Array(piecesHeight, { Array(piecesWidth, {
    Pair(
            Triple(PVector(0f, 0f, 0f), PVector(0f, 0f, 0f), PVector(0f, 0f, 0f)),
            Triple(PVector(0f, 0f, 0f), PVector(0f, 0f, 0f), PVector(0f, 0f, 0f))
    ) } ).toList() }).toList()

  val rectSize = 40f
  val rectsWidth = 32
  val rectsHeight = 18
  val rectFromRad: Array<Array<Float>>
  init {
    app.randomSeed(130495672834576L)
    rectFromRad = Array(rectsHeight, { Array(rectsWidth, { app.random(0f, PI * 2) }) })
  }
  val rectTiming: Array<Array<Int>>
  init {
    app.randomSeed(34598678942576L)
    rectTiming = Array(rectsHeight, { Array(rectsWidth, { round(app.random(1f)) }) })
  }

  val beat0 = app.beatToFrame(startBeat + 0f).toInt() + 1

  override val scroll1 = Pair(startBeat + 0f, startBeat + 16.5f)
  override val scroll2 = Pair(startBeat + 15.5f, startBeat + 32f)

  fun draw(app: Main, frame: Int, beat: Float) {
    if(beat !in startBeat - 1f..startBeat + 33f) return
    updateBuffer(app, frame, beat)

    with(app) {
      if(frame == beat0) {
        initPieces(app)
      }
      when {
        beat in startBeat..startBeat + 4f -> {
          pushMatrix()
          translate(640f, 360f, 0f)
          translate(0f, 0f, -500f)
          scale(1.805f)
          translate(-640f, -360f, 0f)
          image(g1, 0f, 0f)
          popMatrix()
          pieceState.forEachIndexed { iy, ey -> ey.forEachIndexed { ix, e ->
            val (s1, s2) = e
            val (p1, v1, r1) = s1
            val (p2, v2, r2) = s2
            val rf = frame - beat0
            noStroke()
            fill(0f, 0f, 0f)
            val h = pieceSize / 2
            val xtl = -h + vertexPosOffset[iy][ix].first
            val ytl = -h + vertexPosOffset[iy][ix].second
            val xtr = +h + vertexPosOffset[iy][ix+1].first
            val ytr = -h + vertexPosOffset[iy][ix+1].second
            val xbl = -h + vertexPosOffset[iy+1][ix].first
            val ybl = +h + vertexPosOffset[iy+1][ix].second
            val xbr = +h + vertexPosOffset[iy+1][ix+1].first
            val ybr = +h + vertexPosOffset[iy+1][ix+1].second
            if(pieceType[iy][ix] == 0) {
              pushMatrix()
              translate(p1.x, p1.y, p1.z)
              translate(-pieceSize / 5f, -pieceSize / 5f, 0f)
              rotateAll(r1.x * rf, r1.y * rf, r1.z * rf)
              translate(pieceSize / 5f, pieceSize / 5f, 0f)
              triangle(xtl, ytl, xbl, ybl, xtr, ytr)
              popMatrix()

              pushMatrix()
              translate(p2.x, p2.y, p2.z)
              translate(pieceSize / 5f, pieceSize / 5f, 0f)
              rotateAll(r2.x * rf, r2.y * rf, r2.z * rf)
              translate(-pieceSize / 5f, -pieceSize / 5f, 0f)
              triangle(xtr, ytr, xbl, ybl, xbr, ybr)
              popMatrix()
            }
            if(pieceType[iy][ix] == 1) {
              pushMatrix()
              translate(p1.x, p1.y, p1.z)
              translate(pieceSize / 5f, -pieceSize / 5f, 0f)
              rotateAll(r1.x * rf, r1.y * rf, r1.z * rf)
              translate(-pieceSize / 5f, pieceSize / 5f, 0f)
              triangle(xtl, ytl, xbr, ybr, xtr, ytr)
              popMatrix()

              pushMatrix()
              translate(p2.x, p2.y, p2.z)
              translate(-pieceSize / 5f, pieceSize / 5f, 0f)
              rotateAll(r2.x * rf, r2.y * rf, r2.z * rf)
              translate(pieceSize / 5f, -pieceSize / 5f, 0f)
              triangle(xtl, ytl, xbl, ybl, xbr, ybr)
              popMatrix()
            }
          } }
          pieceState = pieceState.mapIndexed { iy, ey ->
            ey.mapIndexed { ix, e ->
              val (s1, s2) = e
              val (p1, v1, r1) = s1
              val (p2, v2, r2) = s2
              val np1 = p1.add(v1)
              val np2 = p2.add(v2)
              val nv1 = v1.add(PVector(0f, 5f, 0f))
              val nv2 = v2.add(PVector(0f, 5f, 0f))
              Pair(Triple(np1, nv1, r1), Triple(np2, nv2, r2))
            }
          }
        }
        beat in startBeat + 4f..startBeat + 15.5f -> {
          image(g1, 0f, 0f)
        }
        beat in startBeat + 15.5f..startBeat + 16.5f -> {
          val t = cnorm(beat, startBeat + 15.5f, startBeat + 16.5f)
          val s = cube1(t)
          pushMatrix()
          translate(0f, -s * 720f)
          image(g1, 0f, 0f)
          translate(0f, 720f)
          image(g2, 0f, 0f)
          popMatrix()
        }
        beat in startBeat + 16.5f..startBeat + 32f -> {
          image(g2, 0f, 0f)

          val (px, py) = piecePos[6]
          noStroke()
          fill(opaq(pieceColors[py][px]))
          (0..rectsHeight-1).forEach { iy ->
            (0..rectsWidth-1).forEach { ix ->
              val t: Float
              if(rectTiming[iy][ix] == 0) {
                t = cnorm(beat, startBeat + 30.5f, startBeat + 31.25f)
              } else {
                t = cnorm(beat, startBeat + 31.25f, startBeat + 32f)
              }
              val s = cube1(t)
              val a = 1280f * 1.732f
              val b = 720f * 1.732f
              val rad = rectFromRad[iy][ix]
              val sx = a * cos(rad) - rectSize / 2f
              val sy = b * sin(rad) - rectSize / 2f
              val ex = ix * rectSize
              val ey = iy * rectSize
              val x = lerp(sx, ex, s)
              val y = lerp(sy, ey, s)
              rect(x, y, rectSize, rectSize)
            }
          }
        }
      }
    }
  }

  fun initPieces(app: Main) {
    val dist = pieceSize / 3
    val distr = 0.05f
    app.randomSeed(3560897349L)
    pieceType = Array(piecesHeight, { Array(piecesWidth, { round(app.random(1f)) }) })
    vertexPosOffset = Array(piecesHeight + 1, { iy -> Array(piecesWidth + 1, { ix ->
      val x = if(ix == 0 || ix == piecesWidth) 0f else app.random(-dist, dist)
      val y = if(iy == 0 || iy == piecesHeight) 0f else app.random(-dist, dist)
      Pair(x, y)
    } ) })
    pieceState = Array(piecesHeight, { iy -> Array(piecesWidth, { ix ->
      val vx = (ix - (piecesWidth / 2f)) * 20f / (piecesWidth / 2f)
      Pair(
              Triple(PVector((ix + 0.5f) * pieceSize, (iy + 0.5f) * pieceSize, 0f), PVector(vx, app.random(-50f, 20f), app.random(-20f, 20f)), PVector(app.random(-distr, distr), app.random(-distr, distr), app.random(-distr, distr))),
              Triple(PVector((ix + 0.5f) * pieceSize, (iy + 0.5f) * pieceSize, 0f), PVector(vx, app.random(-50f, 20f), app.random(-20f, 20f)), PVector(app.random(-distr, distr), app.random(-distr, distr), app.random(-distr, distr)))
      ) } ).toList() }).toList()
  }
}
