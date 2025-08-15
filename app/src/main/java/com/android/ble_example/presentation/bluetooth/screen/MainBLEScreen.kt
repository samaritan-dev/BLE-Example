package com.android.ble_example.presentation.bluetooth.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.ble_example.domain.bluetooth.entity.BLEConnectionState
import com.android.ble_example.presentation.bluetooth.component.BLEScannerComponent
import com.android.ble_example.presentation.bluetooth.coordinator.BLECoordinator
import com.android.ble_example.presentation.bluetooth.viewmodel.BLEScannerViewModel
import com.android.ble_example.presentation.bluetooth.viewmodel.BLEConnectionViewModel

@Composable
fun MainBLEScreen(
    scannerViewModel: BLEScannerViewModel,
    connectionViewModel: BLEConnectionViewModel,
    coordinator: BLECoordinator,
    onRequestPermissions: () -> Unit
) {
    val hasPermissions by remember { mutableStateOf(coordinator.hasRequiredPermissions()) }
    
    if (!hasPermissions) {
        PermissionRequestCard(onRequestPermissions = onRequestPermissions)
        return
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Scanner Component
        BLEScannerComponent(
            viewModel = scannerViewModel,
            onDeviceSelected = { device ->
                // Handle device selection
                // This would trigger connection
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Connection Status Component
        ConnectionStatusComponent(
            viewModel = connectionViewModel
        )
    }
}

@Composable
private fun PermissionRequestCard(
    onRequestPermissions: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Permissions Required",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This app needs Bluetooth and Location permissions to scan for BLE devices.",
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRequestPermissions,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Request Permissions")
            }
        }
    }
}

@Composable
private fun ConnectionStatusComponent(
    viewModel: BLEConnectionViewModel
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val connectedDevice by viewModel.connectedDevice.collectAsState()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Connection Status",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val currentConnectionState = connectionState
            when (currentConnectionState) {
                is BLEConnectionState.Idle -> {
                    Text(
                        text = "Not connected",
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                is BLEConnectionState.Connecting -> {
                    Text(
                        text = "Connecting...",
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                is BLEConnectionState.Connected -> {
                    connectedDevice?.let { device ->
                        Text(
                            text = "Connected to: ${device.displayName}",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = device.address,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
                is BLEConnectionState.Disconnected -> {
                    Text(
                        text = "Disconnected",
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                is BLEConnectionState.ErrorWithMessage -> {
                    Text(
                        text = currentConnectionState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    Text(
                        text = "Unknown state",
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
