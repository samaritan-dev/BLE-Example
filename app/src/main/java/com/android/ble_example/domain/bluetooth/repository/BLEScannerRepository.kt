package com.android.ble_example.domain.bluetooth.repository

import com.android.ble_example.domain.bluetooth.entity.BLEScanResult
import kotlinx.coroutines.flow.Flow

interface BLEScannerRepository {
    fun getScanResults(): Flow<List<BLEScanResult>>
    fun startScan()
    fun stopScan()
    fun isScanning(): Boolean
}
