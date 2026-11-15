package com.naandalist.pdf_tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

// Three colours: Lime is the one sharp accent, Forest Ink is the ink (text, primary buttons, the tab bar), and
// Cool Blue is the quiet second surface (the Recent card, the hero's words). Light is sage-tinted paper; dark is a
// neutral near-black. Loud surfaces are a radial glow with film grain, not a glossy linear gradient.
object Brand {
  val lime = Color(0xFF84CC16)
  val forest = Color(0xFF0E2B1F)
  val cool = Color(0xFFD7EFFF)
  val black = Color(0xFF000000) // the primary button fill
}

@Immutable
data class Palette(
  val bg: Color, // screen background
  val surface: Color, // grouped lists, inputs, dialogs
  val sunken: Color, // tiles behind artwork, pressed rows, search, segmented track
  val page: Color, // paper inside illustrations
  val ink: Color, // text and primary buttons
  val sub: Color,
  val faint: Color,
  val line: Color,
  val lineStrong: Color,
  val accent: Color,
  val accentSoft: Color,
  val accentInk: Color,
  val onInk: Color, // text on primary buttons
  val hero: List<Color>, // the loud surface: base, middle, glow
  val panel: Color, // the floating tab bar's glass
  val onPanel: Color,
  val onPanelSub: Color,
  val dark: Boolean,
)

val LightPalette = Palette(
  bg = Color(0xFFF3F6EF), surface = Color(0xFFFFFFFF), sunken = Color(0xFFE5EDDD), page = Color(0xFFFFFFFF),
  ink = Brand.forest, sub = Color(0xFF5C6B5F), faint = Color(0xFFA3AFA2), line = Color(0xFFE1E9DB),
  lineStrong = Color(0xFFC9D5C4), accent = Brand.lime, accentSoft = Color(0xFFECFCCB), accentInk = Color(0xFF3F6212),
  onInk = Color(0xFFFFFFFF),
  hero = listOf(Brand.forest, Color(0xFF3F6212), Brand.lime),
  panel = Brand.forest, onPanel = Color(0xFFFFFFFF), onPanelSub = Color(0xFFB5CBBA),
  dark = false,
)

// Dark is a neutral warm near-black (after Poetica), not a green tint: Lime is the only colour in it.
val DarkPalette = Palette(
  bg = Color(0xFF111110), surface = Color(0xFF1A1A18), sunken = Color(0xFF232321), page = Color(0xFF262624),
  ink = Color(0xFFEDEDEB), sub = Color(0xFF9C9C99), faint = Color(0xFF6B6A66), line = Color(0xFF2A2A27),
  lineStrong = Color(0xFF383834), accent = Color(0xFFA3E635), accentSoft = Color(0xFF1E2A14), accentInk = Color(0xFFD9F99D),
  onInk = Color(0xFF111110),
  hero = listOf(Color(0xFF1C1B19), Color(0xFF365314), Brand.lime), // near-black, the glow alone carries the colour
  panel = Color(0xFF1A1A18), onPanel = Color(0xFFEDEDEB), onPanelSub = Color(0xFF9C9C99),
  dark = true,
)

// The scanner is its own dark, camera-style surface in both themes: the dark palette's near-blacks, with Lime for
// detection and actions (and Forest Ink on top of it).
object ScanUi {
  val bg = Color(0xFF0B0B0A)
  val panel = Color(0xFF1A1A18)
  val raised = Color(0xFF2A2A27)
  val text = Color(0xFFFFFFFF)
  val dim = Color(0xFF9C9C99)
  val line = Color(0xFF2A2A27)
  val accent = Brand.lime
  val accentSoft = Color(0x3884CC16)
  val onAccent = Brand.forest
  val scrim = Color(0x99000000)
}

val LocalPalette = staticCompositionLocalOf { LightPalette }

/** The active palette. */
val c: Palette @Composable get() = LocalPalette.current

// Poppins throughout. It runs tall, so line heights are ~1.3x to keep accents and descenders unclipped.
val Poppins = FontFamily(
  Font(R.font.poppins_regular, FontWeight.Normal),
  Font(R.font.poppins_medium, FontWeight.Medium),
  Font(R.font.poppins_semibold, FontWeight.SemiBold),
)

object Type {
  private fun s(size: Float, weight: FontWeight, line: Float? = null, spacing: Float = 0f) = TextStyle(
    fontFamily = Poppins, fontWeight = weight, fontSize = size.sp,
    lineHeight = (line ?: (size * 1.32f)).sp, letterSpacing = spacing.sp,
  )
  val largeTitle = s(32f, FontWeight.SemiBold, 42f, -0.5f)
  val title = s(25f, FontWeight.SemiBold, 33f, -0.3f)
  val hero = s(25f, FontWeight.SemiBold, 33f, -0.4f)
  val body = s(15f, FontWeight.Normal, 22f)
  val small = s(13f, FontWeight.Normal, 19f)
  val item = s(15f, FontWeight.Medium)
  val label = s(13f, FontWeight.SemiBold)
  val button = s(15f, FontWeight.SemiBold)
  val num = s(12.5f, FontWeight.Medium)
  val med = s(13.5f, FontWeight.Medium)
  val bold = s(14f, FontWeight.SemiBold)
  val big = s(26f, FontWeight.SemiBold, 32f, -0.6f) // the reference's big numbers: sizes and counts
  val caption = s(11.5f, FontWeight.Medium, 16f, 1.4f) // uppercase section labels, spaced out
}

// Deliberately not one radius for everything: small things are tighter.
object Radius {
  val tile = 10.dp
  val control = 12.dp
  val group = 16.dp
  val card = 22.dp
  val hero = 30.dp
}

