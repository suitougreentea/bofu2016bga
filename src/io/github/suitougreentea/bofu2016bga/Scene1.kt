package io.github.suitougreentea.bofu2016bga

import processing.core.PApplet.*
import processing.core.PConstants
import processing.core.PConstants.*
import processing.core.PGraphics
import java.util.*

class Scene1(app: Main): Scene {
  override val startBeat = 4f
  val circle = arrayOf(0f, 2f, 4f, 6f, 7f, 8f, 10f, 12f, 12.75f, 13.5f, 14.5f, 15.25f)
  // Offset +16f
  val circle2 = arrayOf(0f, 2f, 4f, 5.5f, 7f, 8f, 10f)
  val randCircle = Random(124509L)
  val circleData = circle.map {
    Pair(Pair(randCircle.nextFloat() * 880f + 200f, randCircle.nextFloat() * 420f + 150f),
            Triple(randCircle.nextFloat(), lerp(0.3f, 0.8f, randCircle.nextFloat()), lerp(0.5f, 1f, randCircle.nextFloat())))
  }
  val circleData2 = circle2.map { Pair(randCircle.nextFloat() * 880f + 200f, randCircle.nextFloat() * 420f + 150f) }

  // Offset +16f
  val notes = arrayOf(
          Pair(0f, 7),
          Pair(2f, 5),
          Pair(3f, 10),
          Pair(4f, 2),
          Pair(5.5f, 3),
          Pair(6.5f, 5),
          Pair(7f, 7),
          Pair(7.5f, 3),
          Pair(8f, 0),
          Pair(9.25f, -5),
          Pair(9.5f, -3),
          Pair(9.75f, 3),
          Pair(10f, 3),
          Pair(11f, 2),
          Pair(12f, 3)
  )

  // Offset +28f
  val triple = arrayOf(0f, 0.75f, 1.5f)

  // Offset +28f
  val kick = arrayOf(2.75f, 3f, 3.5f)


  val graph = app.createGraphics(1280, 720)

  override fun draw(app: Main, frame: Int, beat: Float) {
    val bnorm = createBNorm(app, beat)
    val bnorm0 = createBNorm0(app, beat)
    val bnorm1 = createBNorm1(app, beat)
    val bin = createBIn(app, beat)

    if(!bin(-4f, 32f + 3f)) return

    with(app) {

      withGraphics(graph) {
        clear()
        ellipseMode(RADIUS)
        strokeWeight(4f)
        colorMode(RGB, 1f)
        noFill()
        blendMode(ADD)
        if(bin(16.5f, 35f)) {
          val t = (beat + 0.5f) % 1f
          val s = quad1(t)
          val w = inv(t) * 50f

          noStroke()
          fill(lerp(0.3f, 0f, t))

          pushMatrix()
          translate(s * 360f, 360f)

          pushMatrix()
          shearX(PI / 4f)
          rect(0f, 0f, -w, -360f)
          popMatrix()

          pushMatrix()
          shearX(-PI / 4f)
          rect(0f, 0f, -w, 360f)
          popMatrix()

          popMatrix()

          pushMatrix()
          translate(1280f - s * 360f, 360f)

          pushMatrix()
          shearX(-PI / 4f)
          rect(0f, 0f, w, -360f)
          popMatrix()

          pushMatrix()
          shearX(PI / 4f)
          rect(0f, 0f, w, 360f)
          popMatrix()

          popMatrix()

        }

        kick.map { bnorm1(it + 28f, it + 28f + 1f) }.min()?.let { t ->
          if(t > 0f) {
            val s = quad1(t)
            tint(lerp(1f, 0f, s))
            image(res.leftRight, 0f, 0f)
            tint(1f)

          }
        }

        notes.forEach {
          val (b, h) = it
          if(bin(b + 16f, b + 16f + 1.5f)) {
            val t = bnorm(b + 16f, b + 16f + 1.5f)
            pushMatrix()
            translate(0f, 420f - h * 40f)
            tint(lerp(0.6f, 0f, t))
            image(res.horLine, 0f, -10f)
            popMatrix()
          }
        }
        tint(1f)

        circle2.forEachIndexed { i, b ->
          if(bin(b + 16f, b + 16f + 2f)) {
            val t = bnorm(b + 16f, b + 16f + 2f)
            val s = quad1(t) * 250f
            val (ox, oy) = circleData2[i]
            fill(inv(t) * 0.4f)
            stroke(inv(t) * 0.8f)
            strokeWeight(10f * inv(t))
            ellipse(ox, oy, s, s)
          }
        }

        triple.forEachIndexed { i, it ->
          val t = bnorm0(it + 28f, it + 28f + 2f)
          val s = quad1(t) * 200f
          (-1..1).forEach {
            noFill()
            stroke(inv(t), 0.3f)
            strokeWeight(10f)
            ellipse(640f + it * 320f, 360f + (i - 1) * 240f, s, s)

            stroke(inv(t), 0.3f)
            strokeWeight(6f)
            ellipse(640f + it * 320f, 360f + (i - 1) * 240f, s, s)
            stroke(inv(t), 0.3f)

            stroke(inv(t), 0.3f)
            fill(inv(t), 0.8f)
            strokeWeight(4f)
            ellipse(640f + it * 320f, 360f + (i - 1) * 240f, s, s)
          }
        }
        blendMode(BLEND)
      }

      colorMode(RGB, 1f)
      if(bin(0f, 35f)) image(app.bg, 0f, 0f)

      blendMode(ADD)
      tint(0.3f)
      if(bin(32f, 35f)) {
        tint(0.3f, inv(bnorm(32f, 35f)))
      }
      image(graph, 0f, 0f)

      blendMode(BLEND)
      tint(1f)

      val ft = bnorm(16f, 18f)
      noStroke()
      fill(0f, 0f, 0f, inv(ft))
      rect(0f, 0f, 1280f, 720f)

      if(bin(0f, 18f)) {
        ellipseMode(RADIUS)
        colorMode(HSB, 1f)
        strokeWeight(4f)
        noFill()
        circle.forEachIndexed { i, b ->
          val t = bnorm(b, b + 2f)
          val r = quad1(t) * 200f
          val a = 1f - t
          val (pos, col) = circleData[i]
          stroke(col.first, col.second, col.third, a)
          ellipse(pos.first, pos.second, r, r)
        }
        colorMode(RGB, 1f)
      }

      if(bin(-1f, 5f)) {
        val a = if(bin(-1f, 0f)) bnorm(-1f, 0f) else if(bin(4f, 5f)) inv(bnorm(4f, 5f)) else 1f
        noStroke()
        fill(1f, 1f, 1f, a)
        textAlign(CENTER)
        textFont(res.font)
        textSize(96f)
        text("Piece of Mine", 640f, 320f)
        textSize(48f)
        text("Greentea", 640f, 420f)
      }
    }
  }
}