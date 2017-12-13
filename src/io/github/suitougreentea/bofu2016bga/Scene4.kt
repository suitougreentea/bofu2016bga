package io.github.suitougreentea.bofu2016bga

import ddf.minim.analysis.FFT
import processing.core.PApplet.*
import processing.core.PConstants
import processing.core.PConstants.*
import processing.core.PGraphics

class Scene4(app: Main): Scene {
  override val startBeat: Float = 4f + 32f + 32f + 32f

  val size = 30f
  val sizeB = 20f
  val sizeC = 10f
  val row = 18
  val col = 32
  val gridWidth = 1280 / col
  val gridHeight = 720 / row

  val state = Array(row, { Array(col, { 0f }).toMutableList() }).toMutableList()

  var lastBeat = 0f

  val cymbal = arrayOf(30.5f, 31.25f, 32f)

  init {
    app.randomSeed(2450968L)
  }
  var start = Array(col, { app.random(0f, col.toFloat()).toInt() })
  val order = Array(row, { iy -> Array(col, { ix -> (iy + start[ix]) % row } )})

  val order2 = Array(col, { iy -> (0..8).toList().shuffle() })

  val fft = FFT(app.res.music.bufferSize(), app.res.music.sampleRate())
  var fftMax = Array(col, { 0f }).toList()

  val fftL = FFT(app.res.music.bufferSize(), app.res.music.sampleRate())
  var fftMaxL = Array(col, { 0f }).toList()
  val fftR = FFT(app.res.music.bufferSize(), app.res.music.sampleRate())
  var fftMaxR = Array(col, { 0f }).toList()

  val graph = app.createGraphics(1280, 720)

  var index = 0

  val average = arrayOf(
          3944.2437, 3944.2437, 4621.1104, 4621.1104, 4621.1104, 2985.9526, 1945.108, 1945.108, 1493.6637, 1637.8477, 1560.0629, 1848.4485, 1358.9857, 1074.6313, 1286.1226, 562.02155, 357.7059, 447.30417, 228.8722, 196.65678, 179.603, 111.19456, 80.996994, 62.267395, 52.727463, 49.38289, 46.782593, 43.86639, 32.837612, 30.741127, 25.830946, 12.113524
  ).map { (it / 64f).toFloat() }

  override fun draw(app: Main, frame: Int, beat: Float) {
    val bnorm = createBNorm(app, beat)
    val bin = createBIn(app, beat)
    val half = (lastBeat / 0.5f).toInt() != (beat / 0.5f).toInt()
    lastBeat = beat

    if(!bin(0f, 64f + 3f)) return

    with(app) {
      withGraphics(graph) { clear() }
      if(frame == beatToFrame(startBeat).toInt() + 1) {
        index = 0
      }

      if(bin(0f, 32f)) {
        fft.forward(res.music.mix)
        fftMax = (0..col - 1).map { max(fftMax[it], fft.getFreq(50f * exp((it / col.toFloat()) * log(20000f / 50f)))) }

        if (half) {
          fftMax.forEachIndexed { i, e ->
            state[order[index][i]][i] = min(e / (average[i] * 1.5f), 1f)
          }
          index = (index + 1) % row
          fftMax = fftMax.map { 0f }
        }
      }
      if(bin(32f, 64f)) {
        fftL.forward(res.music.left)
        fftMaxL = (0..col - 1).map { max(fftMaxL[it], fftL.getFreq(50f * exp((it / col.toFloat()) * log(20000f / 50f)))) }
        fftR.forward(res.music.right)
        fftMaxR = (0..col - 1).map { max(fftMaxR[it], fftR.getFreq(50f * exp((it / col.toFloat()) * log(20000f / 50f)))) }

        if (half) {
          if(index >= 9) index = 0
          fftMaxL.forEachIndexed { i, e ->
            state[order2[i][index]][i] = min(e / (average[i] * 1.5f), 1f)
          }
          fftMaxR.forEachIndexed { i, e ->
            state[row - 1 - order2[i][index]][col - 1 - i] = min(e / (average[i] * 1.5f), 1f)
          }
          index = (index + 1) % (row / 2)
          fftMaxL = fftMaxL.map { 0f }
          fftMaxR = fftMaxR.map { 0f }
        }

      }

      if(bin(31f, 64f)) {
        withGraphics(graph) {
          colorMode(RGB, 1f)
          blendMode(ADD)
          Notes4.sax.forEach {
            val (b, h, v) = it
            val t = bnorm(31f + b, 31f + b + 1f)

            if(0f < t && t < 1f) {
              pushMatrix()
              translate(0f, 360f - (h - 68) * 20f)
              //stroke(lerp(0.2f, 0f, t) * (v / 127f))
              tint(lerp(0.2f, 0f, t) * (v / 127f))
              //strokeWeight(5f)
              //noFill()
              //line(0f, 0f, 1280f, 0f)
              image(res.horLineSmall, 0f, -5f)
              tint(1f)
              popMatrix()
            }
          }
        }
      }

      if(bin(0f, 67f)) {
        withGraphics(graph) {
          rectMode(CENTER)
          colorMode(RGB, 1f)
          blendMode(ADD)
          if(bin(0f, 64f)) {
            val t = beat % 1f
            val s = quad1(t)
            tint(lerp(0.15f, 0f, s))
            image(res.leftRight, 0f, 0f)
            tint(1f)
          }

          state.forEachIndexed { iy, ey ->
            ey.forEachIndexed { ix, e ->
              val s = quad1(e) * size
              val s2 = quad1(e) * sizeB
              val s3 = quad1(e) * sizeC
              pushMatrix()
              translate(640f, 360f)
              scale(1.1f)
              translate(-640f, -360f)
              val cx = (ix + 0.5f) * gridWidth
              val cy = (iy + 0.5f) * gridHeight
              noStroke()
              fill(0.05f)
              rect(cx, cy, s, s)
              fill(0.05f)
              rect(cx, cy, s2, s2)
              fill(0.05f)
              rect(cx, cy, s3, s3)
              popMatrix()

              state[iy][ix] = max(0f, e - 0.02f)
            }
          }

          cymbal.forEachIndexed { i, b ->
            if(bin(b, b + 2f)) {
              val t = bnorm(b, b + 2f)
              val s = cube1(t) * if(i == 2) 1200f else 800f
              val a = inv(t) * if(i == 2) 0.25f else 0.15f
              val w = inv(t) * if(i == 2) 50f else 20f
              saveMatrix {
                translate(640f, 360f)
                rotate(PI/4)
                noFill()
                stroke(a)
                strokeWeight(w)
                rect(0f, 0f, s, s)
              }
            }
          }

          rectMode(CORNER)
          blendMode(BLEND)
        }

        if(bin(3f, 67f)) image(app.bg, 0f, 0f)
        blendMode(ADD)
        if(bin(0f, 3f)) {
          tint(1f, 1f, 1f, bnorm(0f, 3f))
        }
        if(bin(64f, 67f)) {
          tint(1f, 1f, 1f, inv(bnorm(64f, 67f)))
        }
        image(graph, 0f, 0f)
        tint(1f)
        blendMode(BLEND)
      }
    }
  }
}

fun <E> List<E>.shuffle(): List<E> {
  if(this.size == 1) return this
  val index = (Math.random() * this.size).toInt()
  return listOf(this[index]) + this.filterIndexed { i, e -> i != index }.shuffle()
}