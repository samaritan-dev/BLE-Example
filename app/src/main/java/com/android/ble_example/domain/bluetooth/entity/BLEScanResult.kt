package com.android.ble_example.domain.bluetooth.entity

import android.bluetooth.BluetoothDevice

data class BLEScanResult(
    val device: BluetoothDevice,
    val name: String?,
    val address: String,
    val rssi: Int,
    val isConnected: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis()
) {
    val displayName: String
        get() = name ?: address
}
