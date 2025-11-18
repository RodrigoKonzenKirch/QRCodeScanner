package com.example.qrcodescanner

import android.content.Context
import android.os.Bundle
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
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QRCodeScannerTheme {
                val scannedValue: MutableState<String?> = remember {
                    mutableStateOf("")
                }
                val isScanning: MutableState<Boolean> = remember {
                    mutableStateOf(false)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        scannedValue = scannedValue,
                        isScanning = isScanning,
                        startScan = { scanQRCode(scannedValue, isScanning) },
                    )
                }
            }
        }
    }

    private fun scanQRCode(scannedValue: MutableState<String?>, isScanning: MutableState<Boolean>) {
        isScanning.value = true
        val context: Context = this.applicationContext
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
        val scanner = GmsBarcodeScanning.getClient(context, options)
        val moduleInstallClient = ModuleInstall.getClient(context)

        moduleInstallClient
            .areModulesAvailable(scanner)
            .addOnSuccessListener {
                if (it.areModulesAvailable()) {
                    startScanning(scanner, scannedValue, isScanning)
                } else {
                    val installRequest = ModuleInstallRequest.newBuilder().addApi(scanner).build()
                    moduleInstallClient
                        .installModules(installRequest)
                        .addOnSuccessListener { startScanning(scanner, scannedValue, isScanning) }
                        .addOnFailureListener {
                            scannedValue.value = "Module installation failed"
                            isScanning.value = false
                        }
                }
            }
            .addOnFailureListener {
                scannedValue.value = "Could not check for module availability"
                isScanning.value = false
            }
    }

    private fun startScanning(
        scanner: GmsBarcodeScanner,
        scannedValue: MutableState<String?>,
        isScanning: MutableState<Boolean>
    ) {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                scannedValue.value = barcode.rawValue
            }
            .addOnCanceledListener {
                scannedValue.value = "Scan Cancelled"
            }
            .addOnFailureListener { e ->
                scannedValue.value = "Scan Failed: ${e.message}"
            }
            .addOnCompleteListener { isScanning.value = false }
    }
}
