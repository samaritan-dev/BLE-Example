package com.android.ble_example.domain.bluetooth.usecase

import com.android.ble_example.domain.bluetooth.entity.BLEGattService
import com.android.ble_example.domain.bluetooth.repository.BLEGattRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BLEGattUseCase @Inject constructor(
    private val gattRepository: BLEGattRepository
) {
    fun getDiscoveredServices(): Flow<List<BLEGattService>> = gattRepository.getDiscoveredServices()
    
    suspend fun discoverServices(): Result<Unit> = gattRepository.discoverServices()
    
    suspend fun readCharacteristic(serviceUuid: String, characteristicUuid: String): Result<ByteArray?> {
        return gattRepository.readCharacteristic(serviceUuid, characteristicUuid)
    }
    
    suspend fun writeCharacteristic(serviceUuid: String, characteristicUuid: String, value: ByteArray): Result<Unit> {
        return gattRepository.writeCharacteristic(serviceUuid, characteristicUuid, value)
    }
}
