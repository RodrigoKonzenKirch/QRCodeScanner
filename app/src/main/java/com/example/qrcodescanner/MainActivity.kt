package com.example.qrcodescanner

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.qrcodescanner.ui.compose.MainScreen
import com.example.qrcodescanner.ui.theme.QRCodeScannerTheme
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val context = this.applicationContext

        setContent {
            QRCodeScannerTheme {

                val scannedValue: MutableState<String?> = remember {
                    mutableStateOf("")
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),

                        scannedValue = scannedValue,
                        startScan = {
                            if (isModuleAvailable(context)) {
                                scanQRCode(context, scannedValue)
                            } else {
                                val moduleInstall = ModuleInstall.getClient(context)
                                val moduleInstallRequest = ModuleInstallRequest.newBuilder()
                                    .addApi(GmsBarcodeScanning.getClient(context))
                                    .build()

                                moduleInstall.installModules(moduleInstallRequest)
                                    .addOnSuccessListener { response ->
                                        if (response.areModulesAlreadyInstalled()) {
                                            // Module already installed, proceed with scanning
                                            scanQRCode(context, scannedValue)
                                        } else {
                                            // Module was just installed, wait briefly then scan
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                scanQRCode(context, scannedValue)
                                            }, 1000)
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle installation failure
                                    }
                            }
                        },
                    )
                }
            }
        }
    }
}

fun isModuleAvailable(context: Context): Boolean {
    val gmsBarcodeScanner = GmsBarcodeScanning.getClient(context)
    val moduleInstall = ModuleInstall.getClient(context)
    var isAvailable = false

    moduleInstall.areModulesAvailable(gmsBarcodeScanner).addOnSuccessListener {
        isAvailable = it.areModulesAvailable()
    }

    return isAvailable
}

fun scanQRCode(context: Context, scannedValue: MutableState<String?>) {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE
        )
        .enableAutoZoom()
        .build()

    val scanner = GmsBarcodeScanning.getClient(context, options)
    scanner.startScan()
        .addOnSuccessListener { barcode ->
            scannedValue.value = barcode.rawValue.toString()
        }
        .addOnCanceledListener {
            scannedValue.value = "Scan Cancelled"
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            scannedValue.value = "Scan Failed\n Error: ${e.message}"
        }
}



