package com.android.ble_example.presentation.bluetooth.state

import com.android.ble_example.domain.bluetooth.entity.BLEConnectionState
import com.android.ble_example.domain.bluetooth.entity.BLEGattService
import com.android.ble_example.domain.bluetooth.entity.BLEScanResult

data class BLEUIState(
    val connectionState: BLEConnectionState = BLEConnectionState.Idle,
    val scanResults: List<BLEScanResult> = emptyList(),
    val discoveredServices: List<BLEGattService> = emptyList(),
    val connectedDevice: BLEScanResult? = null,
    val isScanning: Boolean = false,
    val hasPermissions: Boolean = false
)
