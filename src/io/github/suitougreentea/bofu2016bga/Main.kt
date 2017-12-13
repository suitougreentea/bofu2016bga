package io.github.suitougreentea.bofu2016bga

import ddf.minim.Minim
import processing.core.*
import processing.event.KeyEvent
import java.util.*
import kotlin.properties.Delegates
import processing.core.PConstants.*
import processing.opengl.PShader

fun main(args: Array<String>) {
  PApplet.main("io.github.suitougreentea.bofu2016bga.Main")
}

class Main : PApplet() {
  val FRAME_RATE = 30f
  var minim: Minim by Delegates.notNull()
  var res: Res by Delegates.notNull()
  var input: MutableMap<Char, Int> = hashMapOf('[' to 0, ']' to 0)
  var bg: PGraphics by Delegates.notNull()

  var scenes: Array<Scene> by Delegates.notNull()

  val colors = arrayOf(
          Triple(0.3f, 0.8f, 0.8f),
          Triple(0.4f, 0.8f, 0.8f),
          Triple(0.55f, 0.8f, 0.8f),
          Triple(0.7f, 0.8f, 0.8f),
          Triple(0.85f, 0.8f, 0.8f),
          Triple(1f, 0.8f, 0.8f),
          Triple(1.15f, 0.8f, 0.8f),
          Triple(1.15f, 0.8f, 0.8f)
  )

  override fun settings() {
    size(1280, 720, P2D)
  }

  override fun setup() {
    minim = Minim(this)
    res = Res(this, minim)

    colorMode(RGB, 1f, 1f, 1f, 1f)
    frameRate(FRAME_RATE)
    background(0f, 0f, 0f)
    smooth()
    keyRepeatEnabled = false

    scenes = Array(6, { when(it){
      0 -> Scene1(this)
      1 -> Scene2(this)
      2 -> Scene3(this)
      3 -> Scene4(this)
      4 -> Scene5(this)
      5 -> Scene6(this)
      else -> object: Scene {
        override val startBeat = 0f

        override fun draw(app: Main, frame: Int, beat: Float) {}
      }
    }})

    bg = createGraphics(1280, 720)

    res.music.play()

    jump(2f)
    //jump(4f)
    //jump(36f)
    //jump(36f + 32f)
    //jump(36f + 32f + 32f)
    //jump(36f + 32f + 32f + 32f)
    //jump(36f + 32f + 32f + 32f + 32f)
    //jump(36f + 32f + 32f + 32f + 32f + 32f)
  }

  // frame = beat / (beat / min) * (frame / min)
  val speedTable = arrayOf(
          Pair(0f, 122f)
  ).let {
    val result: MutableList<Triple<Float, Float, Float>> = ArrayList()
    var lastStartFrame = 0f
    it.indices.forEach { i ->
      val frame: Float
      if (i == 0) frame = 0f
      else {
        val dBeat = it[i].first - it[i - 1].first
        frame = lastStartFrame + dBeat / it[i - 1].second * FRAME_RATE * 60
        lastStartFrame = frame
      }
      result.add(Triple(it[i].first, it[i].second, frame))
    }
    result.toList()
  }

  var speedTableIndex = 0

  fun beatToFrame(speedData: Triple<Float, Float, Float>, beat: Float): Float {
    val (startBeat, bpm, startFrame) = speedData
    return startFrame + (beat - startBeat) / bpm * FRAME_RATE * 60
  }

  fun beatToFrame(beat: Float) = beatToFrame(getSpeedTableFromBeat(beat), beat)

  // beat = frame / (frame / min) * (beat / min)
  fun frameToBeat(speedData: Triple<Float, Float, Float>, frame: Float): Float {
    val (startBeat, bpm, startFrame) = speedData
    return startBeat + (frame - startFrame) * bpm / (FRAME_RATE * 60)
  }

  var startFrame = 0
  var offset = 0
  var musicWaiting = false
  var musicJump = false

  var px = 0f
  var py = 0f
  var pz = 0f
  var vx = 0f
  var vy = 0f
  var vz = 0f

  fun getSpeedTableIndexFromBeat(beat: Float) = if(beat <= 0f) 0 else speedTable.indexOfLast { it.first < beat }
  fun getSpeedTableFromBeat(beat: Float) = speedTable[getSpeedTableIndexFromBeat(beat)]

  fun jump(beat: Float) {
    speedTableIndex = getSpeedTableIndexFromBeat(beat)
    offset = beatToFrame(speedTable[speedTableIndex], beat).toInt()
    startFrame = frameCount
    musicJump = true
  }

  override fun draw() {
    val frame = offset + frameCount - startFrame
    while (speedTableIndex < speedTable.size - 1 && speedTable[speedTableIndex + 1].third <= frame) speedTableIndex++
    val beat = frameToBeat(speedTable[speedTableIndex], frame.toFloat())
    val idealPosition = (frame / FRAME_RATE * 1000).toInt()
    val diff = res.music.position() - idealPosition

    if(musicJump) {
      res.music.cue(idealPosition)
      musicJump = false
    }
    if(musicWaiting) {
      if (diff <= 0) {
        res.music.play()
        musicWaiting = false
      }
    } else {
      if (diff <= -100) res.music.cue(idealPosition)
      if (diff >= 20) {
        res.music.pause()
        musicWaiting = true
      }
    }

    if(input['['] == 1) {
      jump(Math.max(0f, beat - 4))
    }
    if(input[']'] == 1) {
      jump(beat + 4)
    }

    input.mapValuesTo(input) { if(it.value > 0) it.value + 1 else 0 }

    withGraphics(bg) {
      val fx = cos(frame / 15.4245f * PI) * 80f
      val fy = sin(frame / 12f * PI) * 80f
      val fz = cos(frame / 10.583f * PI + 4f / PI) * 80f
      val ax = (fx - px) / 80f
      val ay = (fy - py) / 80f
      val az = (fz - pz) / 80f
      vx += ax
      vy += ay
      vz += az
      px += vx / 2f
      py += vy / 2f
      pz += vz / 2f

      val i = max(0, ((beat - 4f) / 32f).toInt())
      val b = (beat - 4f) % 32f
      val (h, s, l) = if(b in 0f..3f && i >= 1) {
        val p = colors[i - 1]
        val c = colors[i]
        val t = cnorm(b, 0f, 3f)
        Triple(lerp(p.first, c.first, t), lerp(p.second, c.second, t), lerp(p.third, c.third, t))
      } else colors[i]

      // うごかしたい
      clear()
      colorMode(HSB, 1f)
      noStroke()
      fill((h + px / 7000f) % 1f, s + py / 1800f, l + pz / 2600f)
      rect(0f, 0f, 1280f, 720f)

      blendMode(ADD)
      tint(0.2f)
      pushMatrix()
      iimage(res.gradient, px, py - 280f)
      popMatrix()
      blendMode(BLEND)
      colorMode(RGB, 1f)
    }

    clear()


    // start

    scenes.forEach { it.draw(this, frame, beat) }

    // end

    //hint(DISABLE_DEPTH_TEST)

    /*noStroke()
    fill(1f, 1f, 1f)
    textFont(res.fontSans)
    textSize(12f)
    textAlign(LEFT, BOTTOM)
    text(frameRate.toString(), 10f, 710f)

    textAlign(LEFT, TOP)
    text(arrayOf("${frameCount}", "${speedTableIndex}", "${beat}").joinToString("\n"), 10f, 10f)*/
    saveFrame()
  }

  override fun keyPressed(event: KeyEvent?) {
    super.keyPressed(event)
    event?.key?.let { if(input[it] == 0) input[it] = 1 }
  }

  override fun keyReleased(event: KeyEvent?) {
    super.keyReleased(event)
    event?.key?.let { if(input.containsKey(it)) input[it] = 0 }
  }

  fun cnorm(value: Float, start: Float, stop: Float) = constrain(norm(value, start, stop), 0f, 1f)
  fun norm0(value: Float, start: Float, stop: Float) = if(value in start..stop) norm(value, start, stop) else 0f
  fun norm1(value: Float, start: Float, stop: Float) = if(value in start..stop) norm(value, start, stop) else 1f
  fun cmap(value: Float, start: Float, stop: Float, min: Float, max: Float) = constrain(map(value, start, stop, min, max), min, max)

  fun inv(x: Float) = 1 - x

  fun quad1(x: Float) = -pow(x - 1, 2f) + 1f
  fun cube1(x: Float) = pow(x - 1, 3f) + 1f
  fun pow1(x: Float, p: Float) = -pow(-x + 1 , p) + 1f

  fun opaq(c: Int) = c + (0xFF shl 24)
  fun alpha(c: Int, a: Float) = c + ((a * 0xFF).toInt() shl 24)

  fun iimage(img: PImage, x: Float, y: Float) = image(img, x.toInt().toFloat(), y.toInt().toFloat())

  fun back(cx: Float, cy: Float) {
  }

  fun rrect(x: Float, y: Float, width: Float, height: Float, rad: Float) {
    val x1 = height * sin(rad)
    val y1 = height * cos(rad)
    val x2 = height * sin(rad) + width * cos(rad)
    val y2 = height * cos(rad) + width * sin(rad)
    val x3 = width * cos(rad)
    val y3 = -height * sin(rad)
    quad(x, y, x + x1, y + y1, x + x2, y + y2, x + x3, y + y3)
  }

  fun rotateAll(x: Float, y: Float, z: Float) {
    rotateX(x)
    rotateY(y)
    rotateZ(z)
  }

  fun rotdp(t: Float) = if(t <= 0.5f) t * PI else -PI + t * PI
  fun rotdm(t: Float) = if(t <= 0.5f) t * -PI else PI - t * PI

  inline fun <R> withGraphics(receiver: PGraphics, f: PGraphics.() -> R) {
    receiver.beginDraw()
    receiver.f()
    receiver.endDraw()
  }

  inline fun <R> saveMatrix(f: () -> R) {
    pushMatrix()
    f()
    popMatrix()
  }
}

fun PGraphics.iimage(img: PImage, x: Float, y: Float) {
  this.image(img, x.toInt().toFloat(), y.toInt().toFloat())
}

inline fun <R> PGraphics.saveMatrix(f: () -> R) {
  this.pushMatrix()
  f()
  this.popMatrix()
}

