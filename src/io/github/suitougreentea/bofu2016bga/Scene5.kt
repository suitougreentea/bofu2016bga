package io.github.suitougreentea.bofu2016bga

import ddf.minim.analysis.FFT
import processing.core.PApplet.*
import processing.core.PConstants
import processing.core.PConstants.*
import processing.core.PGraphics

class Scene5(app: Main): Scene {
  override val startBeat = 4f + 32f + 32f + 32f + 32f + 32f

  val cymbal = arrayOf(0f, 16f, 30.25f, 31f)

  val graph = app.createGraphics(1280, 720)
  init {
    app.noiseSeed(134698L)
  }

  override fun draw(app: Main, frame: Int, beat: Float) {
    val bnorm = createBNorm(app, beat)
    val bnorm1 = createBNorm1(app, beat)
    val bin = createBIn(app, beat)

    if(!bin(0f, 32f + 3f)) return

    fun renderNotes(notes: Array<Triple<Float, Int, Int>>, f: (a: Float) -> Unit) {
      with(app) {
        with(graph) {
          notes.forEach {
            val (b, n, v) = it
            if (bin(b, b + 4f)) {
              val t = bnorm(b, b + 4f)
              //val x = lerp(40f, 1240f, (b / 4f) % 1f)
              val x = -70f
              val y = 380f - (n - 60) * 15f
              val a = inv(quad1(t))
              saveMatrix {
                translate(x + noise(b + n + v) * 1600f, y)
                scale(pow1(v / 127f, 0.1f) * 24f)
                f(a)
              }
            }
          }
        }
      }
    }

    with(app) {
      withGraphics(graph) {
        clear()
        colorMode(RGB, 1f)
        blendMode(ADD)
        ellipseMode(CENTER)

        if (bin(0f, 32f)) {
          Notes5.kick.map { bnorm1(it, it + 1f) }.min()?.let { t ->
            val s = quad1(t)
            tint(lerp(0.15f, 0f, s))
            image(res.leftRight, 0f, 0f)
            tint(1f)
          }
        }

        cymbal.forEach {
          if(bin(it, it + 2.5f)) {
            val t = bnorm(it, it + 2.5f)
            val s = cube1(t) * 800f
            val a = inv(t)
            noStroke()
            fill(0.1f * a)
            ellipse(640f, 360f, s, s)
          }
        }

        strokeWeight(2f)
        renderNotes(Notes5.bass) { a ->
          noFill()
          stroke(0.12f * a)
          rect(-13f, -13f, 26f, 26f)
        }
        renderNotes(Notes5.guitar1) { a ->
          noFill()
          stroke(0.12f * a)
          rect(-13f, -13f, 26f, 26f)
        }
        renderNotes(Notes5.guitar2) { a ->
          noStroke()
          fill(0.12f * a)
          rect(-13f, -13f, 26f, 26f)
        }
        renderNotes(Notes5.piano) { a ->
          noStroke()
          fill(0.12f * a)
          rect(-13f, -13f, 26f, 26f)
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