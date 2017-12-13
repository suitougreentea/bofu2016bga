package io.github.suitougreentea.bofu2016bga

import ddf.minim.analysis.FFT
import processing.core.PApplet.*
import processing.core.PConstants
import processing.core.PConstants.*
import processing.core.PGraphics

class Scene2(app: Main): Scene {
  override val startBeat = 36f

  val col = 40
  val fft = FFT(app.res.music.bufferSize(), app.res.music.sampleRate())
  val state = Array(col, { 0f }).toMutableList()
  val average = arrayOf(
          3166.9336, 3166.9336, 4661.7144, 4661.7144, 4661.7144, 4661.7144, 2980.7876, 2980.7876, 1737.2882, 1737.2882, 1921.398, 1979.3142, 2090.9055, 2092.2158, 2569.0913, 4612.6255, 852.99585, 805.60236, 437.22314, 656.677, 612.6237, 344.47433, 184.42592, 210.88548, 127.73367, 120.30445, 96.05319, 75.48348, 85.3911, 48.798298, 47.724876, 43.7009, 31.446964, 28.391771, 26.407085, 22.272476, 23.391443, 20.225485, 13.1204405, 9.673673
  ).map { (it / 240f).toFloat() }

  val beatList = (0..27).map { it.toFloat() } + listOf(28f, 29f, 30f, 30.75f, 31f, 31.5f)

  val graph = app.createGraphics(1280, 720)

  override fun draw(app: Main, frame: Int, beat: Float) {
    val bnorm = createBNorm(app, beat)
    val bin = createBIn(app, beat)

    if(!bin(0f, 32f + 3f)) return

    with(app) {
      fft.forward(res.music.mix)
      state.forEachIndexed { i, e ->
        val n = fft.getFreq(50f * exp((i / col.toFloat()) * log(20000f / 50f)))
        state[i] = max(e - 0.1f, map(constrain(n / average[i], 2f, 10f), 2f, 10f, 0f, 12f))
      }

      withGraphics(graph) {
        clear()
        colorMode(RGB, 1f)
        blendMode(ADD)

        saveMatrix {
          translate(640f, 720f)
          rectMode(CENTER)
          state.forEachIndexed { i, e ->
            saveMatrix {
              rotate(lerp(-PI / 2.2f, PI / 2.2f, i / (col - 1).toFloat()))
              noStroke()
              (0..e.toInt()).forEach {
                val a = constrain(e - it, 0f, 1f)
                fill(0.1f * a)
                rect(0f, -400f - it * 30f, 20f, 20f)
                rect(0f, -400f - it * 30f, 14f, 14f)
              }
            }
          }
          rectMode(CORNER)

          ellipseMode(CENTER)
          beatList.forEach {
            if(bin(it, it + 1.5f)) {
              val t = bnorm(it, it + 1.5f)
              val r = lerp(370f * 2f, 300f * 2f, t)
              val a = inv(t)
              val w = inv(t) * 5f
              stroke(a * 0.2f)
              strokeWeight(w)
              noFill()
              ellipse(0f, 0f, r, r)
            }
          }

          val t = (beat + 0.5f) % 1f
          val a = lerp(0.1f, 0f, t)
          noStroke()
          fill(a)
          ellipse(0f, 0f, 270f * 2f, 270f * 2f)
        }

        if(bin(31f, 33f)) {
          val t = bnorm(31f, 33f)
          val s = cube1(t) * 1600f
          val w = t * 30f
          val a = inv(t) * 0.2f
          noFill()
          stroke(a)
          strokeWeight(w)
          ellipse(640f, 720f, s, s)
        }
      }

      if(bin(3f, 35f)) image(app.bg, 0f, 0f)
      blendMode(ADD)
      if(bin(0f, 3f)) {
        tint(1f, 1f, 1f, bnorm(0f, 3f))
      }
      if(bin(32f, 35f)) {
        tint(1f, 1f, 1f, inv(bnorm(32f, 35f)))
      }
      image(graph, 0f, 0f)
      tint(1f)
      blendMode(BLEND)
    }
  }
}