package com.android.ble_example.domain.bluetooth.usecase

import com.android.ble_example.domain.bluetooth.entity.BLEConnectionState
import com.android.ble_example.domain.bluetooth.entity.BLEScanResult
import com.android.ble_example.domain.bluetooth.repository.BLEConnectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BLEConnectionUseCase @Inject constructor(
    private val connectionRepository: BLEConnectionRepository
) {
    fun getConnectionState(): Flow<BLEConnectionState> = connectionRepository.getConnectionState()
    
    suspend fun connectToDevice(device: BLEScanResult): Result<Unit> {
        return connectionRepository.connectToDevice(device)
    }
    
    suspend fun disconnectDevice(): Result<Unit> {
        return connectionRepository.disconnectDevice()
    }
    
    fun isConnected(): Boolean = connectionRepository.isConnected()
}
