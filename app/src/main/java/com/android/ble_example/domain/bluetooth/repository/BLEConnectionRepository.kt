package com.android.ble_example.domain.bluetooth.repository

import com.android.ble_example.domain.bluetooth.entity.BLEConnectionState
import com.android.ble_example.domain.bluetooth.entity.BLEScanResult
import kotlinx.coroutines.flow.Flow

interface BLEConnectionRepository {
    fun getConnectionState(): Flow<BLEConnectionState>
    suspend fun connectToDevice(device: BLEScanResult): Result<Unit>
    suspend fun disconnectDevice(): Result<Unit>
    fun isConnected(): Boolean
}
