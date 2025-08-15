package com.android.ble_example.domain.repository

import com.android.ble_example.domain.model.BLEDevice
import com.android.ble_example.domain.model.BLEService
import com.android.ble_example.domain.model.BLEState
import kotlinx.coroutines.flow.Flow

interface BLERepository {
    fun getBLEState(): Flow<BLEState>
    fun scanForDevices(): Flow<List<BLEDevice>>
    fun startScan()
    fun stopScan()
    suspend fun connectToDevice(device: BLEDevice): Result<Unit>
    suspend fun disconnectDevice(): Result<Unit>
    fun discoverServices(): Flow<List<BLEService>>
    fun isBluetoothEnabled(): Boolean
    fun requestBluetoothPermissions()
}
