package io.github.suitougreentea.bofu2016bga

import ddf.minim.Minim
import processing.core.PApplet
import processing.core.PImage
import processing.sound.SoundFile

class Res(app: PApplet, minim: Minim) {
  val music = minim.loadFile("music.mp3")
  val font = app.createFont("Windsong", 128f)
  val fontSans = app.createFont("Open Sans", 12f)
  val pieceBg = (1..16).map { app.loadImage("piece${it}.png") }
  val piece = app.loadImage("piece.png")
  val pieceNumber: List<PImage>
  init {
    val base = app.loadImage("piecenumber.png")
    val h = base.height / 8
    pieceNumber = (0..7).map { base.get(0, h * it, base.width, h) }
  }
  val line = app.loadImage("line.png")
  val conversation1: List<PImage>
  val conversation2: List<PImage>
  init {
    val base = app.loadImage("conversation.png")
    val h = base.height / 16
    conversation1 = (0..7).map { base.get(0, h * 2 * it, base.width, h) }
    conversation2 = (0..7).map { base.get(0, h * 2 * it + h, base.width, h) }
  }
  val glass = (0..15).map { app.loadImage("glass${it}.png") }
  val glassxy = (0..3).map { iy -> (0..3).map { ix -> glass[ix + iy * 4] } }
}

