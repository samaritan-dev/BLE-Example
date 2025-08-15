package com.android.ble_example.domain.usecase

import com.android.ble_example.domain.model.BLEDevice
import com.android.ble_example.domain.repository.BLERepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanForDevicesUseCase @Inject constructor(
    private val bleRepository: BLERepository
) {
    operator fun invoke(): Flow<List<BLEDevice>> = bleRepository.scanForDevices()
    
    fun startScan() = bleRepository.startScan()
    
    fun stopScan() = bleRepository.stopScan()
}
