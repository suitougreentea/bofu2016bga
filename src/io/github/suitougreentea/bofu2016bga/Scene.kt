package io.github.suitougreentea.bofu2016bga

interface Scene {
  val startBeat: Float

  fun draw(app: Main, frame: Int, beat: Float)

  fun createBNorm(app: Main, beat: Float): (start: Float, end: Float) -> Float = { start, end -> app.cnorm(beat, startBeat + start, startBeat + end) }
  fun createBNorm0(app: Main, beat: Float): (start: Float, end: Float) -> Float = { start, end -> app.norm0(beat, startBeat + start, startBeat + end) }
  fun createBNorm1(app: Main, beat: Float): (start: Float, end: Float) -> Float = { start, end -> app.norm1(beat, startBeat + start, startBeat + end) }
  fun createBIn(app: Main, beat: Float): (start: Float, end: Float) -> Boolean = { start, end -> beat in startBeat + start..startBeat + end }
}