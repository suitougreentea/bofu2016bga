package io.github.suitougreentea.bofu2016bga

import ddf.minim.Minim
import processing.core.*
import processing.event.KeyEvent
import java.util.*
import kotlin.properties.Delegates
import processing.core.PConstants.*

fun main(args: Array<String>) {
  PApplet.main("io.github.suitougreentea.bofu2016bga.Main")
}

// TODO: Gradient
class Main : PApplet() {
  val FRAME_RATE = 30f
  var minim: Minim by Delegates.notNull()
  var res: Res by Delegates.notNull()
  var input: MutableMap<Char, Int> = hashMapOf('[' to 0, ']' to 0)

  var scene1: Scene1 by Delegates.notNull()
  var scene2: Scene2 by Delegates.notNull()
  var scene3: Scene3 by Delegates.notNull()
  var scene4: Scene4 by Delegates.notNull()
  var scene5: Scene5 by Delegates.notNull()
  var scene6: Scene6 by Delegates.notNull()
  var scene7: Scene7 by Delegates.notNull()

  /*
      2
      134
     875
       6
   */
  val pieceColors = arrayOf(
          arrayOf(0x376698, 0xCCCCCC, 0x9999CC, 0x400000),
          arrayOf(0xE8E9E6, 0x38343F, 0x528E13, 0x006C6C),
          arrayOf(0x724300, 0x272A56, 0xFF9917, 0x404040),
          arrayOf(0xBB885E, 0x87766E, 0xCC9966, 0xFFCC66)
  )

  val piecePos = arrayOf(
          Pair(1, 1),
          Pair(1, 0),
          Pair(2, 1),
          Pair(3, 1),
          Pair(2, 2),
          Pair(2, 3),
          Pair(1, 2),
          Pair(0, 2),
          Pair(0, 0),
          Pair(0, 1),
          Pair(2, 0),
          Pair(3, 0),
          Pair(1, 3),
          Pair(0, 3),
          Pair(3, 2),
          Pair(3, 3)
  )

  val piecePosInv = piecePos.mapIndexed { i, e -> Pair(e, i) }.toMap()

  init {
    randomSeed(3456009L)
  }
  val pieceColorsExpanded = (0..5).map { iy -> (0..5).map { ix ->
    if(ix in 1..4 && iy in 1..4) pieceColors[iy - 1][ix - 1]
    else random(0x000000.toFloat(), 0xFFFFFF.toFloat()).toInt()
  } }

  override fun settings() {
    size(1280, 720, P3D)
  }

  override fun setup() {
    minim = Minim(this)

    res = Res(this, minim)
    scene1 = Scene1()
    scene2 = Scene2(this)
    scene3 = Scene3(this)
    scene4 = Scene4(this)
    scene5 = Scene5(this)
    scene6 = Scene6()
    scene7 = Scene7()

    /*pieceColors.forEachIndexed { iy, ey ->
      ey.forEachIndexed { ix, color ->
        val g = createGraphics(1280, 720)
        g.beginDraw()
        println(ix)
        g.tint(opaq(color))
        g.image(res.glass, 0f, 0f, 1280f, 720f, ix * 1280, iy * 720, ix * 1280 + 1280, iy * 720 + 720)
        g.endDraw()
        g.save("glass${ix + iy * 4}.png")
      }
    }*/

    colorMode(RGB, 1f, 1f, 1f, 1f)
    frameRate(FRAME_RATE)
    background(0f, 0f, 0f)
    smooth()
    strokeWeight(5f)
    keyRepeatEnabled = false
    res.music.play()

    //jump(36f)
    //jump(36f + 32f + 32f + 32f + 28f)
    //jump(36f + 32f + 32f + 32f + 32f)
    jump(36f + 32f + 32f + 32f + 32f + 32f)
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

    clear()

    // start

    scene1.draw(this, frame, beat)
    scene2.draw(this, frame, beat)
    scene3.draw(this, frame, beat)
    scene4.draw(this, frame, beat)
    scene5.draw(this, frame, beat)
    scene6.draw(this, frame, beat)
    scene7.draw(this, frame, beat)

    /*
    noStroke()
    fill(1f, 1f, 1f)
    textAlign(CENTER)
    textFont(res.font)
    textSize(96f)
    text("A Piece of Mine", 640f, 320f)
    textSize(48f)
    text("Greentea", 640f, 420f)
    */

    // end

    hint(DISABLE_DEPTH_TEST)

    noStroke()
    fill(1f, 1f, 1f)
    textFont(res.fontSans)
    textSize(12f)
    textAlign(LEFT, BOTTOM)
    text(frameRate.toString(), 10f, 710f)

    textAlign(LEFT, TOP)
    text(arrayOf("${frameCount}", "${speedTableIndex}", "${beat}").joinToString("\n"), 10f, 10f)
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
  fun cmap(value: Float, start: Float, stop: Float, min: Float, max: Float) = constrain(map(value, start, stop, min, max), min, max)

  fun inv(x: Float) = 1 - x

  fun quad1(x: Float) = -pow(x - 1, 2f) + 1f
  fun cube1(x: Float) = pow(x - 1, 3f) + 1f
  fun pow1(x: Float, p: Float) = -pow(-x + 1 , p) + 1f

  fun opaq(c: Int) = c + (0xFF shl 24)
  fun alpha(c: Int, a: Float) = c + ((a * 0xFF).toInt() shl 24)

  fun iimage(img: PImage, x: Float, y: Float) = image(img, x.toInt().toFloat(), y.toInt().toFloat())

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
}

fun PGraphics.iimage(img: PImage, x: Float, y: Float) {
  this.image(img, x.toInt().toFloat(), y.toInt().toFloat())
}
