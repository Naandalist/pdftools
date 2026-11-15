package com.naandalist.pdf_tools

import android.view.LayoutInflater
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView as NativeAdViewAndroid

const val ADMOB_NATIVE_AD_UNIT = "ca-app-pub-1316297860279927/3715278401"

@Composable
fun NativeAdView(modifier: Modifier = Modifier) {
  val palette = LocalPalette.current
  val shape = RoundedCornerShape(Radius.group)
  Box(
    modifier
      .fillMaxWidth()
      .height(200.dp)
      .clip(shape)
      .background(palette.surface, shape)
  ) {
    AndroidView(
      factory = { context ->
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.ad_native, null) as NativeAdViewAndroid
      },
      modifier = Modifier.fillMaxWidth(),
      update = { adView ->
        applyThemeColors(adView, palette)
        val adRequest = AdRequest.Builder().build()
        val adLoader = com.google.android.gms.ads.AdLoader.Builder(appContext, ADMOB_NATIVE_AD_UNIT)
          .forNativeAd { nativeAd ->
            populateNativeAdView(nativeAd, adView, palette)
          }
          .withAdListener(object : com.google.android.gms.ads.AdListener() {
            override fun onAdFailedToLoad(p0: com.google.android.gms.ads.LoadAdError) {
              super.onAdFailedToLoad(p0)
            }
          })
          .build()
        adLoader.loadAd(adRequest)
      }
    )
  }
}

private fun applyThemeColors(adView: NativeAdViewAndroid, palette: Palette) {
  adView.setBackgroundColor(palette.surface.hashCode())

  val labelView = adView.findViewById<android.widget.TextView>(R.id.ad_label)
  labelView?.setTextColor(palette.faint.hashCode())

  val headlineView = adView.findViewById<android.widget.TextView>(R.id.ad_headline)
  headlineView?.setTextColor(palette.ink.hashCode())

  val bodyView = adView.findViewById<android.widget.TextView>(R.id.ad_body)
  bodyView?.setTextColor(palette.sub.hashCode())

  val ctaView = adView.findViewById<android.widget.Button>(R.id.ad_call_to_action)
  ctaView?.setTextColor(palette.onInk.hashCode())
  ctaView?.setBackgroundColor(palette.ink.hashCode())
}

private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdViewAndroid, palette: Palette) {
  adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)
  adView.headlineView = adView.findViewById(R.id.ad_headline)
  adView.bodyView = adView.findViewById(R.id.ad_body)
  adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
  adView.iconView = adView.findViewById(R.id.ad_app_icon)

  (adView.headlineView as? android.widget.TextView)?.text = nativeAd.headline
  (adView.bodyView as? android.widget.TextView)?.text = nativeAd.body
  (adView.callToActionView as? android.widget.Button)?.text = nativeAd.callToAction

  nativeAd.icon?.let { icon ->
    (adView.iconView as? android.widget.ImageView)?.setImageDrawable(icon.drawable)
  }

  adView.setNativeAd(nativeAd)
}
