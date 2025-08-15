package com.android.ble_example.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.ble_example.domain.model.BLEDevice
import com.android.ble_example.domain.model.BLEState
import com.android.ble_example.presentation.viewmodel.BLEViewModel
import com.android.ble_example.presentation.viewmodel.BLEUIState

@Composable
fun BLEScreen(
    viewModel: BLEViewModel,
    onRequestPermissions: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        BLEScreenHeader(
            bleState = uiState.bleState,
            onScanClick = { 
                if (uiState.bleState == BLEState.Scanning) {
                    viewModel.stopScan()
                } else {
                    viewModel.startScan()
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Permission Check
        if (!uiState.bleState.isPermissionsGranted()) {
            PermissionRequestCard(
                onRequestPermissions = { showPermissionDialog = true }
            )
        }
        
        // Error Display
        val currentBleState = uiState.bleState
        if (currentBleState is BLEState.ErrorWithMessage) {
            ErrorCard(
                message = currentBleState.message,
                onDismiss = { viewModel.clearError() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Connected Device Info
        uiState.connectedDevice?.let { device ->
            ConnectedDeviceCard(
                device = device,
                onDisconnect = { viewModel.disconnectDevice() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Device List
        if (uiState.discoveredDevices.isNotEmpty()) {
            Text(
                text = "Discovered Devices (${uiState.discoveredDevices.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.discoveredDevices) { device ->
                    DeviceCard(
                        device = device,
                        isConnected = device.address == uiState.connectedDevice?.address,
                        onConnect = { viewModel.connectToDevice(device) }
                    )
                }
            }
        } else if (uiState.bleState == BLEState.Scanning) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Text(
                text = "No devices found. Tap scan to start discovering BLE devices.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Services List
        if (uiState.discoveredServices.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Discovered Services (${uiState.discoveredServices.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.discoveredServices) { service ->
                    ServiceCard(service = service)
                }
            }
        }
    }
    
    // Permission Dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permissions Required") },
            text = { 
                Text("This app needs Bluetooth and Location permissions to scan for BLE devices. Please grant these permissions.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        onRequestPermissions()
                    }
                ) {
                    Text("Grant Permissions")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BLEScreenHeader(
    bleState: BLEState,
    onScanClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BLE Scanner",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = when (bleState) {
                        is BLEState.Idle -> "Ready to scan"
                        is BLEState.Scanning -> "Scanning for devices..."
                        is BLEState.Connecting -> "Connecting..."
                        is BLEState.Connected -> "Connected"
                        is BLEState.Disconnected -> "Disconnected"
                        is BLEState.Error -> "Error occurred"
                        is BLEState.ErrorWithMessage -> bleState.message
                    },
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            IconButton(
                onClick = onScanClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = when (bleState) {
                        is BLEState.Scanning -> Icons.Default.Refresh
                        is BLEState.Connected -> Icons.Default.Settings
                        else -> Icons.Default.Refresh
                    },
                    contentDescription = "Scan",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
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
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun ConnectedDeviceCard(
    device: BLEDevice,
    onDisconnect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Connected to: ${device.displayName}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = device.address,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
            }
            Button(
                onClick = onDisconnect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Disconnect")
            }
        }
    }
}

@Composable
private fun DeviceCard(
    device: BLEDevice,
    isConnected: Boolean,
    onConnect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.displayName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = device.address,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "RSSI: ${device.rssi} dBm",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isConnected) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Connected",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(onClick = onConnect) {
                    Text("Connect")
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: com.android.ble_example.domain.model.BLEService
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Service: ${service.uuid}",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Type: ${service.type}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (service.characteristics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Characteristics: ${service.characteristics.size}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Extension function to check if permissions are granted
private fun BLEState.isPermissionsGranted(): Boolean {
    return this !is BLEState.ErrorWithMessage || 
           !this.message.contains("permission", ignoreCase = true)
}
