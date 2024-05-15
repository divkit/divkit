package com.yandex.divkit.demo.utils

import android.os.Bundle
import com.google.zxing.client.android.R
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView

internal class QRCaptureActivity: CaptureActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<DecoratedBarcodeView>(R.id.zxing_barcode_scanner)?.apply {
            viewFinder.setLaserVisibility(false)
        }
    }
}
