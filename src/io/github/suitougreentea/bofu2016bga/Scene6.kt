package io.github.suitougreentea.bofu2016bga

import ddf.minim.analysis.FFT
import processing.core.PApplet.*
import processing.core.PConstants
import processing.core.PConstants.*
import processing.core.PGraphics

class Scene6(app: Main): Scene {
  override val startBeat = 4f + 32f + 32f + 32f + 32f + 32f + 32f

  val graph = app.createGraphics(1280, 720)

  val cymbal = arrayOf(
          0f, 2.5f, 4f, 6.5f, 7.25f,
          8f, 10.5f, 12f, 14f, 15f,
          16f, 19f,
          20f, 20.75f, 21.5f, 22.25f, 23f, 23.5f,
          24f, 28f, 30f)
  val kick = arrayOf(
          0f, 0.75f, 2.5f, 4f, 4.75f, 6.5f, 7.25f,
          8f, 8.75f, 10.5f, 12f, 12.75f, 14f, 15f, 15.25f, 15.75f,
          16f, 17f, 18f, 19f, 19.75f,
          20f, 20.75f, 21.5f, 22.25f, 23f, 23.5f,
          24f, 25f, 26f, 26.75f, 27.25f, 27.5f,
          28f, 28.5f, 29.25f, 30f
  )

  override fun draw(app: Main, frame: Int, beat: Float) {
    val bnorm = createBNorm(app, beat)
    val bnorm1 = createBNorm1(app, beat)
    val bin = createBIn(app, beat)

    if(!bin(-1f, 32f + 4f)) return

    fun renderNotes(notes: Array<Triple<Float, Int, Int>>, offset: Int, f: (a: Float) -> Unit) {
      with(app) {
        with(graph) {
          notes.forEach {
            val (b, n, v) = it
            if (bin(b, b + 4f)) {
              val t = bnorm(b, b + 4f)
              //val x = lerp(40f, 1240f, (b / 4f) % 1f)
              val x = -70f
              val y = 380f - (n - 60 - offset) * 15f
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

        rectMode(CENTER)
        cymbal.forEach {
          if(bin(it, it + 2f)) {
            val t = bnorm(it, it + 2f)
            val s = lerp(600f, 1400f, quad1(t))
            val a = inv(quad1(t)) * 0.1f
            val w = lerp(25f, 0f, t)
            noFill()
            stroke(a)
            strokeWeight(w)
            saveMatrix {
              translate(640f, 360f)
              rotate(PI / 4f)
              rect(0f, 0f, s, s)
            }
          }
        }
        rectMode(CORNER)
        Notes6.sax.forEach {
          val (bb, n, v) = it
          val b = bb - 1f
          if(bin(b, b + 1.5f)) {
            val t = bnorm(b, b + 1.5f)
            val a = inv(t) * 0.2f
            saveMatrix {
              translate(0f, 360f - (n - 68) * 20f)
              tint(a)
              image(res.horLine, 0f, -10f)
              tint(1f)
            }
          }
        }

        renderNotes(Notes6.bass, 0) { a ->
          noFill()
          stroke(0.12f * a)
          strokeWeight(2f)
          rect(-13f, -13f, 26f, 26f)
        }
        renderNotes(Notes6.guitar, 0) { a ->
          noFill()
          stroke(0.12f * a)
          strokeWeight(2f)
          rect(-13f, -13f, 26f, 26f)
        }
        renderNotes(Notes6.synth1, 12) { a ->
          noFill()
          stroke(0.12f * a)
          strokeWeight(0.7f)
          rect(-13f, -13f, 26f, 26f)
        }
        renderNotes(Notes6.synth2, 24) { a ->
          noFill()
          stroke(0.12f * a)
          strokeWeight(10f)
          rect(-13f, -13f, 26f, 26f)
        }
        renderNotes(Notes6.piano, 0) { a ->
          noStroke()
          fill(0.12f * a)
          rect(-13f, -13f, 26f, 26f)
        }

        kick.map { bnorm1(it, it + 1f) }.min()?.let {
          val s = quad1(it)
          tint(lerp(0.15f, 0f, s))
          image(res.leftRight, 0f, 0f)
          tint(1f)
        }
      }

      if(bin(3f, 32f)) image(app.bg, 0f, 0f)
      blendMode(ADD)
      image(graph, 0f, 0f)
      blendMode(BLEND)
      if(bin(29f, 34f)) {
        val t = bnorm(29f, 31f)
        noStroke()
        fill(0f, t)
        rect(0f, 0f, 1280f, 720f)
        blendMode(ADD)
        if(bin(29f, 31f)) {
          imageMode(CENTER)
          tint(1f, t)
          image(res.bmson, 640f, 360f)
          tint(1f)
          imageMode(CORNER)
        }
        if(bin(31f, 34f)) {
          val t = bnorm(33f, 34f)
          imageMode(CENTER)
          tint(1f, inv(t))
          image(res.bmson, 640f, 360f)
          tint(1f)
          imageMode(CORNER)
        }
        blendMode(BLEND)
      }
    }
  }
}