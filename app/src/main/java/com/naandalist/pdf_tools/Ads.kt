package com.naandalist.pdf_tools

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

const val ADMOB_NATIVE_AD_UNIT = "ca-app-pub-1316297860279927/3715278401"

// No fixed height: AdMob rejects any asset laid out past the NativeAdView's bounds, so the card wraps its content.
@Composable
fun NativeAdCard(modifier: Modifier = Modifier) {
  var ad by remember { mutableStateOf<NativeAd?>(null) }
  DisposableEffect(Unit) {
    var disposed = false
    AdLoader.Builder(appContext, ADMOB_NATIVE_AD_UNIT)
      .forNativeAd { if (disposed) it.destroy() else ad = it }
      .build()
      .loadAd(AdRequest.Builder().build())
    onDispose { disposed = true; ad?.destroy() }
  }
  val loaded = ad ?: return
  val palette = c
  AndroidView(
    factory = { ctx -> LayoutInflater.from(ctx).inflate(R.layout.ad_native, null) as NativeAdView },
    modifier = modifier.fillMaxWidth().background(palette.surface, RoundedCornerShape(Radius.group)),
    update = { bind(it, loaded, palette) },
  )
}

private fun bind(view: NativeAdView, ad: NativeAd, p: Palette) {
  val label = view.findViewById<TextView>(R.id.ad_label)
  val headline = view.findViewById<TextView>(R.id.ad_headline)
  val body = view.findViewById<TextView>(R.id.ad_body)
  val icon = view.findViewById<ImageView>(R.id.ad_app_icon)
  val cta = view.findViewById<Button>(R.id.ad_call_to_action)

  label.setTextColor(p.faint.toArgb())
  headline.setTextColor(p.ink.toArgb())
  body.setTextColor(p.sub.toArgb())
  cta.setTextColor(p.onInk.toArgb())
  cta.backgroundTintList = ColorStateList.valueOf(p.ink.toArgb())

  headline.text = ad.headline
  body.text = ad.body
  body.visibility = if (ad.body == null) View.GONE else View.VISIBLE
  icon.setImageDrawable(ad.icon?.drawable)
  icon.visibility = if (ad.icon == null) View.GONE else View.VISIBLE
  cta.text = ad.callToAction
  cta.visibility = if (ad.callToAction == null) View.GONE else View.VISIBLE

  view.headlineView = headline
  view.bodyView = body
  view.iconView = icon
  view.callToActionView = cta
  view.mediaView = view.findViewById(R.id.ad_media)
  view.setNativeAd(ad)
}
