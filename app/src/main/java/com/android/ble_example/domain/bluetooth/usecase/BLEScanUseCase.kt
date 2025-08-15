package com.android.ble_example.domain.bluetooth.usecase

import com.android.ble_example.domain.bluetooth.entity.BLEScanResult
import com.android.ble_example.domain.bluetooth.repository.BLEScannerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BLEScanUseCase @Inject constructor(
    private val scannerRepository: BLEScannerRepository
) {
    operator fun invoke(): Flow<List<BLEScanResult>> = scannerRepository.getScanResults()
    
    fun startScan() = scannerRepository.startScan()
    
    fun stopScan() = scannerRepository.stopScan()
    
    fun isScanning(): Boolean = scannerRepository.isScanning()
}
