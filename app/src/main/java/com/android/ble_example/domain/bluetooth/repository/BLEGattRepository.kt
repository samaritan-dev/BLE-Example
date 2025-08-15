package com.android.ble_example.domain.bluetooth.repository

import com.android.ble_example.domain.bluetooth.entity.BLEGattService
import kotlinx.coroutines.flow.Flow

interface BLEGattRepository {
    fun getDiscoveredServices(): Flow<List<BLEGattService>>
    suspend fun discoverServices(): Result<Unit>
    suspend fun readCharacteristic(serviceUuid: String, characteristicUuid: String): Result<ByteArray?>
    suspend fun writeCharacteristic(serviceUuid: String, characteristicUuid: String, value: ByteArray): Result<Unit>
}
