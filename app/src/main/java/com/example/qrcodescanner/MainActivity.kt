package com.example.qrcodescanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrcodescanner.ui.theme.QRCodeScannerTheme
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE)
//            .enableAutoZoom()
            .build()

        val scanner = GmsBarcodeScanning.getClient(this, options)
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
                            scanner.startScan()
                                .addOnSuccessListener { barcode ->

                                    scannedValue.value = barcode.rawValue
                                }
                                .addOnCanceledListener {

                                }
                                .addOnFailureListener { e ->
                                    e.printStackTrace()
                                }
                        },
                    )
                }
            }
        }
    }


}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    scannedValue: State<String?>,
    startScan: () -> Unit = {}
) {
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = { startScan() },
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(10.dp)

        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.button_qr_code))
                Text(text = stringResource(R.string.button_scan))
            }
        }

        Text(text = stringResource(R.string.title), modifier = Modifier.padding(16.dp), fontSize = 30.sp)
        Text(
            modifier = Modifier.padding(16.dp),
            text = if (scannedValue.value.isNullOrBlank())
                stringResource(R.string.empty_message)
            else
                scannedValue.value ?: stringResource(R.string.empty_message)
        )
    }
}



