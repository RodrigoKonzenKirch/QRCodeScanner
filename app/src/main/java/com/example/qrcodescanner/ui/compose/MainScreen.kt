package com.example.qrcodescanner.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrcodescanner.R

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    scannedValue: State<String?>,
    isScanning: State<Boolean>,
    startScan: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.title),
                modifier = Modifier.padding(16.dp),
                fontSize = 30.sp
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = if (scannedValue.value.isNullOrBlank())
                    stringResource(R.string.empty_message)
                else
                    scannedValue.value ?: stringResource(R.string.empty_message)
            )
            Button(
                onClick = { startScan() },
                modifier = Modifier
                    .size(150.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(10.dp)

            ) {
                Text("Scan")
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = stringResource(id = R.string.button_qr_code),
//                    modifier = Modifier.size(64.dp)
//                )
            }
        }
        if (isScanning.value) {
            CircularProgressIndicator()
        }
    }
}