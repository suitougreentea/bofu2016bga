package io.github.suitougreentea.bofu2016bga

import processing.core.PApplet.*
import processing.core.PConstants
import processing.core.PConstants.*
import processing.core.PGraphics

class Scene3(app: Main): Scene {
  override val startBeat = 4f + 32f + 32f

  val wid = 60f

  val hei = 52f

  val column = 32

  val states = Array(5, {
    when(it) {
      0 -> State(Notes3.sax)
      1 -> State(Notes3.bass)
      2 -> State(Notes3.piano)
      3 -> State(Notes3.guitar1)
      4 -> State(Notes3.guitar2)
      else -> throw IllegalStateException()
    }
  })

  val cymbal = arrayOf(0f, 15f, 16f, 27f, 28f, 30f)
  val kick = (0..5).map { i -> listOf(0f, 0.75f, 1.5f, 2.5f, 3.25f).map { it + i * 4f } }.flatten() +
          listOf(0f, 0.75f, 1.5f, 2.5f, 3.25f, 3.5f, 4f, 4.3333333f, 4.66666666f, 5.3333333f, 5.6666666f, 6f).map { it + 24f }
  val brush = (0..13).map { i -> i * 2f + 1f } + listOf(0f, 0.3333333f, 0.666666666f, 1.333333333f, 1.666666666f, 2f).map { it + 28f }

  val graph = app.createGraphics(1280, 720)

  var triState = Array(15, { Array(23, { mutableListOf(0f, 0f, 0f) } ) })

  override fun draw(app: Main, frame: Int, beat: Float) {
    val bnorm = createBNorm(app, beat)
    val bnorm1 = createBNorm1(app, beat)
    val bin = createBIn(app, beat)

    if(!bin(0f, 32f + 3f)) return

    with(app) {
      if (frame == beatToFrame(startBeat).toInt() + 1) {
        states.forEach { it.init() }
        triState = Array(15, { Array(23, { mutableListOf(0f, 0f, 0f) } ) })
      }

      withGraphics(graph) {
        clear()
        colorMode(RGB, 1f)
        rectMode(CENTER)
        ellipseMode(CENTER)
        blendMode(ADD)
        states.forEach {
          it.update(beat - startBeat)
        }

        states[0].state.forEachIndexed { ix, e ->
          if(e > 0f) {
            val (px, py) = transform2(ix, 48)
            triState[5 + py][px][random(3f).toInt()] = e
          }
        }
        states[1].state.forEachIndexed { ix, e ->
          if(e > 0f) {
            val (px, py) = transform2(ix, 30)
            triState[10 + py][px][random(3f).toInt()] = e
          }
        }
        states[2].state.forEachIndexed { ix, e ->
          if(e > 0f) {
            val (px, py) = transform2(ix, 50)
            triState[3 + py][px][random(3f).toInt()] = e
          }
        }
        states[3].state.forEachIndexed { ix, e ->
          if(e > 0f) {
            val (px, py) = transform2(ix, 28)
            triState[7 + py][px][random(3f).toInt()] = e
          }
        }
        states[4].state.forEachIndexed { ix, e ->
          if(e > 0f) {
            val (px, py) = transform2(ix, 40)
            triState[8 + py][px][random(3f).toInt()] = e
          }
        }


        stroke(1f)
        strokeWeight(5f)
        noFill()

        pushMatrix()
        translate(640f, 360f)
        scale(1.3f)
        translate(-640f, -360f)

        imageMode(CENTER)
        (0..22).forEach { ix ->
          (0..14).forEach { iy ->
            val e = triState[iy][ix]
            pushMatrix()
            if(iy % 2 == 0) translate(ix * wid, iy * hei)
            else translate((ix + 0.5f) * wid, iy * hei)

            tint(0.3f * e[0])
            if(e[0] > 0f) image(res.tri1, 0f, 0f)
            tint(0.3f * e[1])
            if(e[1] > 0f) image(res.tri2, 0f, 0f)
            tint(0.3f * e[2])
            if(e[2] > 0f) image(res.tri3, 0f, 0f)
            popMatrix()
          }
        }
        tint(1f)
        imageMode(CORNER)
        popMatrix()

        kick.forEach {
          if(bin(it, it + 1f)) {
            val t = bnorm(it, it + 1f)
            val y = lerp(60f, -20f, quad1(t))
            val a = inv(t) * 0.05f
            val w = lerp(25f, 0f, t)
            noFill()
            stroke(a)
            strokeWeight(w)
            line(0f, y, 1280f, y)
            line(0f, 720 - y, 1280f, 720 - y)
          }
        }

        cymbal.forEach {
          if(bin(it, it + 2f)) {
            val t = bnorm(it, it + 2f)
            val s = quad1(t) * 800f
            val a = inv(t) * 0.1f
            val w = lerp(10f, 0f, t)
            noFill()
            stroke(a)
            strokeWeight(w)
            ellipse(640f, 360f, s, s)
          }
        }

        brush.map { bnorm1(it, it + 2f) }.min()?.let {
          val s = quad1(it)
          tint(lerp(0.15f, 0f, s))
          image(res.leftRight, 0f, 0f)
          tint(1f)
        }

        triState.forEachIndexed { i, ey ->
          ey.forEachIndexed { i, e ->
            e[0] = max(e[0] - 0.01f, 0f)
            e[1] = max(e[1] - 0.01f, 0f)
            e[2] = max(e[2] - 0.01f, 0f)
          }
        }
      }

      if(bin(3f, 35f)) image(bg, 0f, 0f)
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

  fun transform(noteNum: Int, offset: Int) = max(0, min(22, noteNum - offset))

  fun transform2(noteNum: Int, offset: Int): Pair<Int, Int> {
    val a = max(0, min(45, noteNum - offset))
    if(a % 2 == 0) return Pair(a / 2, 0)
    return Pair(a / 2, 1)
  }
}

class State(val data: Array<Triple<Float, Int, Int>>) {
  var state = Array(128, { 0f }).toMutableList()
  var cursor = 0

  fun init() {
    cursor = 0
  }

  fun update(beat: Float) {
    state.forEachIndexed { i, e -> state[i] = 0f }
    val newCursor = data.indexOfFirst { beat < it.first }
    if(newCursor > cursor) {
      (cursor..newCursor - 1).forEach {
        val e = data[it]
        state[e.second] = e.third / 127f
      }
    }
    cursor = newCursor
  }
}