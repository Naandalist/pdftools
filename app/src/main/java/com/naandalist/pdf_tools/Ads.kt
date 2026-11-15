package com.naandalist.pdf_tools

import android.view.LayoutInflater
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

const val ADMOB_NATIVE_AD_UNIT = "ca-app-pub-1316297860279927/3715278401"

@Composable
fun NativeAdView(modifier: Modifier = Modifier) {
  Box(modifier.fillMaxWidth().height(280.dp).padding(horizontal = 12.dp, vertical = 12.dp)) {
    AndroidView(
      factory = { context ->
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.ad_native, null) as NativeAdView
      },
      modifier = Modifier.fillMaxWidth(),
      update = { adView ->
        val adRequest = AdRequest.Builder().build()
        val adLoader = com.google.android.gms.ads.AdLoader.Builder(appContext, ADMOB_NATIVE_AD_UNIT)
          .forNativeAd { nativeAd ->
            populateNativeAdView(nativeAd, adView)
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

private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
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
