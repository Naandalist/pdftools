package com.naandalist.pdf_tools

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.unit.Dp

// The first-launch tour, three screens in a calm, friendly style: white pages, centred bold titles, one dark pill button
// with a small line under it, and Skip on top. Welcome (sticky notes), pin the tools you want at the top of Home, and a
// look to match (light, auto or dark). Always light, whatever the app's theme. Nothing animates except the swipe.

private val DARK = Color(0xFF1C1C1B)
private val SUB = Color(0xFF9B9B9B)
private val PURPLE = Color(0xFF7A6CF2)
private val TEAL = Color(0xFF1FB58C)
private val SALMON = Color(0xFFF2735B)
private const val PAGES = 2

private val TITLE = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 31.sp, lineHeight = 38.sp, letterSpacing = (-0.8).sp)
private val SMALL = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Normal, fontSize = 11.5.sp, lineHeight = 16.sp)

@Composable
fun Onboarding(onDone: () -> Unit) {
  val themeDark = c.dark // the app's theme, read before the tour switches to the light palette
  CompositionLocalProvider(LocalPalette provides LightPalette) { OnboardingBody(themeDark, onDone) }
}

@Composable
private fun OnboardingBody(themeDark: Boolean, onDone: () -> Unit) {
  val pager = rememberPagerState { PAGES }
  val scope = rememberCoroutineScope()
  var pinned by remember { mutableStateOf(listOf<String>()) }
  val page = pager.currentPage
  val last = PAGES - 1
  val go = { to: Int -> scope.launch { pager.animateScrollToPage(to) }; Unit }
  BackHandler(page > 0) { go(page - 1) }

  // The pages are always light, so the status bar gets dark icons for as long as the tour is up.
  val view = LocalView.current
  val ctl = WindowCompat.getInsetsController((view.context as Activity).window, view)
  val darkBars = { ctl.isAppearanceLightStatusBars = true; ctl.isAppearanceLightNavigationBars = true }
  // App() sets the bar icons for the app's own theme in a LaunchedEffect that runs after this first composition, and in a
  // dark theme that leaves white icons on the white page, so set them again once it has.
  LaunchedEffect(Unit) { delay(150); darkBars() }
  DisposableEffect(Unit) {
    darkBars()
    onDispose {
      ctl.isAppearanceLightStatusBars = !themeDark
      ctl.isAppearanceLightNavigationBars = !themeDark
    }
  }

  // Finishing applies what was chosen: the pins and the look. Skip leaves everything as it was.
  val finish = {
    pinned.forEach { if (it !in Pins.ids) Pins.toggle(it) }
    onDone()
  }
  val label = listOf("Get started", "Let's go")[page]
  val line = listOf("No account needed. Everything stays on this phone.", "Pin as many as you like. Change these anytime.")[page]

  Column(Modifier.fillMaxSize().background(Color.White).statusBarsPadding().navigationBarsPadding()) {
    Box(Modifier.weight(1f).fillMaxWidth()) {
      HorizontalPager(pager, Modifier.fillMaxSize()) { i ->
        when (i) {
          0 -> PageWelcome()
          1 -> PageTools(pinned) { id -> pinned = if (id in pinned) pinned - id else pinned + id }
        }
      }
      if (page > 0) Box(Modifier.padding(start = 12.dp, top = 6.dp)) { IconBtn(I.chevronLeft, 26.dp, DARK, "Back") { go(page - 1) } }
      if (page < last) T(
        "Skip", Type.med.copy(fontSize = 14.sp), DARK,
        Modifier.align(Alignment.TopEnd).padding(end = 12.dp, top = 8.dp).tap(shape = CircleShape, scaleTo = 0.92f, label = "Skip the intro", onClick = onDone).padding(horizontal = 14.dp, vertical = 10.dp),
      )
    }
    Box(
      Modifier.padding(horizontal = 24.dp).padding(top = 12.dp).fillMaxWidth().height(58.dp)
        .tap(shape = CircleShape, scaleTo = 0.97f) { if (page < last) go(page + 1) else finish() }
        .background(DARK, CircleShape),
      contentAlignment = Alignment.Center,
    ) { T(label, Type.button, Color.White, maxLines = 1) }
    T(line, SMALL, SUB, Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 14.dp), align = TextAlign.Center)
  }
}

@Composable
private fun Title(title: String, sub: String? = null) {
  T(title, TITLE, DARK, Modifier.fillMaxWidth(), align = TextAlign.Center)
  if (sub != null) T(sub, Type.small.copy(fontSize = 13.sp), SUB, Modifier.fillMaxWidth().padding(top = 8.dp), align = TextAlign.Center)
}

// ── 1. Welcome: a fan of documents and one plain line ───────────────────────────────────────────────────────────────

@Composable
private fun PageWelcome() {
  val headline = buildAnnotatedString {
    append("Every PDF tool.\nAll on your phone.\n")
    withStyle(SpanStyle(background = Brand.lime)) { append(" All free. ") }
  }
  Column(Modifier.fillMaxSize().padding(horizontal = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Spacer(Modifier.weight(1f))
    Fan()
    Spacer(Modifier.height(40.dp))
    Stagger(3) {
      BasicText(headline, Modifier.fillMaxWidth(), TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 27.sp, lineHeight = 38.sp,
        letterSpacing = (-0.6).sp, color = DARK, textAlign = TextAlign.Center,
      ))
    }
    Stagger(4) { T("24 tools and a scanner.\nNo account, and nothing is uploaded.", SMALL, SUB, Modifier.padding(top = 14.dp), align = TextAlign.Center) }
    Spacer(Modifier.weight(1f))
    Stagger(5) {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Merge", "Split", "Scan", "Sign").forEach { label ->
          Box(Modifier.border(1.dp, c.lineStrong, CircleShape).padding(horizontal = 14.dp, vertical = 7.dp)) {
            T(label, Type.small, DARK)
          }
        }
      }
    }
    Spacer(Modifier.height(18.dp))
  }
}

// Three flat pages fanned out: cool blue and lime behind, white in front with a tag and a "done" badge.
@Composable
private fun Fan() {
  Box(Modifier.size(260.dp, 200.dp), contentAlignment = Alignment.Center) {
    Stagger(0) { FanPage(Brand.cool, -12f, (-56).dp, 6.dp, lines = listOf(0.9f, 0.7f, 0.8f, 0.55f), ink = DARK.copy(alpha = 0.16f)) }
    Stagger(1) { FanPage(Brand.lime, 11f, 58.dp, 4.dp, lines = listOf(0.85f, 0.6f, 0.75f, 0.5f), ink = Color.White.copy(alpha = 0.7f)) }
    Stagger(2) {
      FanPage(Color.White, -2f, 0.dp, 0.dp, lines = listOf(0.9f, 0.7f, 0.8f, 0.5f), ink = DARK.copy(alpha = 0.14f), border = c.lineStrong) {
        Box(Modifier.background(Brand.lime, CircleShape).padding(horizontal = 9.dp, vertical = 3.dp)) { T("PDF", Type.caption, Brand.black) }
        Spacer(Modifier.weight(1f))
        Box(Modifier.size(30.dp).background(Brand.black, CircleShape), contentAlignment = Alignment.Center) {
          Icon(I.check, 16.dp, Color.White)
        }
      }
    }
  }
}

@Composable
private fun FanPage(
  fill: Color, rot: Float, dx: Dp, dy: Dp, lines: List<Float>, ink: Color,
  border: Color? = null, top: @Composable ColumnScope.() -> Unit = {},
) {
  val shape = RoundedCornerShape(16.dp)
  Column(
    Modifier.offset(dx, dy).graphicsLayer { rotationZ = rot }.size(128.dp, 164.dp)
      .clip(shape).background(fill, shape)
      .then(if (border != null) Modifier.border(1.dp, border, shape) else Modifier)
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(9.dp),
  ) {
    top()
    lines.forEach { f -> Box(Modifier.fillMaxWidth(f).height(7.dp).background(ink, CircleShape)) }
  }
}

// ── 2. Pin your tools ───────────────────────────────────────────────────────────────────────────────────────────────

@Composable
private fun PageTools(pinned: List<String>, onToggle: (String) -> Unit) {
  val isOn = { t: Tool -> t.id in pinned || t.id in Pins.ids }
  Column(Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
    Spacer(Modifier.height(64.dp))
    Title("Pin your\nfavourite tools", "Pinned tools sit at the top of Home.")
    Spacer(Modifier.height(24.dp))
    TOOLS.chunked(2).forEach { pair ->
      Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        pair.forEach { t ->
          ToolTile(t, isOn(t), Modifier.weight(1f)) { if (t.id !in Pins.ids) onToggle(t.id) }
        }
        if (pair.size == 1) Spacer(Modifier.weight(1f))
      }
    }
    Spacer(Modifier.height(8.dp))
  }
}

// A tool as a card: lime with a black pin when pinned, white with a hairline when not.
@Composable
private fun ToolTile(t: Tool, on: Boolean, modifier: Modifier, onTap: () -> Unit) {
  val shape = RoundedCornerShape(22.dp)
  Column(
    modifier.height(138.dp)
      .clip(shape).background(if (on) Brand.lime else Color.White, shape)
      .then(if (on) Modifier else Modifier.border(1.dp, c.line, shape))
      .tap(shape = shape, scaleTo = 0.97f, label = if (on) "Unpin ${t.title}" else "Pin ${t.title}", onClick = onTap)
      .padding(14.dp),
    verticalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      ArtTile(t.id, 46.dp, bg = Color.White)
      Spacer(Modifier.weight(1f))
      Box(
        Modifier.size(26.dp).background(if (on) Brand.black else Color.Transparent, CircleShape)
          .then(if (on) Modifier else Modifier.border(1.dp, c.lineStrong, CircleShape)),
        contentAlignment = Alignment.Center,
      ) { if (on) Icon(I.pin, 14.dp, Color.White) }
    }
    T(t.title, Type.item.copy(fontSize = 14.sp, lineHeight = 19.sp), DARK, maxLines = 2)
  }
}

// ── 3. Which look suits you ─────────────────────────────────────────────────────────────────────────────────────────

/** A character with stick arms and legs: a coral diamond (winking, with a paper plane), a purple cloud or a teal star. */
@Composable
private fun Character(kind: Int, modifier: Modifier) {
  Canvas(modifier) {
    val w = size.width
    val h = size.height
    val d = density
    val line = 2.2f * d
    val cx = w / 2
    val cy = h * 0.4f
    val r = if (kind == 1) 0.2f * w * 0.62f else 0.17f * w * 0.62f
    val body = when (kind) { 0 -> SALMON; 1 -> PURPLE; else -> TEAL }
    val ink = DrawStroke(line, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val big = r * 1.55f

    // Legs, then arms with open hands.
    drawPath(Path().apply {
      moveTo(cx - big * 0.35f, cy + big * 0.85f); lineTo(cx - big * 0.5f, h - 4 * d); lineTo(cx - big * 0.85f, h - 4 * d)
      moveTo(cx + big * 0.35f, cy + big * 0.85f); lineTo(cx + big * 0.55f, h - 9 * d); lineTo(cx + big * 0.9f, h - 9 * d)
    }, DARK, style = ink)
    for (side in listOf(-1f, 1f)) {
      val hand = Offset(cx + side * big * 2.1f, cy - big * 0.45f)
      drawPath(Path().apply { moveTo(cx + side * big * 0.85f, cy + big * 0.25f); quadraticBezierTo(cx + side * big * 1.8f, cy + big * 0.55f, hand.x, hand.y) }, DARK, style = ink)
      drawCircle(Color.White, 6 * d, hand)
      drawCircle(DARK, 6 * d, hand, style = DrawStroke(1.6f * d))
      for (k in -1..1) drawLine(DARK, Offset(hand.x + k * 3.5f * d, hand.y - 5 * d), Offset(hand.x + k * 4.5f * d, hand.y - 10 * d), 1.6f * d, StrokeCap.Round)
    }

    when (kind) {
      0 -> rotate(45f, Offset(cx, cy)) { drawRoundRect(body, Offset(cx - big * 0.8f, cy - big * 0.8f), Size(big * 1.6f, big * 1.6f), CornerRadius(big * 0.5f)) }
      1 -> {
        drawRoundRect(body, Offset(cx - big, cy - big * 0.7f), Size(big * 2f, big * 1.75f), CornerRadius(big * 0.55f))
        for ((x, y, rr) in listOf(Triple(-0.55f, -0.7f, 0.5f), Triple(0f, -0.82f, 0.56f), Triple(0.55f, -0.7f, 0.5f))) drawCircle(body, rr * big, Offset(cx + x * big, cy + y * big))
      }
      else -> {
        val k = big * 1.2f
        val star = Path().apply {
          moveTo(cx, cy - k)
          quadraticBezierTo(cx + 0.45f * k, cy - 0.45f * k, cx + k, cy)
          quadraticBezierTo(cx + 0.45f * k, cy + 0.45f * k, cx, cy + k)
          quadraticBezierTo(cx - 0.45f * k, cy + 0.45f * k, cx - k, cy)
          quadraticBezierTo(cx - 0.45f * k, cy - 0.45f * k, cx, cy - k)
          close()
        }
        drawPath(star, body, style = DrawStroke(big * 0.35f, join = StrokeJoin.Round))
        drawPath(star, body)
      }
    }

    fun eye(c: Offset, rr: Float, lid: Boolean = false) {
      drawCircle(Color.White, rr, c)
      drawCircle(DARK, rr, c, style = DrawStroke(1.6f * d))
      drawCircle(DARK, rr * 0.52f, Offset(c.x + rr * 0.05f, c.y + rr * 0.1f))
      if (lid) {
        drawArc(body, 180f, 180f, true, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2))
        drawLine(DARK, Offset(c.x - rr, c.y), Offset(c.x + rr, c.y), 1.6f * d, StrokeCap.Round)
      }
    }
    fun smile(c: Offset, rr: Float) = drawPath(Path().apply { moveTo(c.x - rr, c.y); quadraticBezierTo(c.x, c.y + rr * 1.1f, c.x + rr, c.y) }, DARK, style = DrawStroke(2.4f * d, cap = StrokeCap.Round))
    when (kind) {
      0 -> {
        eye(Offset(cx - big * 0.28f, cy - big * 0.1f), big * 0.3f)
        drawPath(Path().apply { moveTo(cx + big * 0.2f, cy - big * 0.08f); quadraticBezierTo(cx + big * 0.45f, cy - big * 0.32f, cx + big * 0.7f, cy - big * 0.08f) }, DARK, style = ink)
        smile(Offset(cx + big * 0.1f, cy + big * 0.35f), big * 0.2f)
        // The paper plane, with a dotted arc over it.
        val plane = Path().apply {
          moveTo(cx + big * 3.2f, cy - big * 1.55f); lineTo(cx + big * 2.1f, cy - big * 1.0f); lineTo(cx + big * 2.55f, cy - big * 0.88f)
          lineTo(cx + big * 2.45f, cy - big * 0.45f); lineTo(cx + big * 2.8f, cy - big * 0.8f); close()
        }
        drawPath(plane, Color(0xFFFCC9BA))
        drawPath(plane, DARK, style = DrawStroke(1.5f * d, join = StrokeJoin.Round))
        drawPath(Path().apply { moveTo(cx + big * 1.5f, cy - big * 1.7f); quadraticBezierTo(cx + big * 2.5f, cy - big * 2.05f, cx + big * 3.3f, cy - big * 1.85f) }, DARK.copy(alpha = 0.5f), style = DrawStroke(1.3f * d, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 4 * d))))
      }
      1 -> {
        eye(Offset(cx - big * 0.4f, cy), big * 0.38f)
        eye(Offset(cx + big * 0.4f, cy), big * 0.38f)
        smile(Offset(cx, cy + big * 0.55f), big * 0.12f)
      }
      else -> {
        eye(Offset(cx - big * 0.36f, cy), big * 0.26f, lid = true)
        eye(Offset(cx + big * 0.36f, cy), big * 0.26f, lid = true)
        smile(Offset(cx, cy + big * 0.42f), big * 0.14f)
      }
    }
  }
}
